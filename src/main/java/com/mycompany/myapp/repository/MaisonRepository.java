package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Maison;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Maison entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaisonRepository extends JpaRepository<Maison, Long> {}
