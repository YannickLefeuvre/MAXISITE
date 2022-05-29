package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Livre;
import com.mycompany.myapp.repository.LivreRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Livre}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LivreResource {

    private final Logger log = LoggerFactory.getLogger(LivreResource.class);

    private static final String ENTITY_NAME = "livre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LivreRepository livreRepository;

    public LivreResource(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    /**
     * {@code POST  /livres} : Create a new livre.
     *
     * @param livre the livre to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livre, or with status {@code 400 (Bad Request)} if the livre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/livres")
    public ResponseEntity<Livre> createLivre(@Valid @RequestBody Livre livre) throws URISyntaxException {
        log.debug("REST request to save Livre : {}", livre);
        if (livre.getId() != null) {
            throw new BadRequestAlertException("A new livre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Livre result = livreRepository.save(livre);
        return ResponseEntity
            .created(new URI("/api/livres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /livres/:id} : Updates an existing livre.
     *
     * @param id the id of the livre to save.
     * @param livre the livre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livre,
     * or with status {@code 400 (Bad Request)} if the livre is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/livres/{id}")
    public ResponseEntity<Livre> updateLivre(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Livre livre)
        throws URISyntaxException {
        log.debug("REST request to update Livre : {}, {}", id, livre);
        if (livre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Livre result = livreRepository.save(livre);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, livre.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /livres/:id} : Partial updates given fields of an existing livre, field will ignore if it is null
     *
     * @param id the id of the livre to save.
     * @param livre the livre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livre,
     * or with status {@code 400 (Bad Request)} if the livre is not valid,
     * or with status {@code 404 (Not Found)} if the livre is not found,
     * or with status {@code 500 (Internal Server Error)} if the livre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/livres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Livre> partialUpdateLivre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Livre livre
    ) throws URISyntaxException {
        log.debug("REST request to partial update Livre partially : {}, {}", id, livre);
        if (livre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Livre> result = livreRepository
            .findById(livre.getId())
            .map(existingLivre -> {
                if (livre.getNom() != null) {
                    existingLivre.setNom(livre.getNom());
                }
                if (livre.getImages() != null) {
                    existingLivre.setImages(livre.getImages());
                }
                if (livre.getImagesContentType() != null) {
                    existingLivre.setImagesContentType(livre.getImagesContentType());
                }
                if (livre.getDescription() != null) {
                    existingLivre.setDescription(livre.getDescription());
                }

                return existingLivre;
            })
            .map(livreRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, livre.getId().toString())
        );
    }

    /**
     * {@code GET  /livres} : get all the livres.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of livres in body.
     */
    @GetMapping("/livres")
    public List<Livre> getAllLivres() {
        log.debug("REST request to get all Livres");
        return livreRepository.findAll();
    }

    /**
     * {@code GET  /livres/:id} : get the "id" livre.
     *
     * @param id the id of the livre to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livre, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livres/{id}")
    public ResponseEntity<Livre> getLivre(@PathVariable Long id) {
        log.debug("REST request to get Livre : {}", id);
        Optional<Livre> livre = livreRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(livre);
    }

    /**
     * {@code DELETE  /livres/:id} : delete the "id" livre.
     *
     * @param id the id of the livre to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/livres/{id}")
    public ResponseEntity<Void> deleteLivre(@PathVariable Long id) {
        log.debug("REST request to delete Livre : {}", id);
        livreRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
