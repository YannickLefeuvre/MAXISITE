package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LienTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lien.class);
        Lien lien1 = new Lien();
        lien1.setId(1L);
        Lien lien2 = new Lien();
        lien2.setId(lien1.getId());
        assertThat(lien1).isEqualTo(lien2);
        lien2.setId(2L);
        assertThat(lien1).isNotEqualTo(lien2);
        lien1.setId(null);
        assertThat(lien1).isNotEqualTo(lien2);
    }
}
