package com.dawanda.classifier;

import com.dawanda.classifier.naivebayes.NaiveBayesModel;
import com.dawanda.classifier.naivebayes.WeightNormalizedComplementNaiveBayes;
import com.dawanda.db.Product;
import com.dawanda.document.Document;
import com.dawanda.featureextractor.filter.ProductFilterPipeline;
import com.dawanda.featureextractor.filter.RandomSubsetFilter;
import com.dawanda.featureextractor.filter.SameSellerFilter;
import com.dawanda.utils.Extractors;
import com.dawanda.utils.NaiveBayesSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Trains the Naive Bayes with selected documents and saves the model to /model.cbayes.json
 * <p/>
 * Created by awolny on 10/12/14.
 */
public class ClassifierTrainer {
    public static void main(String[] args) throws IOException {
        ProductFilterPipeline filter = new ProductFilterPipeline(Arrays.asList(new RandomSubsetFilter(1.0), new SameSellerFilter(3)));
        List<Product> products = filter.filterProducts("./productsEN");
        List<Document> documents = Extractors.STANDARD_EXTRACTOR.extractFeatureVectors(products);
        WeightNormalizedComplementNaiveBayes classifier = new WeightNormalizedComplementNaiveBayes();
        NaiveBayesModel model = classifier.train(documents);
        NaiveBayesSerializer.writeTo(model, "./model/cbayes.json");
    }
}
