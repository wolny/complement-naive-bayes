package com.dawanda.classifier.naivebayes;

import com.dawanda.document.Category;
import com.dawanda.utils.NaiveBayesSerializer;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NaiveBayesModelTest {

    private NaiveBayesModel getSampleModel() {
        Map<Category, NaiveBayesModel.TermWeights> map = new HashMap<>();
        map.put(new Category("1"), new NaiveBayesModel.TermWeights(ImmutableMap.of("a", -101.9, "b", -90.1)));
        map.put(new Category("2"), new NaiveBayesModel.TermWeights(ImmutableMap.of("c", -1.9, "d", -0.1)));
        return new NaiveBayesModel(map);
    }

    @Test
    public void shouldBeJsonSerializable() throws IOException {

        NaiveBayesSerializer.writeTo(getSampleModel(), "testModel.json");

        NaiveBayesModel model = NaiveBayesSerializer.readFrom("testModel.json");

        Assert.assertEquals(getSampleModel().getPerClassTermWeights().keySet(), model.getPerClassTermWeights().keySet());

        new File("testModel.json").delete();
    }
}