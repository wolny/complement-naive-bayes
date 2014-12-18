package com.dawanda.document;

/**
 * Basic interface for document class used for text classifier training and labeling.
 * Document is seen as a bag of words, where word count can be retrieved for each word
 * in the document. Extends {@link Iterable} which returns iterator of words
 * present in the document for convenience (e.g. useful when labeling document).
 * <p/>
 * Created by awolny on 07/12/14.
 */
public interface Document extends Iterable<String> {
    /**
     * @return unique document id
     */
    String getId();

    /**
     * @return document's {@link com.dawanda.document.Category} or null if not a training example
     */
    Category getCategory();

    /**
     * @param word term for which we want to get the frequency in the document
     * @return count of the term {@code word} in the document
     */
    int getWordCount(String word);

    /**
     * @param word term for which we ask if it is present in the document
     * @return {@code true} if document contains a given word, {@code false} otherwise
     */
    boolean contains(String word);
}
