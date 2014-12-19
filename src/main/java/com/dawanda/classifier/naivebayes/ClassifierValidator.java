package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.classifier.LabelingResult;
import com.dawanda.db.Product;
import com.dawanda.document.Category;
import com.dawanda.document.Document;
import com.dawanda.featureextractor.filter.ProductFilter;
import com.dawanda.featureextractor.filter.ProductFilterPipeline;
import com.dawanda.featureextractor.filter.RandomSubsetFilter;
import com.dawanda.utils.Extractors;
import com.dawanda.utils.NaiveBayesSerializer;
import com.dawanda.utils.ProductSerializer;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by awolny on 09/12/14.
 */
public class ClassifierValidator {
    private static final Logger LOG = LoggerFactory.getLogger(ClassifierValidator.class);
    private final String srcDir;
    private final String modelPath;

    public ClassifierValidator(String srcDir, String modelPath) {
        Preconditions.checkNotNull(srcDir);
        Preconditions.checkNotNull(modelPath);
        this.srcDir = srcDir;
        this.modelPath = modelPath;
    }

    /**
     * Validates the classifier by measuring overall and per class accuracy.
     * Results are printed via configured logger.
     *
     * @param fractionOfProducts fraction of products from the {@code srcDir} to be taken for testing
     * @throws IOException
     */
    public void validate(double fractionOfProducts) throws IOException {
        DocumentClassifier classifier = createClassifier();
        List<Product> products = prepareTestSet(srcDir, fractionOfProducts);
        List<Document> testSet = extractFeatures(products);
        checkAccuracy(classifier, testSet);
    }

    /**
     * Labels products from {@code srcDir}. Shows best {@code maxLabels} categories for a given product.
     *
     * @param maxLabels how many top categories to show for a given product
     * @throws IOException
     */
    public void label(int maxLabels) throws IOException {
        DocumentClassifier classifier = createClassifier();
        List<Product> products = ProductSerializer.readFromSourceDir(srcDir);
        List<Document> toBeLabeled = extractFeatures(products);
        printLabels(classifier, toBeLabeled, maxLabels);
    }

    private void printLabels(DocumentClassifier classifier, List<Document> toBeLabeled, int maxLabels) {
        for (Document document : toBeLabeled) {
            LabelingResult labelingResult = classifier.label(document);
            List<LabelingResult.ScoredCategory> topCategories = Lists.newArrayList(Iterables.limit(labelingResult.getOrderedCategories(), maxLabels));
            String result = String.format("Document %s -> %s", document.getId(), topCategories.toString());
            LOG.info(result);
        }
    }

    private DocumentClassifier createClassifier() throws IOException {
        NaiveBayesModel model = NaiveBayesSerializer.readFrom(modelPath);
        return new WeightNormalizedComplementNaiveBayes(model);
    }

    private List<Product> prepareTestSet(String prodDir, double fractionOfProducts) {
        ProductFilter filter = new RandomSubsetFilter(fractionOfProducts);
        ProductFilterPipeline pipeline = new ProductFilterPipeline(Arrays.asList(filter));
        List<Product> products = pipeline.filterProducts(prodDir);
        LOG.info("Test set size: " + products.size());
        return products;
    }

    private List<Document> extractFeatures(List<Product> products) {
        return Extractors.STANDARD_EXTRACTOR.extractFeatureVectors(products);
    }

    private void checkAccuracy(DocumentClassifier classifier, List<Document> testSet) {
        Multiset<Category> success = HashMultiset.create();
        Multiset<Category> all = HashMultiset.create();
        LOG.info(String.format("Labeling %d test documents...", testSet.size()));
        for (Document document : testSet) {
            LabelingResult labelingResult = classifier.label(document);
            Category category = document.getCategory();
            LOG.debug("Expected category: " + category);
            LOG.debug("Labeling result:" + labelingResult.getOrderedCategories());
            all.add(category);
            if (isCorrectlyLabeled(document, labelingResult)) {
                success.add(category);
            } else {
                LOG.debug("Incorrect label for document: " + document.getId());
            }
        }
        printAccuracy(success, all, testSet.size());
    }

    private void printAccuracy(Multiset<Category> success, Multiset<Category> all, int testSize) {
        int successCount = 0;
        List<CategoryAcc> perCategoryAcc = new ArrayList<>();
        for (Category category : all.elementSet()) {
            int numerator = success.count(category);
            successCount += numerator;
            double denominator = all.count(category);
            double catAcc = numerator / denominator;
            perCategoryAcc.add(new CategoryAcc(category, catAcc));
        }
        LOG.info(">>>> Per-category Accuracy:");
        Collections.sort(perCategoryAcc);
        for (CategoryAcc catAcc : perCategoryAcc) {
            LOG.info(catAcc.getCategory() + ", Accuracy: " + catAcc.getAccuracy());
        }
        double acc = successCount / (double) testSize;
        LOG.info(">>>> Overall Accuracy = " + acc + " <<<<");
    }

    private boolean isCorrectlyLabeled(final Document document, LabelingResult labelingResult) {
        int limit = 3; // take first 3 categories
        Iterable<LabelingResult.ScoredCategory> topLabels = Iterables.limit(labelingResult.getOrderedCategories(), limit);
        return Iterables.any(topLabels, new Predicate<LabelingResult.ScoredCategory>() {
            @Override
            public boolean apply(LabelingResult.ScoredCategory input) {
                return document.getCategory().equals(input.getCategory());
            }
        });
    }

    static class CategoryAcc implements Comparable<CategoryAcc> {
        private final Category category;
        private final double accuracy;

        private CategoryAcc(Category category, double accuracy) {
            this.category = category;
            this.accuracy = accuracy;
        }

        public Category getCategory() {
            return category;
        }

        public double getAccuracy() {
            return accuracy;
        }

        @Override
        public int compareTo(CategoryAcc o) {
            Double oAcc = Double.valueOf(o.getAccuracy());
            return oAcc.compareTo(getAccuracy());
        }
    }
}
