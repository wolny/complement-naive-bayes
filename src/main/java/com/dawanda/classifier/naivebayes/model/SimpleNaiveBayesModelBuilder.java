package com.dawanda.classifier.naivebayes.model;

import com.dawanda.document.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class SimpleNaiveBayesModelBuilder extends AbstractNaiveBayesModelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleNaiveBayesModelBuilder.class);

    @Override
    protected Map<Category, NaiveBayesModel.TermWeights> computeWeights(Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        Map<Category, NaiveBayesModel.TermWeights> result = new HashMap<>();
        double denominator = getDenominator(perClassSum);
        int numberOfWords = getNumberOfWords(perClassPerTermSum);
        for (Map.Entry<Category, Map<String, Double>> entry : perClassPerTermSum.entrySet()) {
            Category category = entry.getKey();
            LOG.info(String.format("Computing Term Weights for Category: %s...", category.getId()));
            // because it's complement: subtract the category sum from denominator
            double categoryDenominator = denominator - perClassSum.get(category);
            NaiveBayesModel.TermWeights termWeights = computeTermWeights(category, perClassPerTermSum, categoryDenominator + numberOfWords);
            // We correct for the fact that some classes have greater dependencies by normalizing the weight vectors
            // WARN!!! unfortunately weights normalization decrease model accuracy significantly,
            // probably because of the precision errors. DON'T UNCOMMENT
            // normalizeWeights(termWeights);
            result.put(category, termWeights);
        }
        return result;
    }

}
