package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Maison;
import com.mycompany.myapp.repository.MaisonRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Maison}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MaisonResource {

    private final Logger log = LoggerFactory.getLogger(MaisonResource.class);

    private static final String ENTITY_NAME = "maison";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MaisonRepository maisonRepository;

    public MaisonResource(MaisonRepository maisonRepository) {
        this.maisonRepository = maisonRepository;
    }

    /**
     * {@code POST  /maisons} : Create a new maison.
     *
     * @param maison the maison to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maison, or with status {@code 400 (Bad Request)} if the maison has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/maisons")
    public ResponseEntity<Maison> createMaison(@Valid @RequestBody Maison maison) throws URISyntaxException {
        log.debug("REST request to save Maison : {}", maison);
        if (maison.getId() != null) {
            throw new BadRequestAlertException("A new maison cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Maison result = maisonRepository.save(maison);
        return ResponseEntity
            .created(new URI("/api/maisons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /maisons/:id} : Updates an existing maison.
     *
     * @param id the id of the maison to save.
     * @param maison the maison to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maison,
     * or with status {@code 400 (Bad Request)} if the maison is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maison couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/maisons/{id}")
    public ResponseEntity<Maison> updateMaison(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Maison maison
    ) throws URISyntaxException {
        log.debug("REST request to update Maison : {}, {}", id, maison);
        if (maison.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maison.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!maisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Maison result = maisonRepository.save(maison);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, maison.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /maisons/:id} : Partial updates given fields of an existing maison, field will ignore if it is null
     *
     * @param id the id of the maison to save.
     * @param maison the maison to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maison,
     * or with status {@code 400 (Bad Request)} if the maison is not valid,
     * or with status {@code 404 (Not Found)} if the maison is not found,
     * or with status {@code 500 (Internal Server Error)} if the maison couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/maisons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Maison> partialUpdateMaison(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Maison maison
    ) throws URISyntaxException {
        log.debug("REST request to partial update Maison partially : {}, {}", id, maison);
        if (maison.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maison.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!maisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Maison> result = maisonRepository
            .findById(maison.getId())
            .map(existingMaison -> {
                if (maison.getNom() != null) {
                    existingMaison.setNom(maison.getNom());
                }
                if (maison.getIcone() != null) {
                    existingMaison.setIcone(maison.getIcone());
                }
                if (maison.getIconeContentType() != null) {
                    existingMaison.setIconeContentType(maison.getIconeContentType());
                }
                if (maison.getAbsisce() != null) {
                    existingMaison.setAbsisce(maison.getAbsisce());
                }
                if (maison.getOrdonnee() != null) {
                    existingMaison.setOrdonnee(maison.getOrdonnee());
                }
                if (maison.getArriereplan() != null) {
                    existingMaison.setArriereplan(maison.getArriereplan());
                }
                if (maison.getArriereplanContentType() != null) {
                    existingMaison.setArriereplanContentType(maison.getArriereplanContentType());
                }

                return existingMaison;
            })
            .map(maisonRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, maison.getId().toString())
        );
    }

    /**
     * {@code GET  /maisons} : get all the maisons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of maisons in body.
     */
    @GetMapping("/maisons")
    public List<Maison> getAllMaisons() {
        log.debug("REST request to get all Maisons");
        return maisonRepository.findAll();
    }

    /**
     * {@code GET  /maisons/:id} : get the "id" maison.
     *
     * @param id the id of the maison to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maison, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/maisons/{id}")
    public ResponseEntity<Maison> getMaison(@PathVariable Long id) {
        log.debug("REST request to get Maison : {}", id);
        Optional<Maison> maison = maisonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(maison);
    }

    /**
     * {@code DELETE  /maisons/:id} : delete the "id" maison.
     *
     * @param id the id of the maison to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/maisons/{id}")
    public ResponseEntity<Void> deleteMaison(@PathVariable Long id) {
        log.debug("REST request to delete Maison : {}", id);
        maisonRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
