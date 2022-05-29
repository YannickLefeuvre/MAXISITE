package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlbumPhotoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlbumPhoto.class);
        AlbumPhoto albumPhoto1 = new AlbumPhoto();
        albumPhoto1.setId(1L);
        AlbumPhoto albumPhoto2 = new AlbumPhoto();
        albumPhoto2.setId(albumPhoto1.getId());
        assertThat(albumPhoto1).isEqualTo(albumPhoto2);
        albumPhoto2.setId(2L);
        assertThat(albumPhoto1).isNotEqualTo(albumPhoto2);
        albumPhoto1.setId(null);
        assertThat(albumPhoto1).isNotEqualTo(albumPhoto2);
    }
}
