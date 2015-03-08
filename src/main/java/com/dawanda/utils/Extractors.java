package com.dawanda.utils;

import com.dawanda.featureextractor.FeatureExtractor;
import com.dawanda.featureextractor.TitleAndDescriptionFeatureExtractor;
import com.dawanda.featureextractor.tokenizer.DefaultLuceneTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class Extractors {
    public static final FeatureExtractor STANDARD_ENGLISH_EXTRACTOR = new TitleAndDescriptionFeatureExtractor();
    public static final FeatureExtractor STANDARD_EXTRACTOR = new TitleAndDescriptionFeatureExtractor(new DefaultLuceneTokenizer(new StandardAnalyzer()));
    // TODO: put more!!!

    private Extractors() {
    }
}
