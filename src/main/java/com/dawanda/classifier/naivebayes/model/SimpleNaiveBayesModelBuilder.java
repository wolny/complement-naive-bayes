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
public class SimpleNaiveBayesModelBuilder extends AbstractNaiveBayesModelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleNaiveBayesModelBuilder.class);

    @Override
    public NaiveBayesModel buildNaiveBayesModel(NormalizedTermFrequencies[] transformedTermFrequencies) {
        Map<Category, Double> perClassSum = new HashMap<>();
        Map<Category, Map<String, Double>> perClassPerTermSum = new HashMap<>();
        computePerClassSums(transformedTermFrequencies, perClassSum, perClassPerTermSum);
        Map<Category, NaiveBayesModel.TermWeights> weights = computeWeights(perClassSum, perClassPerTermSum);
        return new NaiveBayesModel(weights);
    }

    private void computePerClassSums(NormalizedTermFrequencies[] transformedTermFrequencies, Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        for (NormalizedTermFrequencies termFrequencies : transformedTermFrequencies) {
            updatePerClassSum(perClassSum, termFrequencies);
            updatePerClassPerTermSum(perClassPerTermSum, termFrequencies);
        }
    }

    private Map<Category, NaiveBayesModel.TermWeights> computeWeights(Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        Map<Category, NaiveBayesModel.TermWeights> result = new HashMap<>();
        double denominator = getDenominator(perClassSum);
        int numberOfWords = getNumberOfWords(perClassPerTermSum);
        for (Map.Entry<Category, Map<String, Double>> entry : perClassPerTermSum.entrySet()) {
            Category category = entry.getKey();
            LOG.info(String.format("Computing Term Weights for Category: %s...", category.getId()));
            // because it's complement: subtract the category sum from denominator
            double categoryDenominator = denominator - perClassSum.get(category);
            NaiveBayesModel.TermWeights termWeights = computeTermWeights(category, perClassPerTermSum, categoryDenominator, numberOfWords);
            // We correct for the fact that some classes have greater dependencies by normalizing the weight vectors
            // WARN!!! unfortunately weights normalization decrease model accuracy significantly,
            // probably because of the precision errors. DON'T UNCOMMENT
            // normalizeWeights(termWeights);
            result.put(category, termWeights);
        }
        return result;
    }

    private int getNumberOfWords(Map<Category, Map<String, Double>> perClassPerTermSum) {
        if (perClassPerTermSum == null || perClassPerTermSum.isEmpty()) {
            throw new IllegalStateException("Term weights have not been computed");
        }
        Set<String> words = new HashSet<>();
        for (Map<String, Double> termMap : perClassPerTermSum.values()) {
            words.addAll(termMap.keySet());
        }
        return words.size();
    }

    private double getDenominator(Map<Category, Double> perClassSum) {
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
     * @param category           {@link com.dawanda.document.Category} for which the parameter vector is created
     * @param perClassPerTermSum term frequencies summed per term per class
     * @param denominator        weight normalizer
     * @param numberOfWords      number of words seen during training
     * @return term weights vector encapsulated in {@link com.dawanda.classifier.naivebayes.model.NaiveBayesModel.TermWeights}
     */
    private NaiveBayesModel.TermWeights computeTermWeights(Category category, Map<Category, Map<String, Double>> perClassPerTermSum, double denominator, int numberOfWords) {
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
            double termWeight = Math.log((numerator + 1) / (denominator + numberOfWords));
            weights.put(term, termWeight);
        }

        return new NaiveBayesModel.TermWeights(weights);
    }

    private void updatePerClassSum(Map<Category, Double> perClassSum, NormalizedTermFrequencies termFrequencies) {
        Category category = termFrequencies.getDocument().getCategory();
        Double sum = perClassSum.get(category);
        if (sum == null) {
            sum = 0.0;
        }
        perClassSum.put(category, sum + sumForDocument(termFrequencies));
    }

    private void updatePerClassPerTermSum(Map<Category, Map<String, Double>> perClassPerTermSum, NormalizedTermFrequencies termFrequencies) {
        Category category = termFrequencies.getDocument().getCategory();
        Map<String, Double> map = perClassPerTermSum.get(category);
        if (map == null) {
            map = new HashMap<>();
            perClassPerTermSum.put(category, map);
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


    private double sumForDocument(NormalizedTermFrequencies termFrequencies) {
        double result = 0.0;
        for (String word : termFrequencies.getDocument()) {
            result += termFrequencies.getFrequency(word);
        }
        return result;
    }
}
