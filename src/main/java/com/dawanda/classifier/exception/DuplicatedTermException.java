package com.dawanda.classifier.exception;

/**
 * Thrown when {@link com.dawanda.document.Document} returns the same term multiple times during iteration.
 * <p/>
 * Created by awolny on 07/12/14.
 */
public class DuplicatedTermException extends RuntimeException {
    public DuplicatedTermException(String msg) {
        super(msg);
    }
}
