package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AlbumPhoto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AlbumPhoto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, Long> {}
