package com.wordsearch.app.service;

import java.util.List;
import java.util.Map;

public interface GameService {
    Map<String, Object> generateBoard(int size, List<String> words);
}
