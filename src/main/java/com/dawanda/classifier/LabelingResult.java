package com.dawanda.classifier;

import com.dawanda.document.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains the result of document classification. See {@link com.dawanda.classifier.DocumentClassifier#label(com.dawanda.document.Document)}
 * for more information. The result is an ordered list of categories and their scores.
 * <p/>
 * Created by awolny on 07/12/14.
 */
public class LabelingResult {
    private final Map<Category, Double> categoryMap;
    private List<ScoredCategory> orderedCategories;

    public LabelingResult(Map<Category, Double> categoryMap) {
        this.categoryMap = categoryMap;
    }

    /**
     * Returns ordered list of categories ordered by their probabilities with the most probable category
     * being the first one in the list.
     *
     * @return ordered list of categories
     */
    public List<ScoredCategory> getOrderedCategories() {
        if (orderedCategories == null) {
            List<ScoredCategory> result = new ArrayList<>();
            for (Map.Entry<Category, Double> entry : categoryMap.entrySet()) {
                result.add(new ScoredCategory(entry.getKey(), entry.getValue()));
            }
            Collections.sort(result);
            orderedCategories = result;
        }
        return orderedCategories;
    }

    public static class ScoredCategory implements Comparable<ScoredCategory> {
        private final Category category;
        private final double score;

        public ScoredCategory(Category category, double score) {
            this.category = category;
            this.score = score;
        }

        public Category getCategory() {
            return category;
        }

        public double getScore() {
            return score;
        }

        // WARN: bare in mind that the best category is the one with the minimum score
        @Override
        public int compareTo(ScoredCategory o) {
            return Double.compare(score, o.getScore());
        }

        @Override
        public String toString() {
            return "ScoredCategory{" +
                    "category=" + category +
                    ", score=" + score +
                    '}';
        }
    }
}
