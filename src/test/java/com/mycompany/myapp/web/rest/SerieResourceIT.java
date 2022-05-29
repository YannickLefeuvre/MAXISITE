package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Serie;
import com.mycompany.myapp.repository.SerieRepository;
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
 * Integration tests for the {@link SerieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SerieResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGES = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGES = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGES_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGES_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/series";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSerieMockMvc;

    private Serie serie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Serie createEntity(EntityManager em) {
        Serie serie = new Serie()
            .nom(DEFAULT_NOM)
            .images(DEFAULT_IMAGES)
            .imagesContentType(DEFAULT_IMAGES_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return serie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Serie createUpdatedEntity(EntityManager em) {
        Serie serie = new Serie()
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return serie;
    }

    @BeforeEach
    public void initTest() {
        serie = createEntity(em);
    }

    @Test
    @Transactional
    void createSerie() throws Exception {
        int databaseSizeBeforeCreate = serieRepository.findAll().size();
        // Create the Serie
        restSerieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serie)))
            .andExpect(status().isCreated());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeCreate + 1);
        Serie testSerie = serieList.get(serieList.size() - 1);
        assertThat(testSerie.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testSerie.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testSerie.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testSerie.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSerieWithExistingId() throws Exception {
        // Create the Serie with an existing ID
        serie.setId(1L);

        int databaseSizeBeforeCreate = serieRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSerieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serie)))
            .andExpect(status().isBadRequest());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = serieRepository.findAll().size();
        // set the field null
        serie.setNom(null);

        // Create the Serie, which fails.

        restSerieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serie)))
            .andExpect(status().isBadRequest());

        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSeries() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        // Get all the serieList
        restSerieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serie.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].imagesContentType").value(hasItem(DEFAULT_IMAGES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].images").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGES))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSerie() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        // Get the serie
        restSerieMockMvc
            .perform(get(ENTITY_API_URL_ID, serie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serie.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.imagesContentType").value(DEFAULT_IMAGES_CONTENT_TYPE))
            .andExpect(jsonPath("$.images").value(Base64Utils.encodeToString(DEFAULT_IMAGES)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSerie() throws Exception {
        // Get the serie
        restSerieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSerie() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        int databaseSizeBeforeUpdate = serieRepository.findAll().size();

        // Update the serie
        Serie updatedSerie = serieRepository.findById(serie.getId()).get();
        // Disconnect from session so that the updates on updatedSerie are not directly saved in db
        em.detach(updatedSerie);
        updatedSerie
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restSerieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSerie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSerie))
            )
            .andExpect(status().isOk());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
        Serie testSerie = serieList.get(serieList.size() - 1);
        assertThat(testSerie.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testSerie.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testSerie.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testSerie.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSerieWithPatch() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        int databaseSizeBeforeUpdate = serieRepository.findAll().size();

        // Update the serie using partial update
        Serie partialUpdatedSerie = new Serie();
        partialUpdatedSerie.setId(serie.getId());

        partialUpdatedSerie.images(UPDATED_IMAGES).imagesContentType(UPDATED_IMAGES_CONTENT_TYPE);

        restSerieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSerie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSerie))
            )
            .andExpect(status().isOk());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
        Serie testSerie = serieList.get(serieList.size() - 1);
        assertThat(testSerie.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testSerie.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testSerie.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testSerie.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSerieWithPatch() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        int databaseSizeBeforeUpdate = serieRepository.findAll().size();

        // Update the serie using partial update
        Serie partialUpdatedSerie = new Serie();
        partialUpdatedSerie.setId(serie.getId());

        partialUpdatedSerie
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restSerieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSerie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSerie))
            )
            .andExpect(status().isOk());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
        Serie testSerie = serieList.get(serieList.size() - 1);
        assertThat(testSerie.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testSerie.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testSerie.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testSerie.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serie.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSerie() throws Exception {
        int databaseSizeBeforeUpdate = serieRepository.findAll().size();
        serie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerieMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(serie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Serie in the database
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSerie() throws Exception {
        // Initialize the database
        serieRepository.saveAndFlush(serie);

        int databaseSizeBeforeDelete = serieRepository.findAll().size();

        // Delete the serie
        restSerieMockMvc
            .perform(delete(ENTITY_API_URL_ID, serie.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Serie> serieList = serieRepository.findAll();
        assertThat(serieList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
