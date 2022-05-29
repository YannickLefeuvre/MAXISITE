package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SerieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Serie.class);
        Serie serie1 = new Serie();
        serie1.setId(1L);
        Serie serie2 = new Serie();
        serie2.setId(serie1.getId());
        assertThat(serie1).isEqualTo(serie2);
        serie2.setId(2L);
        assertThat(serie1).isNotEqualTo(serie2);
        serie1.setId(null);
        assertThat(serie1).isNotEqualTo(serie2);
    }
}
