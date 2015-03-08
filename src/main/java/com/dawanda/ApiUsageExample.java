package com.dawanda;

import com.dawanda.classifier.DocumentClassifier;
import com.dawanda.classifier.naivebayes.labels.LabelResults;
import com.dawanda.classifier.naivebayes.model.NaiveBayesModel;
import com.dawanda.classifier.naivebayes.WeightNormalizedComplementNaiveBayes;
import com.dawanda.document.Document;
import com.dawanda.utils.Extractors;
import com.dawanda.utils.NaiveBayesSerializer;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Created by awolny on 19/12/14.
 */
public class ApiUsageExample {
    public static void main(String[] args) throws IOException {
        // read Naive Bayes model from JSON file
        String pathToModel = "./model.json";
        NaiveBayesModel model = NaiveBayesSerializer.readFrom(pathToModel);

        // create Complement Naive Bayes classifier
        DocumentClassifier classifier = new WeightNormalizedComplementNaiveBayes(model);

        // get the title and description of the product which is to be labeled
        String title = "...";
        String description = "...";
        String text = title + " " + description;

        // extract features, MAKE SURE THE SAME EXTRACTOR WAS USED DURING TRAINING PHASE
        Document document = Extractors.STANDARD_EXTRACTOR.extractFeatureVector(text);

        // label document
        LabelResults labelResults = classifier.label(document);

        // get categories ordered by score
        List<LabelResults.ScoredCategory> categories = labelResults.getOrderedCategories();

        // print 3 best category suggestions according to the model
        System.out.println(Lists.newArrayList(Iterables.limit(categories, 3)));
    }
}
