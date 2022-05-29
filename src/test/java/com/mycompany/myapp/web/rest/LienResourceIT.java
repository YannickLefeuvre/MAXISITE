package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Lien;
import com.mycompany.myapp.repository.LienRepository;
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
 * Integration tests for the {@link LienResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LienResourceIT {

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

    private static final String ENTITY_API_URL = "/api/liens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LienRepository lienRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLienMockMvc;

    private Lien lien;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lien createEntity(EntityManager em) {
        Lien lien = new Lien()
            .nom(DEFAULT_NOM)
            .icone(DEFAULT_ICONE)
            .iconeContentType(DEFAULT_ICONE_CONTENT_TYPE)
            .absisce(DEFAULT_ABSISCE)
            .ordonnee(DEFAULT_ORDONNEE)
            .arriereplan(DEFAULT_ARRIEREPLAN)
            .arriereplanContentType(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
        return lien;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lien createUpdatedEntity(EntityManager em) {
        Lien lien = new Lien()
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        return lien;
    }

    @BeforeEach
    public void initTest() {
        lien = createEntity(em);
    }

    @Test
    @Transactional
    void createLien() throws Exception {
        int databaseSizeBeforeCreate = lienRepository.findAll().size();
        // Create the Lien
        restLienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lien)))
            .andExpect(status().isCreated());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeCreate + 1);
        Lien testLien = lienList.get(lienList.size() - 1);
        assertThat(testLien.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testLien.getIcone()).isEqualTo(DEFAULT_ICONE);
        assertThat(testLien.getIconeContentType()).isEqualTo(DEFAULT_ICONE_CONTENT_TYPE);
        assertThat(testLien.getAbsisce()).isEqualTo(DEFAULT_ABSISCE);
        assertThat(testLien.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testLien.getArriereplan()).isEqualTo(DEFAULT_ARRIEREPLAN);
        assertThat(testLien.getArriereplanContentType()).isEqualTo(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createLienWithExistingId() throws Exception {
        // Create the Lien with an existing ID
        lien.setId(1L);

        int databaseSizeBeforeCreate = lienRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lien)))
            .andExpect(status().isBadRequest());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = lienRepository.findAll().size();
        // set the field null
        lien.setNom(null);

        // Create the Lien, which fails.

        restLienMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lien)))
            .andExpect(status().isBadRequest());

        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLiens() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        // Get all the lienList
        restLienMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lien.getId().intValue())))
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
    void getLien() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        // Get the lien
        restLienMockMvc
            .perform(get(ENTITY_API_URL_ID, lien.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lien.getId().intValue()))
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
    void getNonExistingLien() throws Exception {
        // Get the lien
        restLienMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLien() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        int databaseSizeBeforeUpdate = lienRepository.findAll().size();

        // Update the lien
        Lien updatedLien = lienRepository.findById(lien.getId()).get();
        // Disconnect from session so that the updates on updatedLien are not directly saved in db
        em.detach(updatedLien);
        updatedLien
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restLienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLien.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLien))
            )
            .andExpect(status().isOk());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
        Lien testLien = lienList.get(lienList.size() - 1);
        assertThat(testLien.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLien.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testLien.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testLien.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testLien.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testLien.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testLien.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lien.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lien))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lien))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lien)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLienWithPatch() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        int databaseSizeBeforeUpdate = lienRepository.findAll().size();

        // Update the lien using partial update
        Lien partialUpdatedLien = new Lien();
        partialUpdatedLien.setId(lien.getId());

        partialUpdatedLien.nom(UPDATED_NOM).icone(UPDATED_ICONE).iconeContentType(UPDATED_ICONE_CONTENT_TYPE);

        restLienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLien.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLien))
            )
            .andExpect(status().isOk());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
        Lien testLien = lienList.get(lienList.size() - 1);
        assertThat(testLien.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLien.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testLien.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testLien.getAbsisce()).isEqualTo(DEFAULT_ABSISCE);
        assertThat(testLien.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testLien.getArriereplan()).isEqualTo(DEFAULT_ARRIEREPLAN);
        assertThat(testLien.getArriereplanContentType()).isEqualTo(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateLienWithPatch() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        int databaseSizeBeforeUpdate = lienRepository.findAll().size();

        // Update the lien using partial update
        Lien partialUpdatedLien = new Lien();
        partialUpdatedLien.setId(lien.getId());

        partialUpdatedLien
            .nom(UPDATED_NOM)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restLienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLien.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLien))
            )
            .andExpect(status().isOk());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
        Lien testLien = lienList.get(lienList.size() - 1);
        assertThat(testLien.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLien.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testLien.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testLien.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testLien.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testLien.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testLien.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lien.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lien))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lien))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLien() throws Exception {
        int databaseSizeBeforeUpdate = lienRepository.findAll().size();
        lien.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLienMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lien)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lien in the database
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLien() throws Exception {
        // Initialize the database
        lienRepository.saveAndFlush(lien);

        int databaseSizeBeforeDelete = lienRepository.findAll().size();

        // Delete the lien
        restLienMockMvc
            .perform(delete(ENTITY_API_URL_ID, lien.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lien> lienList = lienRepository.findAll();
        assertThat(lienList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
