package com.dawanda.featureextractor;

import com.dawanda.db.Product;
import com.dawanda.document.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class FeatureExtractorTest {
    private Product getSampleProduct() {
        return new Product(1, 666, 321, "Vintage! Trimmer! Trimmer!", "Your own personal barber. You won't ever buy a new one");
    }

    @Test
    public void titleFeatureExtractorTest() {
        FeatureExtractor extractor = new TitleFeatureExtractor();
        Document document = extractor.extractFeatureVectors(Arrays.asList(getSampleProduct())).get(0);
        Assert.assertTrue(document.contains("vintag"));
        Assert.assertTrue(document.contains("trimmer"));
        Assert.assertEquals(2, document.getWordCount("trimmer"));
    }

    @Test
    public void titleAndDescriptionFeatureExtractorTest() {
        FeatureExtractor extractor = new TitleAndDescriptionFeatureExtractor();
        Document document = extractor.extractFeatureVectors(Arrays.asList(getSampleProduct())).get(0);
        Assert.assertEquals(2, document.getWordCount("trimmer"));
        Assert.assertTrue(document.contains("you"));
    }
}