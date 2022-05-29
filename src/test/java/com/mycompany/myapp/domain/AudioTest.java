package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AudioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Audio.class);
        Audio audio1 = new Audio();
        audio1.setId(1L);
        Audio audio2 = new Audio();
        audio2.setId(audio1.getId());
        assertThat(audio1).isEqualTo(audio2);
        audio2.setId(2L);
        assertThat(audio1).isNotEqualTo(audio2);
        audio1.setId(null);
        assertThat(audio1).isNotEqualTo(audio2);
    }
}
