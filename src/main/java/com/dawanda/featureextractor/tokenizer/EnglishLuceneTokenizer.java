package com.dawanda.featureextractor.tokenizer;

import com.google.common.base.Throwables;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnglishLuceneTokenizer implements Tokenizer {
    private final Analyzer analyzer = new StandardAnalyzer();
    //private final Analyzer analyzer = new StandardAnalyzer(CustomStopWords.ENGLISH_STOP_WORDS_SET);

    @Override
    public List<String> tokenize(String text) {
        List<String> result = new ArrayList<>();
        try {
            EnglishStemmer englishStemmer = new EnglishStemmer();
            TokenStream tokenStream = analyzer.tokenStream(null, text);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = tokenStream.getAttribute(CharTermAttribute.class).toString();
                englishStemmer.setCurrent(term);
                englishStemmer.stem();
                result.add(englishStemmer.getCurrent());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return result;
    }
}
