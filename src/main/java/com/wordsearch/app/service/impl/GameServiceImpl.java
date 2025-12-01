package com.wordsearch.app.service.impl;

import com.wordsearch.app.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private static final String LETTERS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
    private final Random random = new Random();

    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    record WordPlacement(String word, int startRow, int startCol, String direction) {}

    @Override
    public Map<String, Object> generateBoard(int size, List<String> words) {
        // board inicial con char nulos (\u0000). Colocamos las palabras y luego llenamos con letras aleatorias.
        char[][] board = new char[size][size];

        List<WordPlacement> placements = new ArrayList<>();
        List<String> wordList = new ArrayList<>();

        if (words != null) {
            for (String w : words) {
                if (w != null && !w.isBlank()) {
                    wordList.add(w.trim().toUpperCase());
                }
            }
        }

        logger.info("Generando tablero de tamaño {} con {} palabras", size, wordList.size());

        for (String word : wordList) {
            boolean reversed = random.nextBoolean();
            String finalWord = reversed ? new StringBuilder(word).reverse().toString() : word;
            WordPlacement placement = insertWord(board, finalWord);
            if (placement != null) {
                placements.add(placement);
            }
        }

        // Llenar los espacios vacíos con letras aleatorias después de intentar colocar todas las palabras
        fillRemainingWithRandomLetters(board);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("board", boardToList(board));
        response.put("placements", placements);

        return response;
    }

    private void fillRemainingWithRandomLetters(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == '\u0000') {
                    board[i][j] = LETTERS.charAt(random.nextInt(LETTERS.length()));
                }
            }
        }
    }

    private WordPlacement insertWord(char[][] board, String word) {
        int size = board.length;
        int attempts = 0;

        while (attempts < 500) {
            attempts++;

            int row = random.nextInt(size);
            int col = random.nextInt(size);
            Direction direction = Direction.randomDirection(random);

            if (canPlaceWord(board, word, row, col, direction)) {
                placeWord(board, word, row, col, direction);
                return new WordPlacement(word, row, col, direction.name());
            }
        }

        logger.warn("No se pudo colocar la palabra: {} después de {} intentos", word, 500);
        return null;
    }

    private boolean canPlaceWord(char[][] board, String word, int row, int col, Direction dir) {
        int size = board.length;

        for (int i = 0; i < word.length(); i++) {
            int r = row + (dir.rowStep * i);
            int c = col + (dir.colStep * i);

            if (r < 0 || r >= size || c < 0 || c >= size) {
                return false;
            }

            char existing = board[r][c];
            char needed = word.charAt(i);
            // permitido si la celda está vacía o contiene la misma letra (cruce)
            if (existing != '\u0000' && existing != needed) {
                return false;
            }
        }

        return true;
    }

    private void placeWord(char[][] board, String word, int row, int col, Direction dir) {
        for (int i = 0; i < word.length(); i++) {
            int r = row + (dir.rowStep * i);
            int c = col + (dir.colStep * i);
            board[r][c] = word.charAt(i);
        }
    }

    private List<String> boardToList(char[][] board) {
        List<String> result = new ArrayList<>();
        for (char[] row : board) {
            result.add(new String(row));
        }
        return result;
    }

    enum Direction {
        N(-1, 0),
        NE(-1, 1),
        E(0, 1),
        SE(1, 1),
        S(1, 0),
        SW(1, -1),
        W(0, -1),
        NW(-1, -1);

        final int rowStep;
        final int colStep;

        Direction(int rowStep, int colStep) {
            this.rowStep = rowStep;
            this.colStep = colStep;
        }

        static Direction randomDirection(Random rnd) {
            Direction[] values = values();
            return values[rnd.nextInt(values.length)];
        }
    }
}