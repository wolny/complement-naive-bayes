package com.dawanda.utils;

import com.dawanda.classifier.naivebayes.model.NaiveBayesModel;
import com.dawanda.document.Category;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by awolny on 10/12/14.
 */
public class NaiveBayesSerializer {
    public static void writeTo(NaiveBayesModel model, String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, NaiveBayesModel.TermWeights> modelMap = transformWrite(model.getPerClassTermWeights());
        objectMapper.writeValue(new File(path), modelMap);
    }

    public static NaiveBayesModel readFrom(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, NaiveBayesModel.TermWeights> map = objectMapper.readValue(new File(path), new TypeReference<Map<String, NaiveBayesModel.TermWeights>>() {
        });
        return new NaiveBayesModel(transformRead(map));
    }

    private static Map<Category, NaiveBayesModel.TermWeights> transformRead(Map<String, NaiveBayesModel.TermWeights> map) {
        Map<Category, NaiveBayesModel.TermWeights> result = new HashMap<>();

        for (Map.Entry<String, NaiveBayesModel.TermWeights> entry : map.entrySet()) {
            result.put(new Category(entry.getKey()), entry.getValue());
        }

        return result;
    }

    private static Map<String, NaiveBayesModel.TermWeights> transformWrite(Map<Category, NaiveBayesModel.TermWeights> perClassTermWeights) {
        Map<String, NaiveBayesModel.TermWeights> result = new HashMap<>();
        for (Map.Entry<Category, NaiveBayesModel.TermWeights> entry : perClassTermWeights.entrySet()) {
            result.put(entry.getKey().getId(), entry.getValue());
        }
        return result;
    }
}
