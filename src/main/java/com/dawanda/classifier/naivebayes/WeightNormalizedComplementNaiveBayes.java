package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.classifier.naivebayes.labels.LabelResults;
import com.dawanda.classifier.naivebayes.labels.LabelResultsBuilder;
import com.dawanda.classifier.naivebayes.model.AbstractNaiveBayesModelBuilder;
import com.dawanda.classifier.naivebayes.model.NaiveBayesModel;
import com.dawanda.classifier.naivebayes.model.SimpleNaiveBayesModelBuilder;
import com.dawanda.classifier.naivebayes.termfrequency.AbstractTermFrequencyBuilder;
import com.dawanda.classifier.naivebayes.termfrequency.SimpleTermFrequencyBuilder;
import com.dawanda.document.Document;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Implementation based on: http://people.csail.mit.edu/jrennie/papers/icml03-nb.pdf
 * <p/>
 * Created by awolny on 07/12/14.
 */
public class WeightNormalizedComplementNaiveBayes implements DocumentClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(WeightNormalizedComplementNaiveBayes.class);
    private NaiveBayesModel naiveBayesModel;
    private AbstractTermFrequencyBuilder termFrequencyBuilder;
    private AbstractNaiveBayesModelBuilder naiveBayesModelBuilder;
    private LabelResultsBuilder labelResultsBuilder = new LabelResultsBuilder();

    public WeightNormalizedComplementNaiveBayes(NaiveBayesModel naiveBayesModel) {
        this.naiveBayesModel = naiveBayesModel;
    }

    public WeightNormalizedComplementNaiveBayes() {
        termFrequencyBuilder = new SimpleTermFrequencyBuilder();
        naiveBayesModelBuilder = new SimpleNaiveBayesModelBuilder();
    }

    @Override
    public NaiveBayesModel train(Collection<Document> documents) {
        Preconditions.checkArgument(naiveBayesModel == null, "Already trained!");
        LOG.info(String.format("Training Complement Naive Bayes with %d documents...", documents.size()));
        NormalizedTermFrequencies[] normalizedTermFrequencies = termFrequencyBuilder.transformTermFrequencies(documents);
        naiveBayesModel = naiveBayesModelBuilder.buildNaiveBayesModel(normalizedTermFrequencies);
        return naiveBayesModel;
    }

    @Override
    public LabelResults label(Document document) {
        return labelResultsBuilder.buildLabelingResult(naiveBayesModel, document);
    }

    @Override
    public NaiveBayesModel getModel() {
        Preconditions.checkArgument(naiveBayesModel != null, "Classifier must be trained first");
        return naiveBayesModel;
    }
}
