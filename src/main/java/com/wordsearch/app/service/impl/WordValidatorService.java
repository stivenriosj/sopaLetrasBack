package com.wordsearch.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WordValidatorService {

    private static final int[][] DIRECTIONS = {
        {0, 1},   // derecha
        {0, -1},  // izquierda
        {1, 0},   // abajo
        {-1, 0},  // arriba
        {1, 1},   // diagonal ↘
        {1, -1},  // diagonal ↙
        {-1, 1},  // diagonal ↗
        {-1, -1}  // diagonal ↖
    };

    private static final String[] DIRECTION_NAMES = {
        "right", "left", "down", "up", "diag_down_right", "diag_down_left", "diag_up_right", "diag_up_left"
    };

    public ValidationResult validateWord(List<String> board, String word) {
        if (board == null || board.isEmpty() || word == null || word.isBlank()) {
            return new ValidationResult(false, null, null, null);
        }

        int rows = board.size();
        int cols = board.get(0).length();
        word = word.toUpperCase();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int d = 0; d < DIRECTIONS.length; d++) {
                    int dr = DIRECTIONS[d][0];
                    int dc = DIRECTIONS[d][1];
                    if (matches(board, word, i, j, dr, dc, rows, cols)) {
                        int endRow = i + dr * (word.length() - 1);
                        int endCol = j + dc * (word.length() - 1);
                        return new ValidationResult(true, DIRECTION_NAMES[d], new int[]{i, j}, new int[]{endRow, endCol});
                    }
                }
            }
        }

        return new ValidationResult(false, null, null, null);
    }

    private boolean matches(List<String> board, String word, int row, int col, int dr, int dc, int rows, int cols) {
        for (int k = 0; k < word.length(); k++) {
            int r = row + dr * k;
            int c = col + dc * k;

            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                return false;
            }

            char boardChar = board.get(r).charAt(c);
            if (Character.toUpperCase(boardChar) != word.charAt(k)) {
                return false;
            }
        }

        return true;
    }

    public static record ValidationResult(boolean found, String direction, int[] start, int[] end) {}
}