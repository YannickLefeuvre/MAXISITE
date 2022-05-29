package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Maison;
import com.mycompany.myapp.repository.MaisonRepository;
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
 * Integration tests for the {@link MaisonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MaisonResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ICONE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ICONE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ICONE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ICONE_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_ABSISCE = 1;
    private static final Integer UPDATED_ABSISCE = 2;

    private static final Integer DEFAULT_ORDONNEE = 1;
    private static final Integer UPDATED_ORDONNEE = 2;

    private static final byte[] DEFAULT_ARRIEREPLAN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARRIEREPLAN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ARRIEREPLAN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARRIEREPLAN_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/maisons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MaisonRepository maisonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaisonMockMvc;

    private Maison maison;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maison createEntity(EntityManager em) {
        Maison maison = new Maison()
            .nom(DEFAULT_NOM)
            .icone(DEFAULT_ICONE)
            .iconeContentType(DEFAULT_ICONE_CONTENT_TYPE)
            .absisce(DEFAULT_ABSISCE)
            .ordonnee(DEFAULT_ORDONNEE)
            .arriereplan(DEFAULT_ARRIEREPLAN)
            .arriereplanContentType(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
        return maison;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maison createUpdatedEntity(EntityManager em) {
        Maison maison = new Maison()
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        return maison;
    }

    @BeforeEach
    public void initTest() {
        maison = createEntity(em);
    }

    @Test
    @Transactional
    void createMaison() throws Exception {
        int databaseSizeBeforeCreate = maisonRepository.findAll().size();
        // Create the Maison
        restMaisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maison)))
            .andExpect(status().isCreated());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeCreate + 1);
        Maison testMaison = maisonList.get(maisonList.size() - 1);
        assertThat(testMaison.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testMaison.getIcone()).isEqualTo(DEFAULT_ICONE);
        assertThat(testMaison.getIconeContentType()).isEqualTo(DEFAULT_ICONE_CONTENT_TYPE);
        assertThat(testMaison.getAbsisce()).isEqualTo(DEFAULT_ABSISCE);
        assertThat(testMaison.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testMaison.getArriereplan()).isEqualTo(DEFAULT_ARRIEREPLAN);
        assertThat(testMaison.getArriereplanContentType()).isEqualTo(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createMaisonWithExistingId() throws Exception {
        // Create the Maison with an existing ID
        maison.setId(1L);

        int databaseSizeBeforeCreate = maisonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maison)))
            .andExpect(status().isBadRequest());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = maisonRepository.findAll().size();
        // set the field null
        maison.setNom(null);

        // Create the Maison, which fails.

        restMaisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maison)))
            .andExpect(status().isBadRequest());

        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMaisons() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        // Get all the maisonList
        restMaisonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(maison.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].iconeContentType").value(hasItem(DEFAULT_ICONE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].icone").value(hasItem(Base64Utils.encodeToString(DEFAULT_ICONE))))
            .andExpect(jsonPath("$.[*].absisce").value(hasItem(DEFAULT_ABSISCE)))
            .andExpect(jsonPath("$.[*].ordonnee").value(hasItem(DEFAULT_ORDONNEE)))
            .andExpect(jsonPath("$.[*].arriereplanContentType").value(hasItem(DEFAULT_ARRIEREPLAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].arriereplan").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN))));
    }

    @Test
    @Transactional
    void getMaison() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        // Get the maison
        restMaisonMockMvc
            .perform(get(ENTITY_API_URL_ID, maison.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(maison.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.iconeContentType").value(DEFAULT_ICONE_CONTENT_TYPE))
            .andExpect(jsonPath("$.icone").value(Base64Utils.encodeToString(DEFAULT_ICONE)))
            .andExpect(jsonPath("$.absisce").value(DEFAULT_ABSISCE))
            .andExpect(jsonPath("$.ordonnee").value(DEFAULT_ORDONNEE))
            .andExpect(jsonPath("$.arriereplanContentType").value(DEFAULT_ARRIEREPLAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.arriereplan").value(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN)));
    }

    @Test
    @Transactional
    void getNonExistingMaison() throws Exception {
        // Get the maison
        restMaisonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMaison() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();

        // Update the maison
        Maison updatedMaison = maisonRepository.findById(maison.getId()).get();
        // Disconnect from session so that the updates on updatedMaison are not directly saved in db
        em.detach(updatedMaison);
        updatedMaison
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restMaisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMaison.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMaison))
            )
            .andExpect(status().isOk());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
        Maison testMaison = maisonList.get(maisonList.size() - 1);
        assertThat(testMaison.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMaison.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testMaison.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testMaison.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testMaison.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testMaison.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testMaison.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, maison.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(maison))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(maison))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maison)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaisonWithPatch() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();

        // Update the maison using partial update
        Maison partialUpdatedMaison = new Maison();
        partialUpdatedMaison.setId(maison.getId());

        partialUpdatedMaison.nom(UPDATED_NOM).arriereplan(UPDATED_ARRIEREPLAN).arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restMaisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaison.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaison))
            )
            .andExpect(status().isOk());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
        Maison testMaison = maisonList.get(maisonList.size() - 1);
        assertThat(testMaison.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMaison.getIcone()).isEqualTo(DEFAULT_ICONE);
        assertThat(testMaison.getIconeContentType()).isEqualTo(DEFAULT_ICONE_CONTENT_TYPE);
        assertThat(testMaison.getAbsisce()).isEqualTo(DEFAULT_ABSISCE);
        assertThat(testMaison.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testMaison.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testMaison.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateMaisonWithPatch() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();

        // Update the maison using partial update
        Maison partialUpdatedMaison = new Maison();
        partialUpdatedMaison.setId(maison.getId());

        partialUpdatedMaison
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restMaisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaison.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaison))
            )
            .andExpect(status().isOk());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
        Maison testMaison = maisonList.get(maisonList.size() - 1);
        assertThat(testMaison.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testMaison.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testMaison.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testMaison.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testMaison.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testMaison.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testMaison.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, maison.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(maison))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(maison))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaison() throws Exception {
        int databaseSizeBeforeUpdate = maisonRepository.findAll().size();
        maison.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaisonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(maison)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maison in the database
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaison() throws Exception {
        // Initialize the database
        maisonRepository.saveAndFlush(maison);

        int databaseSizeBeforeDelete = maisonRepository.findAll().size();

        // Delete the maison
        restMaisonMockMvc
            .perform(delete(ENTITY_API_URL_ID, maison.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Maison> maisonList = maisonRepository.findAll();
        assertThat(maisonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
