package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoyaumeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Royaume.class);
        Royaume royaume1 = new Royaume();
        royaume1.setId(1L);
        Royaume royaume2 = new Royaume();
        royaume2.setId(royaume1.getId());
        assertThat(royaume1).isEqualTo(royaume2);
        royaume2.setId(2L);
        assertThat(royaume1).isNotEqualTo(royaume2);
        royaume1.setId(null);
        assertThat(royaume1).isNotEqualTo(royaume2);
    }
}
