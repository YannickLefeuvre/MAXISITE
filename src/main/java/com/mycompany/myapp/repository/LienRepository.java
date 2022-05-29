package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Lien;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lien entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LienRepository extends JpaRepository<Lien, Long> {}
