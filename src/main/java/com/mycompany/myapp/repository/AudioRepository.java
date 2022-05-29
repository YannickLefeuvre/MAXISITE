package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Audio;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Audio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {}
