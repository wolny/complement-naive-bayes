package com.dawanda.classifier.naivebayes.termfrequency;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;
import com.dawanda.document.Document;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractTermFrequencyBuilder {
    public abstract NormalizedTermFrequencies[] transformTermFrequencies(Collection<Document> documents);

    protected NormalizedTermFrequencies normalizedTermFrequencies(Document document) {
        Map<String, Double> normalizedFrequencies = Maps.newHashMap();
        double sumOfSquares = 0.0;
        for (String term : document) {
            double value = Math.log(document.getWordCount(term) + 1.0);
            normalizedFrequencies.put(term, value);
            sumOfSquares += value * value;
        }
        double denominator = Math.sqrt(sumOfSquares);
        // normalize and compute sum of term frequencies
        double termFrequencySum = 0.0;
        for (Map.Entry<String, Double> entry : normalizedFrequencies.entrySet()) {
            double normalizedValue = entry.getValue() / denominator;
            entry.setValue(normalizedValue);
            termFrequencySum += normalizedValue;
        }
        return new NormalizedTermFrequencies(document, normalizedFrequencies, termFrequencySum);
    }
}
