package com.dawanda.classifier.naivebayes.model;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;
import com.dawanda.document.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author slo
 */
public abstract class AbstractNaiveBayesModelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNaiveBayesModelBuilder.class);

    public NaiveBayesModel buildNaiveBayesModel(NormalizedTermFrequencies[] normalizedTermFrequencies) {
        LOG.info("Computing per category sums...");
        Map<Category, Double> perClassSum = computePerClassSum(normalizedTermFrequencies);
        LOG.info("computing per term per category sums...");
        Map<Category, Map<String, Double>> perClassPerTermSum = computePerClassPerTermSum(normalizedTermFrequencies);
        LOG.info("Computing term weights...");
        Map<Category, NaiveBayesModel.TermWeights> weights = computeWeights(perClassSum, perClassPerTermSum);
        return new NaiveBayesModel(weights);
    }

    protected abstract Map<Category, NaiveBayesModel.TermWeights>
    computeWeights(Map<Category, Double> perClassSum,
                   Map<Category, Map<String, Double>> perClassPerTermSum);

    protected Map<Category, Double> computePerClassSum(NormalizedTermFrequencies[] normalizedTermFrequencies) {
        Map<Category, Double> result = new HashMap<>();
        for (NormalizedTermFrequencies termFrequencies : normalizedTermFrequencies) {
            Category category = termFrequencies.getDocument().getCategory();
            Double sum = result.get(category);
            if (sum == null) {
                sum = 0.0;
            }
            result.put(category, sum + termFrequencies.getTermFrequencySum());
        }
        return result;
    }

    protected Map<Category, Map<String, Double>> computePerClassPerTermSum(NormalizedTermFrequencies[] normalizedTermFrequencies) {
        Map<Category, Map<String, Double>> result = new HashMap<>();
        for (NormalizedTermFrequencies termFrequencies : normalizedTermFrequencies) {
            Category category = termFrequencies.getDocument().getCategory();
            Map<String, Double> map = result.get(category);
            if (map == null) {
                result.put(category, map = new HashMap<>());
            }
            for (String term : termFrequencies.getDocument()) {
                Double sum = map.get(term);
                if (sum == null) {
                    sum = 0.0;
                }
                sum += termFrequencies.getFrequency(term);
                map.put(term, sum);
            }
        }
        return result;
    }

    /**
     * Counts number of words in the whole corpus represented by {@code perClassPerTermSum} map
     *
     * @param perClassPerTermSum intermediate representation of Naive Bayes model
     * @return number of words
     */
    protected int getNumberOfWords(Map<Category, Map<String, Double>> perClassPerTermSum) {
        Set<String> words = new HashSet<>();
        for (Map<String, Double> termMap : perClassPerTermSum.values()) {
            words.addAll(termMap.keySet());
        }
        return words.size();
    }

    /**
     * Returns the sum of all class 'probabilities'. The sum is used as a normalizer for model parameters.
     *
     * @param perClassSum map of class 'probabilities' (since this is a complement Naive Bayes those are
     *                    not exactly probabilities according to the classic definition, but those values correspond
     *                    to the probabilities in the classic Naive Bayes)
     * @return sum of 'probabilities'
     */
    protected double getDenominator(Map<Category, Double> perClassSum) {
        double result = 0.0;
        for (double sum : perClassSum.values()) {
            result += sum;
        }
        return result;
    }

    /**
     * Computes term weights for a given category. In a classical form of Naive Bayes the weight for a term
     * is the probability that the term occurs in the class.
     *
     * @param category         {@link com.dawanda.document.Category} for which the parameter vector is created
     * @param weightNormalizer weight normalizer
     * @return term weights vector encapsulated in {@link com.dawanda.classifier.naivebayes.model.NaiveBayesModel.TermWeights}
     */
    protected NaiveBayesModel.TermWeights computeTermWeights(Category category, Map<Category, Map<String, Double>> perClassPerTermSum, double weightNormalizer) {
        Map<String, Double> weights = new HashMap<>();
        // for all terms in a given category
        for (String term : perClassPerTermSum.get(category).keySet()) {
            double numerator = 0.0;
            for (Map.Entry<Category, Map<String, Double>> entry : perClassPerTermSum.entrySet()) {
                // for all categories except 'category'
                if (!category.equals(entry.getKey())) {
                    Double inCategoryWeight = entry.getValue().get(term);
                    if (inCategoryWeight == null) {
                        inCategoryWeight = 0.0;
                    }
                    numerator += inCategoryWeight;
                }
            }
            // add smoothing parameters _alpha_i = 1
            double termWeight = Math.log((numerator + 1) / weightNormalizer);
            weights.put(term, termWeight);
        }

        return new NaiveBayesModel.TermWeights(weights);
    }
}
