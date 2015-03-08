package com.dawanda.featureextractor.tokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DefaultLuceneTokenizer implements Tokenizer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLuceneTokenizer.class);
    private final Analyzer analyzer;
    private final static String NOFIELD = "";

    public DefaultLuceneTokenizer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public List<String> tokenize(String text) {
        ArrayList<String> list = new ArrayList<>();
        try {
            TokenStream tokenStream = analyzer.tokenStream(NOFIELD, new StringReader(text));
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                list.add(charTermAttribute.toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            LOG.error("Cannot tokenize text: " + text, e);
        }
        return list;
    }
}
