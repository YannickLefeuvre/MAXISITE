package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Film;
import com.mycompany.myapp.repository.FilmRepository;
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
 * Integration tests for the {@link FilmResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FilmResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGES = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGES = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGES_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGES_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/films";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFilmMockMvc;

    private Film film;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createEntity(EntityManager em) {
        Film film = new Film()
            .nom(DEFAULT_NOM)
            .images(DEFAULT_IMAGES)
            .imagesContentType(DEFAULT_IMAGES_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return film;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createUpdatedEntity(EntityManager em) {
        Film film = new Film()
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return film;
    }

    @BeforeEach
    public void initTest() {
        film = createEntity(em);
    }

    @Test
    @Transactional
    void createFilm() throws Exception {
        int databaseSizeBeforeCreate = filmRepository.findAll().size();
        // Create the Film
        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(film)))
            .andExpect(status().isCreated());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate + 1);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testFilm.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testFilm.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testFilm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createFilmWithExistingId() throws Exception {
        // Create the Film with an existing ID
        film.setId(1L);

        int databaseSizeBeforeCreate = filmRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(film)))
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().size();
        // set the field null
        film.setNom(null);

        // Create the Film, which fails.

        restFilmMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(film)))
            .andExpect(status().isBadRequest());

        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFilms() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get all the filmList
        restFilmMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(film.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].imagesContentType").value(hasItem(DEFAULT_IMAGES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].images").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGES))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        // Get the film
        restFilmMockMvc
            .perform(get(ENTITY_API_URL_ID, film.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(film.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.imagesContentType").value(DEFAULT_IMAGES_CONTENT_TYPE))
            .andExpect(jsonPath("$.images").value(Base64Utils.encodeToString(DEFAULT_IMAGES)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingFilm() throws Exception {
        // Get the film
        restFilmMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film
        Film updatedFilm = filmRepository.findById(film.getId()).get();
        // Disconnect from session so that the updates on updatedFilm are not directly saved in db
        em.detach(updatedFilm);
        updatedFilm.nom(UPDATED_NOM).images(UPDATED_IMAGES).imagesContentType(UPDATED_IMAGES_CONTENT_TYPE).description(UPDATED_DESCRIPTION);

        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFilm.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFilm))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testFilm.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testFilm.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, film.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(film))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(film))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(film)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm.description(UPDATED_DESCRIPTION);

        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testFilm.getImages()).isEqualTo(DEFAULT_IMAGES);
        assertThat(testFilm.getImagesContentType()).isEqualTo(DEFAULT_IMAGES_CONTENT_TYPE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeUpdate = filmRepository.findAll().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm
            .nom(UPDATED_NOM)
            .images(UPDATED_IMAGES)
            .imagesContentType(UPDATED_IMAGES_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            )
            .andExpect(status().isOk());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testFilm.getImages()).isEqualTo(UPDATED_IMAGES);
        assertThat(testFilm.getImagesContentType()).isEqualTo(UPDATED_IMAGES_CONTENT_TYPE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, film.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(film))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(film))
            )
            .andExpect(status().isBadRequest());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().size();
        film.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilmMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(film)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFilm() throws Exception {
        // Initialize the database
        filmRepository.saveAndFlush(film);

        int databaseSizeBeforeDelete = filmRepository.findAll().size();

        // Delete the film
        restFilmMockMvc
            .perform(delete(ENTITY_API_URL_ID, film.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Film> filmList = filmRepository.findAll();
        assertThat(filmList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
