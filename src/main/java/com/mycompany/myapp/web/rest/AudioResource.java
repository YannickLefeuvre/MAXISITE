package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Audio;
import com.mycompany.myapp.repository.AudioRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Audio}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AudioResource {

    private final Logger log = LoggerFactory.getLogger(AudioResource.class);

    private static final String ENTITY_NAME = "audio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AudioRepository audioRepository;

    public AudioResource(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }

    /**
     * {@code POST  /audio} : Create a new audio.
     *
     * @param audio the audio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new audio, or with status {@code 400 (Bad Request)} if the audio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audio")
    public ResponseEntity<Audio> createAudio(@Valid @RequestBody Audio audio) throws URISyntaxException {
        log.debug("REST request to save Audio : {}", audio);
        if (audio.getId() != null) {
            throw new BadRequestAlertException("A new audio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Audio result = audioRepository.save(audio);
        return ResponseEntity
            .created(new URI("/api/audio/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /audio/:id} : Updates an existing audio.
     *
     * @param id the id of the audio to save.
     * @param audio the audio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated audio,
     * or with status {@code 400 (Bad Request)} if the audio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the audio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audio/{id}")
    public ResponseEntity<Audio> updateAudio(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Audio audio)
        throws URISyntaxException {
        log.debug("REST request to update Audio : {}, {}", id, audio);
        if (audio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, audio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!audioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Audio result = audioRepository.save(audio);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, audio.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /audio/:id} : Partial updates given fields of an existing audio, field will ignore if it is null
     *
     * @param id the id of the audio to save.
     * @param audio the audio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated audio,
     * or with status {@code 400 (Bad Request)} if the audio is not valid,
     * or with status {@code 404 (Not Found)} if the audio is not found,
     * or with status {@code 500 (Internal Server Error)} if the audio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/audio/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Audio> partialUpdateAudio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Audio audio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Audio partially : {}, {}", id, audio);
        if (audio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, audio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!audioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Audio> result = audioRepository
            .findById(audio.getId())
            .map(existingAudio -> {
                if (audio.getNom() != null) {
                    existingAudio.setNom(audio.getNom());
                }
                if (audio.getUrl() != null) {
                    existingAudio.setUrl(audio.getUrl());
                }

                return existingAudio;
            })
            .map(audioRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, audio.getId().toString())
        );
    }

    /**
     * {@code GET  /audio} : get all the audio.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of audio in body.
     */
    @GetMapping("/audio")
    public List<Audio> getAllAudio() {
        log.debug("REST request to get all Audio");
        return audioRepository.findAll();
    }

    /**
     * {@code GET  /audio/:id} : get the "id" audio.
     *
     * @param id the id of the audio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the audio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/audio/{id}")
    public ResponseEntity<Audio> getAudio(@PathVariable Long id) {
        log.debug("REST request to get Audio : {}", id);
        Optional<Audio> audio = audioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(audio);
    }

    /**
     * {@code DELETE  /audio/:id} : delete the "id" audio.
     *
     * @param id the id of the audio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/audio/{id}")
    public ResponseEntity<Void> deleteAudio(@PathVariable Long id) {
        log.debug("REST request to delete Audio : {}", id);
        audioRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
