package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.classifier.LabelingResult;
import com.dawanda.document.Document;
import com.dawanda.utils.Extractors;

import java.io.IOException;

/**
 * Created by lfundaro on 12/12/14.
 */
public class NaiveBayesClient {

    private static final DocumentClassifier classifier = initClassifier();

    public static LabelingResult labelInput(String title, String description) {
        Document doc = Extractors.STANDARD_EXTRACTOR.extractFeatureVector(title + " " + description);
        return classifier.label(doc);
    }

    private static DocumentClassifier initClassifier() {
        DocumentClassifier classifier = null;
        try {
            classifier = DocumentClassifierFactory.buildNaiveBayesClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classifier;
    }
}
