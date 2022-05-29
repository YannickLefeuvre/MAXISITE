package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Royaume;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Royaume entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoyaumeRepository extends JpaRepository<Royaume, Long> {}
