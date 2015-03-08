package com.dawanda.classifier.naivebayes.labels;

import com.dawanda.classifier.naivebayes.model.NaiveBayesModel;
import com.dawanda.document.Category;
import com.dawanda.document.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author slo
 */
public class LabelResultsBuilder {
    public LabelResults buildLabelingResult(NaiveBayesModel model, Document document) {
        if (model == null) {
            throw new IllegalStateException("Classifier must be trained first");
        }
        Map<Category, Double> result = new HashMap<>();

        for (Map.Entry<Category, NaiveBayesModel.TermWeights> entry : model.getPerClassTermWeights().entrySet()) {
            Category category = entry.getKey();
            NaiveBayesModel.TermWeights termWeights = entry.getValue();
            result.put(category, labelDocument(termWeights, document));
        }

        return new LabelResults(result);
    }

    private double labelDocument(NaiveBayesModel.TermWeights termWeights, Document document) {
        double result = 0.0;
        for (String term : document) {
            result += document.getWordCount(term) * termWeights.getWeight(term);
        }
        return result;
    }
}
