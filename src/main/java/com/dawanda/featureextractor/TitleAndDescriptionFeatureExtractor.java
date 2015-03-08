package com.dawanda.featureextractor;

import com.dawanda.db.Product;
import com.dawanda.featureextractor.tokenizer.Tokenizer;

public class TitleAndDescriptionFeatureExtractor extends AbstractLuceneFeatureExtractor {
    public TitleAndDescriptionFeatureExtractor() {
    }

    public TitleAndDescriptionFeatureExtractor(Tokenizer tokenizer) {
        super(tokenizer);
    }

    @Override
    String textForProduct(Product product) {
        return product.getTitle() + " " + product.getDescription();
    }
}
