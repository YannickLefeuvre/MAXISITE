package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Royaume;
import com.mycompany.myapp.repository.RoyaumeRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Royaume}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RoyaumeResource {

    private final Logger log = LoggerFactory.getLogger(RoyaumeResource.class);

    private static final String ENTITY_NAME = "royaume";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoyaumeRepository royaumeRepository;

    public RoyaumeResource(RoyaumeRepository royaumeRepository) {
        this.royaumeRepository = royaumeRepository;
    }

    /**
     * {@code POST  /royaumes} : Create a new royaume.
     *
     * @param royaume the royaume to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new royaume, or with status {@code 400 (Bad Request)} if the royaume has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/royaumes")
    public ResponseEntity<Royaume> createRoyaume(@Valid @RequestBody Royaume royaume) throws URISyntaxException {
        log.debug("REST request to save Royaume : {}", royaume);
        if (royaume.getId() != null) {
            throw new BadRequestAlertException("A new royaume cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Royaume result = royaumeRepository.save(royaume);
        return ResponseEntity
            .created(new URI("/api/royaumes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /royaumes/:id} : Updates an existing royaume.
     *
     * @param id the id of the royaume to save.
     * @param royaume the royaume to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated royaume,
     * or with status {@code 400 (Bad Request)} if the royaume is not valid,
     * or with status {@code 500 (Internal Server Error)} if the royaume couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/royaumes/{id}")
    public ResponseEntity<Royaume> updateRoyaume(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Royaume royaume
    ) throws URISyntaxException {
        log.debug("REST request to update Royaume : {}, {}", id, royaume);
        if (royaume.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, royaume.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!royaumeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Royaume result = royaumeRepository.save(royaume);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, royaume.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /royaumes/:id} : Partial updates given fields of an existing royaume, field will ignore if it is null
     *
     * @param id the id of the royaume to save.
     * @param royaume the royaume to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated royaume,
     * or with status {@code 400 (Bad Request)} if the royaume is not valid,
     * or with status {@code 404 (Not Found)} if the royaume is not found,
     * or with status {@code 500 (Internal Server Error)} if the royaume couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/royaumes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Royaume> partialUpdateRoyaume(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Royaume royaume
    ) throws URISyntaxException {
        log.debug("REST request to partial update Royaume partially : {}, {}", id, royaume);
        if (royaume.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, royaume.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!royaumeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Royaume> result = royaumeRepository
            .findById(royaume.getId())
            .map(existingRoyaume -> {
                if (royaume.getNom() != null) {
                    existingRoyaume.setNom(royaume.getNom());
                }
                if (royaume.getDescription() != null) {
                    existingRoyaume.setDescription(royaume.getDescription());
                }
                if (royaume.getRegles() != null) {
                    existingRoyaume.setRegles(royaume.getRegles());
                }
                if (royaume.getArriereplan() != null) {
                    existingRoyaume.setArriereplan(royaume.getArriereplan());
                }
                if (royaume.getArriereplanContentType() != null) {
                    existingRoyaume.setArriereplanContentType(royaume.getArriereplanContentType());
                }
                if (royaume.getIsPublic() != null) {
                    existingRoyaume.setIsPublic(royaume.getIsPublic());
                }

                return existingRoyaume;
            })
            .map(royaumeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, royaume.getId().toString())
        );
    }

    /**
     * {@code GET  /royaumes} : get all the royaumes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of royaumes in body.
     */
    @GetMapping("/royaumes")
    public List<Royaume> getAllRoyaumes() {
        log.debug("REST request to get all Royaumes");
        return royaumeRepository.findAll();
    }

    /**
     * {@code GET  /royaumes/:id} : get the "id" royaume.
     *
     * @param id the id of the royaume to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the royaume, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/royaumes/{id}")
    public ResponseEntity<Royaume> getRoyaume(@PathVariable Long id) {
        log.debug("REST request to get Royaume : {}", id);
        Optional<Royaume> royaume = royaumeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(royaume);
    }

    /**
     * {@code DELETE  /royaumes/:id} : delete the "id" royaume.
     *
     * @param id the id of the royaume to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/royaumes/{id}")
    public ResponseEntity<Void> deleteRoyaume(@PathVariable Long id) {
        log.debug("REST request to delete Royaume : {}", id);
        royaumeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
