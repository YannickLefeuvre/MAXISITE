package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.AlbumPhoto;
import com.mycompany.myapp.repository.AlbumPhotoRepository;
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
 * Integration tests for the {@link AlbumPhotoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlbumPhotoResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGES = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGES = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGES_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGES_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/album-photos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AlbumPhotoRepository albumPhotoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlbumPhotoMockMvc;

    private AlbumPhoto albumPhoto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlbumPhoto createEntity(EntityManager em) {
        AlbumPhoto albumPhoto = new AlbumPhoto()
            .nom(DEFAULT_NOM)
            .images(DEFAULT_IMAGES)
            .imagesContentType(DEFAULT_IMAGES_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return albumPhoto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlbumPhoto createUpdatedEntity(EntityManager em) {
        AlbumPhoto albumPhoto = new AlbumPhoto()
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return albumPhoto;
    }

    @BeforeEach
    public void initTest() {
        albumPhoto = createEntity(em);
    }

    @Test
    @Transactional
    void createAlbumPhoto() throws Exception {
        int databaseSizeBeforeCreate = albumPhotoRepository.findAll().size();
        // Create the AlbumPhoto
        restAlbumPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(albumPhoto)))
            .andExpect(status().isCreated());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeCreate + 1);
        AlbumPhoto testAlbumPhoto = albumPhotoList.get(albumPhotoList.size() - 1);
        assertThat(testAlbumPhoto.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAlbumPhoto.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testAlbumPhoto.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testAlbumPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createAlbumPhotoWithExistingId() throws Exception {
        // Create the AlbumPhoto with an existing ID
        albumPhoto.setId(1L);

        int databaseSizeBeforeCreate = albumPhotoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlbumPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(albumPhoto)))
            .andExpect(status().isBadRequest());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = albumPhotoRepository.findAll().size();
        // set the field null
        albumPhoto.setNom(null);

        // Create the AlbumPhoto, which fails.

        restAlbumPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(albumPhoto)))
            .andExpect(status().isBadRequest());

        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlbumPhotos() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        // Get all the albumPhotoList
        restAlbumPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(albumPhoto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].imagesContentType").value(hasItem(DEFAULT_IMAGES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].images").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGES))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAlbumPhoto() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        // Get the albumPhoto
        restAlbumPhotoMockMvc
            .perform(get(ENTITY_API_URL_ID, albumPhoto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(albumPhoto.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.imagesContentType").value(DEFAULT_IMAGES_CONTENT_TYPE))
            .andExpect(jsonPath("$.images").value(Base64Utils.encodeToString(DEFAULT_IMAGES)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAlbumPhoto() throws Exception {
        // Get the albumPhoto
        restAlbumPhotoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAlbumPhoto() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();

        // Update the albumPhoto
        AlbumPhoto updatedAlbumPhoto = albumPhotoRepository.findById(albumPhoto.getId()).get();
        // Disconnect from session so that the updates on updatedAlbumPhoto are not directly saved in db
        em.detach(updatedAlbumPhoto);
        updatedAlbumPhoto
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restAlbumPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAlbumPhoto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAlbumPhoto))
            )
            .andExpect(status().isOk());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
        AlbumPhoto testAlbumPhoto = albumPhotoList.get(albumPhotoList.size() - 1);
        assertThat(testAlbumPhoto.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAlbumPhoto.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testAlbumPhoto.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testAlbumPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, albumPhoto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(albumPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(albumPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(albumPhoto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlbumPhotoWithPatch() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();

        // Update the albumPhoto using partial update
        AlbumPhoto partialUpdatedAlbumPhoto = new AlbumPhoto();
        partialUpdatedAlbumPhoto.setId(albumPhoto.getId());

        restAlbumPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbumPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbumPhoto))
            )
            .andExpect(status().isOk());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
        AlbumPhoto testAlbumPhoto = albumPhotoList.get(albumPhotoList.size() - 1);
        assertThat(testAlbumPhoto.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAlbumPhoto.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testAlbumPhoto.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testAlbumPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAlbumPhotoWithPatch() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();

        // Update the albumPhoto using partial update
        AlbumPhoto partialUpdatedAlbumPhoto = new AlbumPhoto();
        partialUpdatedAlbumPhoto.setId(albumPhoto.getId());

        partialUpdatedAlbumPhoto
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restAlbumPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbumPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbumPhoto))
            )
            .andExpect(status().isOk());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
        AlbumPhoto testAlbumPhoto = albumPhotoList.get(albumPhotoList.size() - 1);
        assertThat(testAlbumPhoto.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAlbumPhoto.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testAlbumPhoto.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testAlbumPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, albumPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(albumPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(albumPhoto))
            )
            .andExpect(status().isBadRequest());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlbumPhoto() throws Exception {
        int databaseSizeBeforeUpdate = albumPhotoRepository.findAll().size();
        albumPhoto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(albumPhoto))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AlbumPhoto in the database
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlbumPhoto() throws Exception {
        // Initialize the database
        albumPhotoRepository.saveAndFlush(albumPhoto);

        int databaseSizeBeforeDelete = albumPhotoRepository.findAll().size();

        // Delete the albumPhoto
        restAlbumPhotoMockMvc
            .perform(delete(ENTITY_API_URL_ID, albumPhoto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AlbumPhoto> albumPhotoList = albumPhotoRepository.findAll();
        assertThat(albumPhotoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
