package com.dawanda.classifier;

import com.dawanda.classifier.naivebayes.labels.LabelResults;
import com.dawanda.classifier.naivebayes.model.Model;
import com.dawanda.document.Document;

import java.util.Collection;

/**
 * General interface for the text {@link com.dawanda.document.Document} classification.
 * Sample implementations might include <b>Naive Bayes</b> or <b>Support Vector Machine</b>
 * classifiers.
 * <p/>
 * Created by awolny on 07/12/14.
 */
public interface DocumentClassifier {
    /**
     * Trains the model with a given set of training documents.
     *
     * @param documents collection of training documents
     * @return instance of {@link com.dawanda.classifier.naivebayes.model.Model} containing parameters estimated during traning
     */
    Model train(Collection<Document> documents);

    /**
     * Labels a given document based on the model. Throws exception if the classifier was not trained.
     *
     * @param document instance of {@link com.dawanda.document.Document} to be labeled
     * @return instance of {@link com.dawanda.classifier.naivebayes.labels.LabelResults} containing the most probable categories
     * for a given document.
     * @throws IllegalStateException if {@code this} classifier was not trained.
     */
    LabelResults label(Document document);

    /**
     * Returns a model for {@code this} classifier or throws exception if the classifier was not trained.
     *
     * @return instance of serializable {@link com.dawanda.classifier.naivebayes.model.Model} for {@code this} classifier.
     */
    Model getModel();
}
