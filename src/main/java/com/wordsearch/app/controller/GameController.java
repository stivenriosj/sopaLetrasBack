package com.wordsearch.app.controller;

import com.wordsearch.app.service.GameService;
import com.wordsearch.app.service.impl.WordValidatorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://sopa-letras-front.vercel.app/"
})
@RestController
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    
    private final WordValidatorService validatorService;

    public GameController(GameService gameService, WordValidatorService validatorService) {
        this.gameService = gameService;
        this.validatorService = validatorService;
    }

    @GetMapping("/generate")
    public Map<String, Object> generateBoard(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String words) {

        List<String> wordList = new ArrayList<>();
        if (words != null && !words.isEmpty()) {
            for (String w : words.split(",")) {
                if (w != null && !w.isBlank()) {
                    wordList.add(w.trim().toUpperCase());
                }
            }
        }

        logger.info("Delegando generación de tablero de tamaño {} con {} palabras al servicio", size, wordList.size());

        return gameService.generateBoard(size, wordList);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateWord(@RequestBody Map<String, Object> payload) {
        if (payload == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "payload is required"));
        }

        Object boardObj = payload.get("board");
        Object wordObj = payload.get("word");

        if (boardObj == null || wordObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "'board' and 'word' are required in payload"));
        }

        // Normalize board to List<String>
        List<String> board;
        try {
            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) boardObj;
            board = new ArrayList<>();
            for (Object row : raw) {
                if (row == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "board rows must be non-null strings"));
                }
                board.add(row.toString());
            }
        } catch (ClassCastException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "board must be a list/array of strings"));
        }

        String word = wordObj.toString();
        if (word.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "word must be a non-empty string"));
        }

        // Validate board shape: at least one row and all rows same length
        if (board.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "board must contain at least one row"));
        }

        int cols = board.get(0).length();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).length() != cols) {
                return ResponseEntity.badRequest().body(Map.of("error", "all board rows must have the same length"));
            }
        }

        logger.debug("Validando palabra '{}' en tablero de {}x{}", word, board.size(), cols);

        var result = validatorService.validateWord(board, word);
        return ResponseEntity.ok(result);
    }
}