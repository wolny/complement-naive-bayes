package com.dawanda.classifier.naivebayes.model;

import com.dawanda.document.Category;
import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * Created by awolny on 07/12/14.
 */
public class NaiveBayesModel implements Model {
    /**
     * THIS IS THE MODEL: Per each category we're storing the the term weights
     * transformed according to: http://people.csail.mit.edu/jrennie/papers/icml03-nb.pdf
     */
    private final Map<Category, TermWeights> perClassTermWeights;

    public NaiveBayesModel(Map<Category, TermWeights> perClassTermWeights) {
        this.perClassTermWeights = ImmutableMap.copyOf(perClassTermWeights);
    }

    /**
     * Returns the actual Naive Bayes parameters.
     *
     * @return Weighted Complement Naive Bayes parameters estimated during the training set
     */
    public Map<Category, TermWeights> getPerClassTermWeights() {
        return perClassTermWeights;
    }

    /**
     * Wrapper for term weights
     */
    public static class TermWeights {
        private final Map<String, Double> weights;

        @JsonCreator
        public TermWeights(@JsonProperty("weights") Map<String, Double> weights) {
            this.weights = weights;
        }

        public Map<String, Double> getWeights() {
            return weights;
        }

        public double getWeight(String term) {
            Double result = weights.get(term);
            if (result != null) {
                return result;
            }
            return 0.0;
        }

        @Override
        public String toString() {
            return "TermWeights{" +
                    "weights=" + weights +
                    '}';
        }
    }
}
