package com.dawanda.featureextractor.tokenizer;

import java.util.List;

public interface Tokenizer {
    List<String> tokenize(String text);
}
