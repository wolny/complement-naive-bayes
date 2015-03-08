package com.dawanda.featureextractor;

import com.dawanda.db.Product;
import com.dawanda.document.Document;

import java.util.List;

public interface FeatureExtractor {
    /**
     * Converts a given {@code products} to feature vectors
     *
     * @param products input products
     * @return feature vectors
     */
    List<Document> extractFeatureVectors(List<Product> products);

    /**
     * Covert a given text document to the feature vector
     *
     * @param text text to be converted
     * @return feature vector
     */
    Document extractFeatureVector(String text);
}
