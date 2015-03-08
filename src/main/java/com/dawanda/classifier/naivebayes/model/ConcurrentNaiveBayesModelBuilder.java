package com.dawanda.classifier.naivebayes.model;

import com.dawanda.document.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ConcurrentNaiveBayesModelBuilder extends AbstractNaiveBayesModelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentNaiveBayesModelBuilder.class);
    private final ForkJoinPool pool;
    private double denominator;
    private int numberOfWords;

    public ConcurrentNaiveBayesModelBuilder(ForkJoinPool pool) {
        this.pool = pool;
    }

    @Override
    protected Map<Category, NaiveBayesModel.TermWeights> computeWeights(Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        denominator = getDenominator(perClassSum);
        numberOfWords = getNumberOfWords(perClassPerTermSum);

        Category[] categories = perClassSum.keySet().toArray(new Category[0]);
        NaiveBayesModel.TermWeights[] termWeights = new NaiveBayesModel.TermWeights[categories.length];
        pool.invoke(new TermWeightsTask(categories, termWeights, 0, categories.length, perClassSum, perClassPerTermSum));

        Map<Category, NaiveBayesModel.TermWeights> result = new HashMap<>();
        for (int i = 0; i < categories.length; i++) {
            result.put(categories[i], termWeights[i]);
        }
        return result;
    }

    private class TermWeightsTask extends RecursiveAction {
        private final Category[] categories;
        private final NaiveBayesModel.TermWeights[] termWeights;
        private final int lo;
        private final int hi;
        private final Map<Category, Double> perClassSum;
        private final Map<Category, Map<String, Double>> perClassPerTermSum;

        private TermWeightsTask(Category[] categories, NaiveBayesModel.TermWeights[] termWeights, int lo, int hi, Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
            this.categories = categories;
            this.termWeights = termWeights;
            this.lo = lo;
            this.hi = hi;
            this.perClassSum = perClassSum;
            this.perClassPerTermSum = perClassPerTermSum;
        }


        @Override
        protected void compute() {
            if (hi - lo <= 1) {
                Category category = categories[lo];
                LOG.info(String.format("Computing Term Weights for Category: %s...", category.getId()));
                double categoryDenominator = denominator - perClassSum.get(category);
                termWeights[lo] = computeTermWeights(category, perClassPerTermSum, categoryDenominator + numberOfWords);
            } else {
                int mid = (lo + hi) / 2;
                invokeAll(new TermWeightsTask(categories, termWeights, lo, mid, perClassSum, perClassPerTermSum),
                        new TermWeightsTask(categories, termWeights, mid, hi, perClassSum, perClassPerTermSum));
            }

        }
    }
}
