package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Livre;
import com.mycompany.myapp.repository.LivreRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link LivreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LivreResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGES = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGES = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGES_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGES_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/livres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLivreMockMvc;

    private Livre livre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createEntity(EntityManager em) {
        Livre livre = new Livre()
            .nom(DEFAULT_NOM)
            .images(DEFAULT_IMAGES)
            .imagesContentType(DEFAULT_IMAGES_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return livre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createUpdatedEntity(EntityManager em) {
        Livre livre = new Livre()
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return livre;
    }

    @BeforeEach
    public void initTest() {
        livre = createEntity(em);
    }

    @Test
    @Transactional
    void createLivre() throws Exception {
        int databaseSizeBeforeCreate = livreRepository.findAll().size();
        // Create the Livre
        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isCreated());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeCreate + 1);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testLivre.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testLivre.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testLivre.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createLivreWithExistingId() throws Exception {
        // Create the Livre with an existing ID
        livre.setId(1L);

        int databaseSizeBeforeCreate = livreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = livreRepository.findAll().size();
        // set the field null
        livre.setNom(null);

        // Create the Livre, which fails.

        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isBadRequest());

        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLivres() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        // Get all the livreList
        restLivreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livre.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].imagesContentType").value(hasItem(DEFAULT_IMAGES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].images").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGES))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getLivre() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        // Get the livre
        restLivreMockMvc
            .perform(get(ENTITY_API_URL_ID, livre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livre.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.imagesContentType").value(DEFAULT_IMAGES_CONTENT_TYPE))
            .andExpect(jsonPath("$.images").value(Base64Utils.encodeToString(DEFAULT_IMAGES)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingLivre() throws Exception {
        // Get the livre
        restLivreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLivre() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre
        Livre updatedLivre = livreRepository.findById(livre.getId()).get();
        // Disconnect from session so that the updates on updatedLivre are not directly saved in db
        em.detach(updatedLivre);
        updatedLivre
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLivre.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLivre.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testLivre.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testLivre.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livre.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre.images(UPDATED_IMAGES).imagesContentType(UPDATED_IMAGES_CONTENT_TYPE).description(UPDATED_DESCRIPTION);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testLivre.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testLivre.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testLivre.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLivre.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testLivre.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testLivre.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLivre() throws Exception {
        // Initialize the database
        livreRepository.saveAndFlush(livre);

        int databaseSizeBeforeDelete = livreRepository.findAll().size();

        // Delete the livre
        restLivreMockMvc
            .perform(delete(ENTITY_API_URL_ID, livre.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
