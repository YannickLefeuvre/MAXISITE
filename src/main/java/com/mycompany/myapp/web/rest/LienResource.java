package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Lien;
import com.mycompany.myapp.repository.LienRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Lien}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LienResource {

    private final Logger log = LoggerFactory.getLogger(LienResource.class);

    private static final String ENTITY_NAME = "lien";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LienRepository lienRepository;

    public LienResource(LienRepository lienRepository) {
        this.lienRepository = lienRepository;
    }

    /**
     * {@code POST  /liens} : Create a new lien.
     *
     * @param lien the lien to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lien, or with status {@code 400 (Bad Request)} if the lien has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/liens")
    public ResponseEntity<Lien> createLien(@Valid @RequestBody Lien lien) throws URISyntaxException {
        log.debug("REST request to save Lien : {}", lien);
        if (lien.getId() != null) {
            throw new BadRequestAlertException("A new lien cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Lien result = lienRepository.save(lien);
        return ResponseEntity
            .created(new URI("/api/liens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /liens/:id} : Updates an existing lien.
     *
     * @param id the id of the lien to save.
     * @param lien the lien to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lien,
     * or with status {@code 400 (Bad Request)} if the lien is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lien couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/liens/{id}")
    public ResponseEntity<Lien> updateLien(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Lien lien)
        throws URISyntaxException {
        log.debug("REST request to update Lien : {}, {}", id, lien);
        if (lien.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lien.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lienRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Lien result = lienRepository.save(lien);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lien.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /liens/:id} : Partial updates given fields of an existing lien, field will ignore if it is null
     *
     * @param id the id of the lien to save.
     * @param lien the lien to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lien,
     * or with status {@code 400 (Bad Request)} if the lien is not valid,
     * or with status {@code 404 (Not Found)} if the lien is not found,
     * or with status {@code 500 (Internal Server Error)} if the lien couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/liens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Lien> partialUpdateLien(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Lien lien
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lien partially : {}, {}", id, lien);
        if (lien.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lien.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lienRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Lien> result = lienRepository
            .findById(lien.getId())
            .map(existingLien -> {
                if (lien.getNom() != null) {
                    existingLien.setNom(lien.getNom());
                }
                if (lien.getIcone() != null) {
                    existingLien.setIcone(lien.getIcone());
                }
                if (lien.getIconeContentType() != null) {
                    existingLien.setIconeContentType(lien.getIconeContentType());
                }
                if (lien.getAbsisce() != null) {
                    existingLien.setAbsisce(lien.getAbsisce());
                }
                if (lien.getOrdonnee() != null) {
                    existingLien.setOrdonnee(lien.getOrdonnee());
                }
                if (lien.getArriereplan() != null) {
                    existingLien.setArriereplan(lien.getArriereplan());
                }
                if (lien.getArriereplanContentType() != null) {
                    existingLien.setArriereplanContentType(lien.getArriereplanContentType());
                }

                return existingLien;
            })
            .map(lienRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lien.getId().toString())
        );
    }

    /**
     * {@code GET  /liens} : get all the liens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of liens in body.
     */
    @GetMapping("/liens")
    public List<Lien> getAllLiens() {
        log.debug("REST request to get all Liens");
        return lienRepository.findAll();
    }

    /**
     * {@code GET  /liens/:id} : get the "id" lien.
     *
     * @param id the id of the lien to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lien, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/liens/{id}")
    public ResponseEntity<Lien> getLien(@PathVariable Long id) {
        log.debug("REST request to get Lien : {}", id);
        Optional<Lien> lien = lienRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lien);
    }

    /**
     * {@code DELETE  /liens/:id} : delete the "id" lien.
     *
     * @param id the id of the lien to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/liens/{id}")
    public ResponseEntity<Void> deleteLien(@PathVariable Long id) {
        log.debug("REST request to delete Lien : {}", id);
        lienRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
