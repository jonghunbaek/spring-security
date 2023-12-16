package com.example.study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class StringTest {

    @DisplayName("startWith() 대소문자 구분 테스트")
    @Test
    void test() {
        // given
        String sample1 = "abc";
        String sample2 = "ABC";

        // when & then
        assertThat(sample1.startsWith("ab")).isTrue();
        assertThat(sample2.startsWith("aB")).isTrue();
    }
}
