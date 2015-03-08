package com.dawanda.document;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Iterator;
import java.util.List;

public class DocumentImpl implements Document {

    private final Multiset<String> bagOfWords;
    private final String id;
    private final Category category;

    public DocumentImpl(List<String> words, String id, Category cat) {
        bagOfWords = HashMultiset.create(words);
        this.id = id;
        this.category = cat;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public int getWordCount(String word) {
        return bagOfWords.count(word);
    }

    @Override
    public boolean contains(String word) {
        return bagOfWords.contains(word);
    }

    @Override
    public Iterator<String> iterator() {
        return bagOfWords.elementSet().iterator();
    }

    @Override
    public String toString() {
        return bagOfWords.toString();
    }
}
