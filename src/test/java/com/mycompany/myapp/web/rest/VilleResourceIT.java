package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Ville;
import com.mycompany.myapp.repository.VilleRepository;
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
 * Integration tests for the {@link VilleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VilleResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_CAPITAL = false;
    private static final Boolean UPDATED_IS_CAPITAL = true;

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

    private static final String ENTITY_API_URL = "/api/villes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVilleMockMvc;

    private Ville ville;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createEntity(EntityManager em) {
        Ville ville = new Ville()
            .nom(DEFAULT_NOM)
            .isCapital(DEFAULT_IS_CAPITAL)
            .icone(DEFAULT_ICONE)
            .iconeContentType(DEFAULT_ICONE_CONTENT_TYPE)
            .absisce(DEFAULT_ABSISCE)
            .ordonnee(DEFAULT_ORDONNEE)
            .arriereplan(DEFAULT_ARRIEREPLAN)
            .arriereplanContentType(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
        return ville;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createUpdatedEntity(EntityManager em) {
        Ville ville = new Ville()
            .nom(UPDATED_NOM)
            .isCapital(UPDATED_IS_CAPITAL)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        return ville;
    }

    @BeforeEach
    public void initTest() {
        ville = createEntity(em);
    }

    @Test
    @Transactional
    void createVille() throws Exception {
        int databaseSizeBeforeCreate = villeRepository.findAll().size();
        // Create the Ville
        restVilleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isCreated());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeCreate + 1);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testVille.getIsCapital()).isEqualTo(DEFAULT_IS_CAPITAL);
        assertThat(testVille.getIcone()).isEqualTo(DEFAULT_ICONE);
        assertThat(testVille.getIconeContentType()).isEqualTo(DEFAULT_ICONE_CONTENT_TYPE);
        assertThat(testVille.getAbsisce()).isEqualTo(DEFAULT_ABSISCE);
        assertThat(testVille.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testVille.getArriereplan()).isEqualTo(DEFAULT_ARRIEREPLAN);
        assertThat(testVille.getArriereplanContentType()).isEqualTo(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createVilleWithExistingId() throws Exception {
        // Create the Ville with an existing ID
        ville.setId(1L);

        int databaseSizeBeforeCreate = villeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVilleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = villeRepository.findAll().size();
        // set the field null
        ville.setNom(null);

        // Create the Ville, which fails.

        restVilleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isBadRequest());

        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsCapitalIsRequired() throws Exception {
        int databaseSizeBeforeTest = villeRepository.findAll().size();
        // set the field null
        ville.setIsCapital(null);

        // Create the Ville, which fails.

        restVilleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isBadRequest());

        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVilles() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        // Get all the villeList
        restVilleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ville.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].isCapital").value(hasItem(DEFAULT_IS_CAPITAL.booleanValue())))
            .andExpect(jsonPath("$.[*].iconeContentType").value(hasItem(DEFAULT_ICONE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].icone").value(hasItem(Base64Utils.encodeToString(DEFAULT_ICONE))))
            .andExpect(jsonPath("$.[*].absisce").value(hasItem(DEFAULT_ABSISCE)))
            .andExpect(jsonPath("$.[*].ordonnee").value(hasItem(DEFAULT_ORDONNEE)))
            .andExpect(jsonPath("$.[*].arriereplanContentType").value(hasItem(DEFAULT_ARRIEREPLAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].arriereplan").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN))));
    }

    @Test
    @Transactional
    void getVille() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        // Get the ville
        restVilleMockMvc
            .perform(get(ENTITY_API_URL_ID, ville.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ville.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.isCapital").value(DEFAULT_IS_CAPITAL.booleanValue()))
            .andExpect(jsonPath("$.iconeContentType").value(DEFAULT_ICONE_CONTENT_TYPE))
            .andExpect(jsonPath("$.icone").value(Base64Utils.encodeToString(DEFAULT_ICONE)))
            .andExpect(jsonPath("$.absisce").value(DEFAULT_ABSISCE))
            .andExpect(jsonPath("$.ordonnee").value(DEFAULT_ORDONNEE))
            .andExpect(jsonPath("$.arriereplanContentType").value(DEFAULT_ARRIEREPLAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.arriereplan").value(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN)));
    }

    @Test
    @Transactional
    void getNonExistingVille() throws Exception {
        // Get the ville
        restVilleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVille() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        int databaseSizeBeforeUpdate = villeRepository.findAll().size();

        // Update the ville
        Ville updatedVille = villeRepository.findById(ville.getId()).get();
        // Disconnect from session so that the updates on updatedVille are not directly saved in db
        em.detach(updatedVille);
        updatedVille
            .nom(UPDATED_NOM)
            .isCapital(UPDATED_IS_CAPITAL)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restVilleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVille.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testVille.getIsCapital()).isEqualTo(UPDATED_IS_CAPITAL);
        assertThat(testVille.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testVille.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testVille.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testVille.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testVille.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testVille.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ville.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        int databaseSizeBeforeUpdate = villeRepository.findAll().size();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille
            .nom(UPDATED_NOM)
            .isCapital(UPDATED_IS_CAPITAL)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVille.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testVille.getIsCapital()).isEqualTo(UPDATED_IS_CAPITAL);
        assertThat(testVille.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testVille.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testVille.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testVille.getOrdonnee()).isEqualTo(DEFAULT_ORDONNEE);
        assertThat(testVille.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testVille.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        int databaseSizeBeforeUpdate = villeRepository.findAll().size();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille
            .nom(UPDATED_NOM)
            .isCapital(UPDATED_IS_CAPITAL)
            .icone(UPDATED_ICONE)
            .iconeContentType(UPDATED_ICONE_CONTENT_TYPE)
            .absisce(UPDATED_ABSISCE)
            .ordonnee(UPDATED_ORDONNEE)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE);

        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVille.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
        Ville testVille = villeList.get(villeList.size() - 1);
        assertThat(testVille.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testVille.getIsCapital()).isEqualTo(UPDATED_IS_CAPITAL);
        assertThat(testVille.getIcone()).isEqualTo(UPDATED_ICONE);
        assertThat(testVille.getIconeContentType()).isEqualTo(UPDATED_ICONE_CONTENT_TYPE);
        assertThat(testVille.getAbsisce()).isEqualTo(UPDATED_ABSISCE);
        assertThat(testVille.getOrdonnee()).isEqualTo(UPDATED_ORDONNEE);
        assertThat(testVille.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testVille.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ville.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVille() throws Exception {
        int databaseSizeBeforeUpdate = villeRepository.findAll().size();
        ville.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ville)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ville in the database
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVille() throws Exception {
        // Initialize the database
        villeRepository.saveAndFlush(ville);

        int databaseSizeBeforeDelete = villeRepository.findAll().size();

        // Delete the ville
        restVilleMockMvc
            .perform(delete(ENTITY_API_URL_ID, ville.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ville> villeList = villeRepository.findAll();
        assertThat(villeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
