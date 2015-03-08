package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.naivebayes.model.NaiveBayesModel;
import com.dawanda.db.Product;
import com.dawanda.document.Document;
import com.dawanda.featureextractor.filter.ProductFilterPipeline;
import com.dawanda.featureextractor.filter.RandomSubsetFilter;
import com.dawanda.featureextractor.filter.SameSellerFilter;
import com.dawanda.utils.Extractors;
import com.dawanda.utils.NaiveBayesSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Trains the Naive Bayes with selected documents and saves the model to /model.cbayes.json
 * <p/>
 * Created by awolny on 10/12/14.
 */
public class ClassifierTrainer {
    private static final Logger LOG = LoggerFactory.getLogger(ClassifierTrainer.class);
    private final String srcDir;
    private final String outputModel;

    public ClassifierTrainer(String srcDir, String outputModel) {
        this.srcDir = srcDir;
        this.outputModel = outputModel;
    }

    public void train() throws IOException {
        LOG.info("Training Complement Naive Bayes...");
        ProductFilterPipeline filter = new ProductFilterPipeline(Arrays.asList(new RandomSubsetFilter(1.0), new SameSellerFilter(3)));
        List<Product> products = filter.filterProducts(srcDir);
        List<Document> documents = Extractors.STANDARD_EXTRACTOR.extractFeatureVectors(products);
        WeightNormalizedComplementNaiveBayes classifier = new WeightNormalizedComplementNaiveBayes();
        NaiveBayesModel model = classifier.train(documents);
        LOG.info("Training completed. Writing model to: " + outputModel);
        NaiveBayesSerializer.writeTo(model, outputModel);
    }
}
