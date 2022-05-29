package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.AlbumPhoto;
import com.mycompany.myapp.repository.AlbumPhotoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.AlbumPhoto}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AlbumPhotoResource {

    private final Logger log = LoggerFactory.getLogger(AlbumPhotoResource.class);

    private static final String ENTITY_NAME = "albumPhoto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlbumPhotoRepository albumPhotoRepository;

    public AlbumPhotoResource(AlbumPhotoRepository albumPhotoRepository) {
        this.albumPhotoRepository = albumPhotoRepository;
    }

    /**
     * {@code POST  /album-photos} : Create a new albumPhoto.
     *
     * @param albumPhoto the albumPhoto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new albumPhoto, or with status {@code 400 (Bad Request)} if the albumPhoto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/album-photos")
    public ResponseEntity<AlbumPhoto> createAlbumPhoto(@Valid @RequestBody AlbumPhoto albumPhoto) throws URISyntaxException {
        log.debug("REST request to save AlbumPhoto : {}", albumPhoto);
        if (albumPhoto.getId() != null) {
            throw new BadRequestAlertException("A new albumPhoto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AlbumPhoto result = albumPhotoRepository.save(albumPhoto);
        return ResponseEntity
            .created(new URI("/api/album-photos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /album-photos/:id} : Updates an existing albumPhoto.
     *
     * @param id the id of the albumPhoto to save.
     * @param albumPhoto the albumPhoto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumPhoto,
     * or with status {@code 400 (Bad Request)} if the albumPhoto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the albumPhoto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/album-photos/{id}")
    public ResponseEntity<AlbumPhoto> updateAlbumPhoto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlbumPhoto albumPhoto
    ) throws URISyntaxException {
        log.debug("REST request to update AlbumPhoto : {}, {}", id, albumPhoto);
        if (albumPhoto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, albumPhoto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!albumPhotoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AlbumPhoto result = albumPhotoRepository.save(albumPhoto);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, albumPhoto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /album-photos/:id} : Partial updates given fields of an existing albumPhoto, field will ignore if it is null
     *
     * @param id the id of the albumPhoto to save.
     * @param albumPhoto the albumPhoto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumPhoto,
     * or with status {@code 400 (Bad Request)} if the albumPhoto is not valid,
     * or with status {@code 404 (Not Found)} if the albumPhoto is not found,
     * or with status {@code 500 (Internal Server Error)} if the albumPhoto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/album-photos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AlbumPhoto> partialUpdateAlbumPhoto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlbumPhoto albumPhoto
    ) throws URISyntaxException {
        log.debug("REST request to partial update AlbumPhoto partially : {}, {}", id, albumPhoto);
        if (albumPhoto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, albumPhoto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!albumPhotoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AlbumPhoto> result = albumPhotoRepository
            .findById(albumPhoto.getId())
            .map(existingAlbumPhoto -> {
                if (albumPhoto.getNom() != null) {
                    existingAlbumPhoto.setNom(albumPhoto.getNom());
                }
                if (albumPhoto.getImages() != null) {
                    existingAlbumPhoto.setImages(albumPhoto.getImages());
                }
                if (albumPhoto.getImagesContentType() != null) {
                    existingAlbumPhoto.setImagesContentType(albumPhoto.getImagesContentType());
                }
                if (albumPhoto.getDescription() != null) {
                    existingAlbumPhoto.setDescription(albumPhoto.getDescription());
                }

                return existingAlbumPhoto;
            })
            .map(albumPhotoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, albumPhoto.getId().toString())
        );
    }

    /**
     * {@code GET  /album-photos} : get all the albumPhotos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of albumPhotos in body.
     */
    @GetMapping("/album-photos")
    public List<AlbumPhoto> getAllAlbumPhotos() {
        log.debug("REST request to get all AlbumPhotos");
        return albumPhotoRepository.findAll();
    }

    /**
     * {@code GET  /album-photos/:id} : get the "id" albumPhoto.
     *
     * @param id the id of the albumPhoto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the albumPhoto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/album-photos/{id}")
    public ResponseEntity<AlbumPhoto> getAlbumPhoto(@PathVariable Long id) {
        log.debug("REST request to get AlbumPhoto : {}", id);
        Optional<AlbumPhoto> albumPhoto = albumPhotoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(albumPhoto);
    }

    /**
     * {@code DELETE  /album-photos/:id} : delete the "id" albumPhoto.
     *
     * @param id the id of the albumPhoto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/album-photos/{id}")
    public ResponseEntity<Void> deleteAlbumPhoto(@PathVariable Long id) {
        log.debug("REST request to delete AlbumPhoto : {}", id);
        albumPhotoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
