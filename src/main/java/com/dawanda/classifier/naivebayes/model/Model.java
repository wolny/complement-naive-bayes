package com.dawanda.classifier.naivebayes.model;

/**
 * Just a marking interface for the model returned by the {@link com.dawanda.classifier.DocumentClassifier}.
 * Implementations should contain complete set of parameters estimated during the classifier training phase.
 * Model should be serializable (JSON, XML, binary, etc.) so it can be persisted and reused by
 * the {@link com.dawanda.classifier.DocumentClassifier} without the need to train the classifier again.
 * <p/>
 * Created by awolny on 07/12/14.
 */
public interface Model {
}
