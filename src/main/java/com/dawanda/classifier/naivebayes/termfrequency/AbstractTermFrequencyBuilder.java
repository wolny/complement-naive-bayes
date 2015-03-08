package com.dawanda.classifier.naivebayes.termfrequency;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;
import com.dawanda.document.Document;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * @author slo
 */
public abstract class AbstractTermFrequencyBuilder {
    public abstract NormalizedTermFrequencies[] transformTermFrequencies(Collection<Document> documents);

    protected Map<String, Double> normalizeTermFrequencies(Document document) {
        Map<String, Double> normalizedFrequencies = Maps.newHashMap();
        double sumOfSquares = 0.0;
        for (String term : document) {
            double value = Math.log(document.getWordCount(term) + 1.0);
            normalizedFrequencies.put(term, value);
            sumOfSquares += value * value;
        }
        double denominator = Math.sqrt(sumOfSquares);
        for (Map.Entry<String, Double> entry : normalizedFrequencies.entrySet()) {
            entry.setValue(entry.getValue() / denominator);
        }
        return normalizedFrequencies;
    }
}
