package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Film;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Film entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {}
