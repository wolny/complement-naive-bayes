package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.exception.DuplicatedTermException;
import com.dawanda.document.Document;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.HashMap;
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

    private NormalizedTermFrequencies(Document document) {
        this.document = document;
        termFrequencies = new HashMap<>();
    }

    public NormalizedTermFrequencies(Document document, Map<String, Double> termFrequencies) {
        this.document = document;
        this.termFrequencies = ImmutableMap.copyOf(termFrequencies);
    }

    /**
     * Creates a {@link NormalizedTermFrequencies}
     * for a given document and performs set of transforms for term frequencies.
     * The following transformations are applied:
     * <ul>
     * <li>TF transform - adjust multinomial model to be proportional to power law distribution, which better approximates the term distribution</li>
     * <li>IDF transform - discount terms that occur in many documents</li>
     * <li>document length normalization</li>
     * </ul>
     *
     * @param document  document which term frequencies are to be transformed
     * @param documents all training documents
     * @return instance of {@link NormalizedTermFrequencies}
     * with term frequencies transformed.
     */
    public static NormalizedTermFrequencies forDocument(Document document, Collection<Document> documents) {
        NormalizedTermFrequencies result = new NormalizedTermFrequencies(document);
        return result
                .tfTransform()
                //.idfTransform(documents)
                .normalize();
    }

    public Document getDocument() {
        return document;
    }

    public double getFrequency(String term) {
        return termFrequencies.get(term);
    }

    private NormalizedTermFrequencies tfTransform() {
        for (String term : getDocument()) {
            if (termFrequencies.containsKey(term)) {
                String msg = String.format("Term: %s duplicated in document: %s", term, getDocument().getId());
                throw new DuplicatedTermException(msg);
            }
            termFrequencies.put(term, Math.log(document.getWordCount(term) + 1.0));
        }
        return this;
    }

    private NormalizedTermFrequencies idfTransform(Collection<Document> documents) {
        for (Map.Entry<String, Double> entry : termFrequencies.entrySet()) {
            entry.setValue(entry.getValue() * idfCoeff(entry.getKey(), documents));
        }
        return this;
    }

    private double idfCoeff(String term, Collection<Document> documents) {
        int sum = 0;
        for (Document document : documents) {
            if (document.contains(term)) {
                sum += 1;
            }
        }
        if (sum == 0) {
            throw new IllegalStateException("Term must be present in at least one document");
        }
        return Math.log(1.0 / sum);
    }

    private NormalizedTermFrequencies normalize() {
        double sumOfSquares = 0.0;
        for (double freq : termFrequencies.values()) {
            sumOfSquares += freq * freq;
        }
        double denominator = Math.sqrt(sumOfSquares);
        for (Map.Entry<String, Double> entry : termFrequencies.entrySet()) {
            entry.setValue(entry.getValue() / denominator);
        }
        return this;
    }
}
