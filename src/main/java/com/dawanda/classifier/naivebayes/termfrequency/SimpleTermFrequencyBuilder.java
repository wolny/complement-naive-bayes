package com.dawanda.classifier.naivebayes.termfrequency;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;
import com.dawanda.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SimpleTermFrequencyBuilder extends AbstractTermFrequencyBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTermFrequencyBuilder.class);

    @Override
    public NormalizedTermFrequencies[] transformTermFrequencies(Collection<Document> documents) {
        LOG.info("Normalizing term frequencies for each of {} documents...", documents.size());
        NormalizedTermFrequencies[] normalizedFrequencies = new NormalizedTermFrequencies[documents.size()];
        int i = 0;
        for (Document document : documents) {
            normalizedFrequencies[i++] = normalizedTermFrequencies(document);
        }
        return normalizedFrequencies;
    }
}
