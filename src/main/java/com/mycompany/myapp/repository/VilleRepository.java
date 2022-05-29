package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Ville;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ville entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {}
