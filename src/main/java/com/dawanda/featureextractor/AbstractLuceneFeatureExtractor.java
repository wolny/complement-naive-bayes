package com.dawanda.featureextractor;

import com.dawanda.db.Product;
import com.dawanda.document.Category;
import com.dawanda.document.Document;
import com.dawanda.document.DocumentImpl;
import com.dawanda.featureextractor.tokenizer.EnglishLuceneTokenizer;
import com.dawanda.featureextractor.tokenizer.Tokenizer;
import com.google.common.base.Function;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by awolny on 10/12/14.
 */
public abstract class AbstractLuceneFeatureExtractor implements FeatureExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLuceneFeatureExtractor.class);
    private final Tokenizer tokenizer;

    public AbstractLuceneFeatureExtractor(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public AbstractLuceneFeatureExtractor() {
        this(new EnglishLuceneTokenizer());
    }

    private final Function<Product, Document> prodToDoc = new Function<Product, Document>() {
        @Override
        public Document apply(Product input) {
            List<String> tokens = tokenizer.tokenize(textForProduct(input));
            String categoryId = String.valueOf(input.getCategory());
            String documentId = String.valueOf(input.getId());
            return new DocumentImpl(tokens, documentId, new Category(categoryId));
        }
    };

    @Override
    public List<Document> extractFeatureVectors(List<Product> products) {
        LOG.info(String.format("Extracting features for %d products...", products.size()));
        ArrayList<Document> result = Lists.newArrayList(Iterables.transform(products, prodToDoc));
        LOG.info(">> Categories distribution: \n" + getDistStr(result));
        return result;
    }

    @Override
    public Document extractFeatureVector(String text) {
        return new DocumentImpl(tokenizer.tokenize(text), null, null);
    }

    private String getDistStr(List<Document> documents) {
        Multiset<Category> distribution = getDist(documents);
        StringBuilder sb = new StringBuilder();
        for (Category category : Multisets.copyHighestCountFirst(distribution).elementSet()) {
            sb.append(String.format("\t%s: %d\n", category, distribution.count(category)));
        }

        return sb.toString();
    }

    private Multiset<Category> getDist(List<Document> documents) {
        Multiset<Category> result = HashMultiset.create();
        for (Document document : documents) {
            result.add(document.getCategory());
        }
        return result;
    }


    abstract String textForProduct(Product product);
}
