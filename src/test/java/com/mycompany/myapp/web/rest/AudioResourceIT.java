package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Audio;
import com.mycompany.myapp.repository.AudioRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AudioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AudioResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audio";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AudioRepository audioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAudioMockMvc;

    private Audio audio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Audio createEntity(EntityManager em) {
        Audio audio = new Audio().nom(DEFAULT_NOM).url(DEFAULT_URL);
        return audio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Audio createUpdatedEntity(EntityManager em) {
        Audio audio = new Audio().nom(UPDATED_NOM).url(UPDATED_URL);
        return audio;
    }

    @BeforeEach
    public void initTest() {
        audio = createEntity(em);
    }

    @Test
    @Transactional
    void createAudio() throws Exception {
        int databaseSizeBeforeCreate = audioRepository.findAll().size();
        // Create the Audio
        restAudioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(audio)))
            .andExpect(status().isCreated());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeCreate + 1);
        Audio testAudio = audioList.get(audioList.size() - 1);
        assertThat(testAudio.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAudio.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createAudioWithExistingId() throws Exception {
        // Create the Audio with an existing ID
        audio.setId(1L);

        int databaseSizeBeforeCreate = audioRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAudioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(audio)))
            .andExpect(status().isBadRequest());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = audioRepository.findAll().size();
        // set the field null
        audio.setNom(null);

        // Create the Audio, which fails.

        restAudioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(audio)))
            .andExpect(status().isBadRequest());

        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAudio() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        // Get all the audioList
        restAudioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audio.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getAudio() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        // Get the audio
        restAudioMockMvc
            .perform(get(ENTITY_API_URL_ID, audio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(audio.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingAudio() throws Exception {
        // Get the audio
        restAudioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAudio() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        int databaseSizeBeforeUpdate = audioRepository.findAll().size();

        // Update the audio
        Audio updatedAudio = audioRepository.findById(audio.getId()).get();
        // Disconnect from session so that the updates on updatedAudio are not directly saved in db
        em.detach(updatedAudio);
        updatedAudio.nom(UPDATED_NOM).url(UPDATED_URL);

        restAudioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAudio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAudio))
            )
            .andExpect(status().isOk());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
        Audio testAudio = audioList.get(audioList.size() - 1);
        assertThat(testAudio.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAudio.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, audio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(audio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(audio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(audio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAudioWithPatch() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        int databaseSizeBeforeUpdate = audioRepository.findAll().size();

        // Update the audio using partial update
        Audio partialUpdatedAudio = new Audio();
        partialUpdatedAudio.setId(audio.getId());

        partialUpdatedAudio.nom(UPDATED_NOM);

        restAudioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAudio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAudio))
            )
            .andExpect(status().isOk());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
        Audio testAudio = audioList.get(audioList.size() - 1);
        assertThat(testAudio.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAudio.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void fullUpdateAudioWithPatch() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        int databaseSizeBeforeUpdate = audioRepository.findAll().size();

        // Update the audio using partial update
        Audio partialUpdatedAudio = new Audio();
        partialUpdatedAudio.setId(audio.getId());

        partialUpdatedAudio.nom(UPDATED_NOM).url(UPDATED_URL);

        restAudioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAudio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAudio))
            )
            .andExpect(status().isOk());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
        Audio testAudio = audioList.get(audioList.size() - 1);
        assertThat(testAudio.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAudio.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, audio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(audio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(audio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAudio() throws Exception {
        int databaseSizeBeforeUpdate = audioRepository.findAll().size();
        audio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAudioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(audio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Audio in the database
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAudio() throws Exception {
        // Initialize the database
        audioRepository.saveAndFlush(audio);

        int databaseSizeBeforeDelete = audioRepository.findAll().size();

        // Delete the audio
        restAudioMockMvc
            .perform(delete(ENTITY_API_URL_ID, audio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Audio> audioList = audioRepository.findAll();
        assertThat(audioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
