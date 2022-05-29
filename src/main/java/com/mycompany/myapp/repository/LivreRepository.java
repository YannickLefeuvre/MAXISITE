package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Livre;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Livre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {}
