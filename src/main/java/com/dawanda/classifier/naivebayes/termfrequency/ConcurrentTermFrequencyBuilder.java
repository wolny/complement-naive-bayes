package com.dawanda.classifier.naivebayes.termfrequency;

import com.dawanda.classifier.naivebayes.NormalizedTermFrequencies;
import com.dawanda.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author slo
 */
public class ConcurrentTermFrequencyBuilder extends AbstractTermFrequencyBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentTermFrequencyBuilder.class);

    private final ForkJoinPool pool;

    public ConcurrentTermFrequencyBuilder(ForkJoinPool pool) {
        this.pool = pool;
    }

    @Override
    public NormalizedTermFrequencies[] transformTermFrequencies(Collection<Document> documents) {
        LOG.info("Normalizing term frequencies for each of {} documents...", documents.size());
        Document[] documentArray = documents.toArray(new Document[0]);
        NormalizedTermFrequencies[] normalizedFrequencies = new NormalizedTermFrequencies[documentArray.length];
        TermFrequencyTransformer transformer = new TermFrequencyTransformer(normalizedFrequencies, documentArray, 0, documentArray.length - 1);
        pool.invoke(transformer);
        return normalizedFrequencies;

    }

    public class TermFrequencyTransformer extends RecursiveAction {
        private final NormalizedTermFrequencies[] frequencies;
        private final Document[] documents;
        private final int lo;
        private final int hi;

        public TermFrequencyTransformer(NormalizedTermFrequencies[] frequencies, Document[] documents, int lo, int hi) {
            this.frequencies = frequencies;
            this.documents = documents;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (lo == hi) {
                Document document = documents[lo];
                frequencies[lo] = new NormalizedTermFrequencies(document, normalizeTermFrequencies(document));
            } else {
                int mid = (lo + hi) / 2;
                invokeAll(new TermFrequencyTransformer(frequencies, documents, lo, mid),
                        new TermFrequencyTransformer(frequencies, documents, mid, hi));
            }
        }
    }

}
