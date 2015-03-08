package com.dawanda.classifier.naivebayes.model;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;

/**
 * @author slo
 */
public abstract class AbstractNaiveBayesModelBuilder {
    public abstract NaiveBayesModel buildNaiveBayesModel(NormalizedTermFrequencies[] transformedTermFrequencies);
}
