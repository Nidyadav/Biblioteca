package com.tw.vapsi.biblioteca.exception;

public class NoBooksAvailableException extends RuntimeException {
    public NoBooksAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
