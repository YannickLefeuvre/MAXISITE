package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaisonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Maison.class);
        Maison maison1 = new Maison();
        maison1.setId(1L);
        Maison maison2 = new Maison();
        maison2.setId(maison1.getId());
        assertThat(maison1).isEqualTo(maison2);
        maison2.setId(2L);
        assertThat(maison1).isNotEqualTo(maison2);
        maison1.setId(null);
        assertThat(maison1).isNotEqualTo(maison2);
    }
}
