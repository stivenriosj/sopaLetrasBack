package com.wordsearch.app.service.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class WordValidatorServiceTest {

    private final WordValidatorService validator = new WordValidatorService();

    @Test
    void findsWordHorizontallyRight() {
        List<String> board = List.of(
            "ABCDE",
            "FGHIJ",
            "KLMNO",
            "PQRST",
            "UVWXY"
        );

        var res = validator.validateWord(board, "GHI");
        assertTrue(res.found());
        assertEquals("right", res.direction());
        assertArrayEquals(new int[]{1,1}, res.start());
        assertArrayEquals(new int[]{1,3}, res.end());
    }

    @Test
    void findsWordHorizontallyLeft() {
        List<String> board = List.of(
            "ABCDE",
            "FGHIJ",
            "KLMNO",
            "PQRST",
            "UVWXY"
        );

        var res = validator.validateWord(board, "IHG");
        assertTrue(res.found());
        assertEquals("left", res.direction());
        assertArrayEquals(new int[]{1,3}, res.start());
        assertArrayEquals(new int[]{1,1}, res.end());
    }

    @Test
    void findsWordVerticallyDown() {
        List<String> board = List.of(
            "AXXXX",
            "BXXXX",
            "CXXXX",
            "DXXXX",
            "EXXXX"
        );

        var res = validator.validateWord(board, "ABCDE");
        assertTrue(res.found());
        assertEquals("down", res.direction());
        assertArrayEquals(new int[]{0,0}, res.start());
        assertArrayEquals(new int[]{4,0}, res.end());
    }

    @Test
    void findsWordVerticallyUp() {
        List<String> board = List.of(
            "AXXXX",
            "BXXXX",
            "CXXXX",
            "DXXXX",
            "EXXXX"
        );

        var res = validator.validateWord(board, "EDCBA");
        assertTrue(res.found());
        assertEquals("up", res.direction());
        assertArrayEquals(new int[]{4,0}, res.start());
        assertArrayEquals(new int[]{0,0}, res.end());
    }

    @Test
    void findsWordDiagonallyDownRight() {
        List<String> board = List.of(
            "Axx",
            "xBz",
            "xxC"
        );

        var res = validator.validateWord(board, "ABC");
        assertTrue(res.found());
        assertEquals("diag_down_right", res.direction());
        assertArrayEquals(new int[]{0,0}, res.start());
        assertArrayEquals(new int[]{2,2}, res.end());
    }

    @Test
    void returnsNotFoundWhenMissing() {
        List<String> board = List.of(
            "ABCDE",
            "FGHIJ",
            "KLMNO",
            "PQRST",
            "UVWXY"
        );

        var res = validator.validateWord(board, "ZZZ");
        assertFalse(res.found());
    }
}