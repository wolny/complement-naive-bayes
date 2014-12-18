package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.utils.NaiveBayesSerializer;

import java.io.IOException;
import java.net.URL;

/**
 * Created by lfundaro on 12/12/14.
 */
public class DocumentClassifierFactory {
    public static final String DEFAULT_MODEL_PATH = "cbayes.json";

    /**
     * Default initialization with model under resources.
     * The model is provided without training though.
     */
    public static DocumentClassifier buildNaiveBayesClassifier() throws IOException {
        URL pathToModel = DocumentClassifierFactory.class.getClassLoader().getResource(DEFAULT_MODEL_PATH);
        NaiveBayesModel model = NaiveBayesSerializer.readFrom(pathToModel.getPath());
        DocumentClassifier classifier = new WeightNormalizedComplementNaiveBayes(model);
        return classifier;
    }

}
