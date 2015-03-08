package com.dawanda.featureextractor;

import com.dawanda.db.Product;

public class TitleFeatureExtractor extends AbstractLuceneFeatureExtractor {
    @Override
    String textForProduct(Product product) {
        return product.getTitle();
    }
}
