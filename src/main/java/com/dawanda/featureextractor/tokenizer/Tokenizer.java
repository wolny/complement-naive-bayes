package com.dawanda.featureextractor.tokenizer;

import java.util.List;

/**
 * Created by lfundaro on 08/12/14.
 */
public interface Tokenizer {
    List<String> tokenize(String text);
}
