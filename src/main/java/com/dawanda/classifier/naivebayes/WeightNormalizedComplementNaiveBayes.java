package com.dawanda.classifier.naivebayes;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.classifier.LabelingResult;
import com.dawanda.document.Category;
import com.dawanda.document.Document;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation based on: http://people.csail.mit.edu/jrennie/papers/icml03-nb.pdf
 * <p/>
 * Created by awolny on 07/12/14.
 */
public class WeightNormalizedComplementNaiveBayes implements DocumentClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(WeightNormalizedComplementNaiveBayes.class);
    private NaiveBayesModel naiveBayesModel;

    public WeightNormalizedComplementNaiveBayes(NaiveBayesModel naiveBayesModel) {
        this.naiveBayesModel = naiveBayesModel;
    }

    public WeightNormalizedComplementNaiveBayes() {
    }

    @Override
    public NaiveBayesModel train(Collection<Document> documents) {
        Preconditions.checkArgument(naiveBayesModel == null, "Already trained!");
        LOG.info(String.format("Training Complement Naive Bayes with %d documents...", documents.size()));
        List<DocumentTermFrequencies> transformedTermFrequencies = transformTermFrequencies(documents);
        naiveBayesModel = estimateModel(transformedTermFrequencies);
        return naiveBayesModel;
    }

    @Override
    public LabelingResult label(Document document) {
        if (naiveBayesModel == null) {
            throw new IllegalStateException("Classifier must be trained first");
        }
        Map<Category, Double> result = new HashMap<>();

        for (Map.Entry<Category, NaiveBayesModel.TermWeights> entry : naiveBayesModel.getPerClassTermWeights().entrySet()) {
            Category category = entry.getKey();
            NaiveBayesModel.TermWeights termWeights = entry.getValue();
            result.put(category, labelDocument(termWeights, document));
        }

        return new LabelingResult(result);
    }

    @Override
    public NaiveBayesModel getModel() {
        Preconditions.checkArgument(naiveBayesModel != null, "Classifier must be trained first");
        return naiveBayesModel;
    }

    private NaiveBayesModel estimateModel(List<DocumentTermFrequencies> transformedTermFrequencies) {
        Map<Category, Double> perClassSum = new HashMap<>();
        Map<Category, Map<String, Double>> perClassPerTermSum = new HashMap<>();
        computePerClassSums(transformedTermFrequencies, perClassSum, perClassPerTermSum);
        Map<Category, NaiveBayesModel.TermWeights> weights = computeWeights(perClassSum, perClassPerTermSum);
        return new NaiveBayesModel(weights);
    }

    private void computePerClassSums(List<DocumentTermFrequencies> transformedTermFrequencies, Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        for (DocumentTermFrequencies termFrequencies : transformedTermFrequencies) {
            updatePerClassSum(perClassSum, termFrequencies);
            updatePerClassPerTermSum(perClassPerTermSum, termFrequencies);
        }
    }

    private Map<Category, NaiveBayesModel.TermWeights> computeWeights(Map<Category, Double> perClassSum, Map<Category, Map<String, Double>> perClassPerTermSum) {
        Map<Category, NaiveBayesModel.TermWeights> result = new HashMap<>();
        double denominator = getDenominator(perClassSum);
        int numberOfWords = getNumberOfWords(perClassPerTermSum);
        for (Map.Entry<Category, Map<String, Double>> entry : perClassPerTermSum.entrySet()) {
            Category category = entry.getKey();
            LOG.info(String.format("Computing Term Weights for Category: %s...", category.getId()));
            // because it's complement: subtract the category sum from denominator
            double categoryDenominator = denominator - perClassSum.get(category);
            NaiveBayesModel.TermWeights termWeights = computeTermWeights(category, perClassPerTermSum, categoryDenominator, numberOfWords);
            // We correct for the fact that some classes have greater dependencies by normalizing the weight vectors
            // WARN!!! unfortunately weights normalization decrease model accuracy significantly,
            // probably because of the precision errors. DON'T UNCOMMENT
            // normalizeWeights(termWeights);
            result.put(category, termWeights);
        }
        return result;
    }

    private int getNumberOfWords(Map<Category, Map<String, Double>> perClassPerTermSum) {
        if (perClassPerTermSum == null || perClassPerTermSum.isEmpty()) {
            throw new IllegalStateException("Term weights have not been computed");
        }
        Set<String> words = new HashSet<>();
        for (Map<String, Double> termMap : perClassPerTermSum.values()) {
            words.addAll(termMap.keySet());
        }
        return words.size();
    }

    private void normalizeWeights(NaiveBayesModel.TermWeights termWeights) {
        double denominator = 0.0;
        for (double termWeight : termWeights.getWeights().values()) {
            denominator += Math.abs(termWeight);
        }
        for (Map.Entry<String, Double> entry : termWeights.getWeights().entrySet()) {
            entry.setValue(entry.getValue() / denominator);
        }
    }

    private double getDenominator(Map<Category, Double> perClassSum) {
        double result = 0.0;
        for (double sum : perClassSum.values()) {
            result += sum;
        }
        return result;
    }

    /**
     * Computes term weights for a given category. In a classical form of Naive Bayes the weight for a term
     * is the probability that the term occurs in the class.
     *
     * @param category           {@link com.dawanda.document.Category} for which the parameter vector is created
     * @param perClassPerTermSum term frequencies summed per term per class
     * @param denominator        weight normalizer
     * @param numberOfWords      number of words seen during training
     * @return term weights vector encapsulated in {@link com.dawanda.classifier.naivebayes.NaiveBayesModel.TermWeights}
     */
    private NaiveBayesModel.TermWeights computeTermWeights(Category category, Map<Category, Map<String, Double>> perClassPerTermSum, double denominator, int numberOfWords) {
        Map<String, Double> weights = new HashMap<>();
        // for all terms in a given category
        for (String term : perClassPerTermSum.get(category).keySet()) {
            double numerator = 0.0;
            for (Map.Entry<Category, Map<String, Double>> entry : perClassPerTermSum.entrySet()) {
                // for all categories except 'category'
                if (!category.equals(entry.getKey())) {
                    Double inCategoryWeight = entry.getValue().get(term);
                    if (inCategoryWeight == null) {
                        inCategoryWeight = 0.0;
                    }
                    numerator += inCategoryWeight;
                }
            }
            // add smoothing parameters _alpha_i = 1
            double termWeight = Math.log((numerator + 1) / (denominator + numberOfWords));
            weights.put(term, termWeight);
        }

        return new NaiveBayesModel.TermWeights(weights);
    }

    private void updatePerClassSum(Map<Category, Double> perClassSum, DocumentTermFrequencies termFrequencies) {
        Category category = termFrequencies.getDocument().getCategory();
        Double sum = perClassSum.get(category);
        if (sum == null) {
            sum = 0.0;
        }
        perClassSum.put(category, sum + sumForDocument(termFrequencies));
    }

    private void updatePerClassPerTermSum(Map<Category, Map<String, Double>> perClassPerTermSum, DocumentTermFrequencies termFrequencies) {
        Category category = termFrequencies.getDocument().getCategory();
        Map<String, Double> map = perClassPerTermSum.get(category);
        if (map == null) {
            map = new HashMap<>();
            perClassPerTermSum.put(category, map);
        }
        for (String term : termFrequencies.getDocument()) {
            Double sum = map.get(term);
            if (sum == null) {
                sum = 0.0;
            }
            sum += termFrequencies.getTransformedFrequency(term);
            map.put(term, sum);
        }
    }


    private double sumForDocument(DocumentTermFrequencies termFrequencies) {
        double result = 0.0;
        for (String word : termFrequencies.getDocument()) {
            result += termFrequencies.getTransformedFrequency(word);
        }
        return result;
    }

    private List<DocumentTermFrequencies> transformTermFrequencies(Collection<Document> documents) {
        List<DocumentTermFrequencies> transformedDocuments = new ArrayList<>(documents.size());
        for (Document document : documents) {
            transformedDocuments.add(DocumentTermFrequencies.forDocument(document, documents));
        }
        return transformedDocuments;
    }

    private double labelDocument(NaiveBayesModel.TermWeights termWeights, Document document) {
        double result = 0.0;
        for (String term : document) {
            result += document.getWordCount(term) * termWeights.getWeight(term);
        }
        return result;
    }
}
