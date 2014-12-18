package com.dawanda.featureextractor.tokenizer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Test;

public class EnglishLuceneTokenizerTest {
    private String getSampleDocument() {
        String str = "Hi!\n" +
                "\n" +
                "Fantasticum is the place where you can find unique gifts for any occasion! \n" +
                "For your friends, for your co-workers, for your relatives, for everyone, in a word :)\n" +
                "John's cat's and dog's haven't\n" +
                "Cheers! \n" +
                "Natalia";
        return str;
    }

    @Test
    public void shouldTokenizeTextDocument() {
        Tokenizer tokenizer = new EnglishLuceneTokenizer();
        Multiset<String> multiset = HashMultiset.create();
        multiset.addAll(tokenizer.tokenize(getSampleDocument()));
        Assert.assertTrue(multiset.contains("cat"));
        Assert.assertTrue(multiset.contains("john"));
        Assert.assertTrue(multiset.contains("natalia"));
    }
}