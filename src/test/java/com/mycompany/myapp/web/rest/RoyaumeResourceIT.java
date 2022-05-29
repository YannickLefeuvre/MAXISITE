package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Royaume;
import com.mycompany.myapp.repository.RoyaumeRepository;
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
 * Integration tests for the {@link RoyaumeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoyaumeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_REGLES = "AAAAAAAAAA";
    private static final String UPDATED_REGLES = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ARRIEREPLAN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARRIEREPLAN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ARRIEREPLAN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARRIEREPLAN_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String ENTITY_API_URL = "/api/royaumes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RoyaumeRepository royaumeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoyaumeMockMvc;

    private Royaume royaume;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Royaume createEntity(EntityManager em) {
        Royaume royaume = new Royaume()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .regles(DEFAULT_REGLES)
            .arriereplan(DEFAULT_ARRIEREPLAN)
            .arriereplanContentType(DEFAULT_ARRIEREPLAN_CONTENT_TYPE)
            .isPublic(DEFAULT_IS_PUBLIC);
        return royaume;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Royaume createUpdatedEntity(EntityManager em) {
        Royaume royaume = new Royaume()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .regles(UPDATED_REGLES)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE)
            .isPublic(UPDATED_IS_PUBLIC);
        return royaume;
    }

    @BeforeEach
    public void initTest() {
        royaume = createEntity(em);
    }

    @Test
    @Transactional
    void createRoyaume() throws Exception {
        int databaseSizeBeforeCreate = royaumeRepository.findAll().size();
        // Create the Royaume
        restRoyaumeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(royaume)))
            .andExpect(status().isCreated());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeCreate + 1);
        Royaume testRoyaume = royaumeList.get(royaumeList.size() - 1);
        assertThat(testRoyaume.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testRoyaume.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRoyaume.getRegles()).isEqualTo(DEFAULT_REGLES);
        assertThat(testRoyaume.getArriereplan()).isEqualTo(DEFAULT_ARRIEREPLAN);
        assertThat(testRoyaume.getArriereplanContentType()).isEqualTo(DEFAULT_ARRIEREPLAN_CONTENT_TYPE);
        assertThat(testRoyaume.getIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
    }

    @Test
    @Transactional
    void createRoyaumeWithExistingId() throws Exception {
        // Create the Royaume with an existing ID
        royaume.setId(1L);

        int databaseSizeBeforeCreate = royaumeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoyaumeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(royaume)))
            .andExpect(status().isBadRequest());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = royaumeRepository.findAll().size();
        // set the field null
        royaume.setNom(null);

        // Create the Royaume, which fails.

        restRoyaumeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(royaume)))
            .andExpect(status().isBadRequest());

        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoyaumes() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        // Get all the royaumeList
        restRoyaumeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(royaume.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].regles").value(hasItem(DEFAULT_REGLES)))
            .andExpect(jsonPath("$.[*].arriereplanContentType").value(hasItem(DEFAULT_ARRIEREPLAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].arriereplan").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN))))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())));
    }

    @Test
    @Transactional
    void getRoyaume() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        // Get the royaume
        restRoyaumeMockMvc
            .perform(get(ENTITY_API_URL_ID, royaume.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(royaume.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.regles").value(DEFAULT_REGLES))
            .andExpect(jsonPath("$.arriereplanContentType").value(DEFAULT_ARRIEREPLAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.arriereplan").value(Base64Utils.encodeToString(DEFAULT_ARRIEREPLAN)))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingRoyaume() throws Exception {
        // Get the royaume
        restRoyaumeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRoyaume() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();

        // Update the royaume
        Royaume updatedRoyaume = royaumeRepository.findById(royaume.getId()).get();
        // Disconnect from session so that the updates on updatedRoyaume are not directly saved in db
        em.detach(updatedRoyaume);
        updatedRoyaume
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .regles(UPDATED_REGLES)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE)
            .isPublic(UPDATED_IS_PUBLIC);

        restRoyaumeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoyaume.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRoyaume))
            )
            .andExpect(status().isOk());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
        Royaume testRoyaume = royaumeList.get(royaumeList.size() - 1);
        assertThat(testRoyaume.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testRoyaume.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRoyaume.getRegles()).isEqualTo(UPDATED_REGLES);
        assertThat(testRoyaume.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testRoyaume.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        assertThat(testRoyaume.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void putNonExistingRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, royaume.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(royaume))
            )
            .andExpect(status().isBadRequest());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(royaume))
            )
            .andExpect(status().isBadRequest());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(royaume)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoyaumeWithPatch() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();

        // Update the royaume using partial update
        Royaume partialUpdatedRoyaume = new Royaume();
        partialUpdatedRoyaume.setId(royaume.getId());

        partialUpdatedRoyaume
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE)
            .isPublic(UPDATED_IS_PUBLIC);

        restRoyaumeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoyaume.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoyaume))
            )
            .andExpect(status().isOk());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
        Royaume testRoyaume = royaumeList.get(royaumeList.size() - 1);
        assertThat(testRoyaume.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testRoyaume.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRoyaume.getRegles()).isEqualTo(DEFAULT_REGLES);
        assertThat(testRoyaume.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testRoyaume.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        assertThat(testRoyaume.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void fullUpdateRoyaumeWithPatch() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();

        // Update the royaume using partial update
        Royaume partialUpdatedRoyaume = new Royaume();
        partialUpdatedRoyaume.setId(royaume.getId());

        partialUpdatedRoyaume
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .regles(UPDATED_REGLES)
            .arriereplan(UPDATED_ARRIEREPLAN)
            .arriereplanContentType(UPDATED_ARRIEREPLAN_CONTENT_TYPE)
            .isPublic(UPDATED_IS_PUBLIC);

        restRoyaumeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoyaume.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoyaume))
            )
            .andExpect(status().isOk());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
        Royaume testRoyaume = royaumeList.get(royaumeList.size() - 1);
        assertThat(testRoyaume.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testRoyaume.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRoyaume.getRegles()).isEqualTo(UPDATED_REGLES);
        assertThat(testRoyaume.getArriereplan()).isEqualTo(UPDATED_ARRIEREPLAN);
        assertThat(testRoyaume.getArriereplanContentType()).isEqualTo(UPDATED_ARRIEREPLAN_CONTENT_TYPE);
        assertThat(testRoyaume.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void patchNonExistingRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, royaume.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(royaume))
            )
            .andExpect(status().isBadRequest());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(royaume))
            )
            .andExpect(status().isBadRequest());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoyaume() throws Exception {
        int databaseSizeBeforeUpdate = royaumeRepository.findAll().size();
        royaume.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoyaumeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(royaume)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Royaume in the database
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoyaume() throws Exception {
        // Initialize the database
        royaumeRepository.saveAndFlush(royaume);

        int databaseSizeBeforeDelete = royaumeRepository.findAll().size();

        // Delete the royaume
        restRoyaumeMockMvc
            .perform(delete(ENTITY_API_URL_ID, royaume.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Royaume> royaumeList = royaumeRepository.findAll();
        assertThat(royaumeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
