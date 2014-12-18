package com.dawanda.featureextractor;

import com.dawanda.db.Product;

/**
 * Created by awolny on 10/12/14.
 */
public class TitleFeatureExtractor extends AbstractLuceneFeatureExtractor {
    @Override
    String textForProduct(Product product) {
        return product.getTitle();
    }
}
