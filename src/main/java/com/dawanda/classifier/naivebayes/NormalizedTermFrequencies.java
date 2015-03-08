package com.dawanda.classifier.naivebayes;

import com.dawanda.document.Document;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Represents transformed term frequencies for a given document.
 * The following transformations are applied for each term:
 * <ul>
 * <li>TF transform - adjust multinomial model to be proportional to power law distribution, which better approximates the term distribution</li>
 * <li>IDF transform - discount terms that occur in many documents</li>
 * <li>document length normalization</li>
 * </ul>
 */
public class NormalizedTermFrequencies {
    private final Document document;
    private final Map<String, Double> termFrequencies;

    public NormalizedTermFrequencies(Document document, Map<String, Double> termFrequencies) {
        this.document = document;
        this.termFrequencies = ImmutableMap.copyOf(termFrequencies);
    }

    public Document getDocument() {
        return document;
    }

    public double getFrequency(String term) {
        return termFrequencies.get(term);
    }
}
