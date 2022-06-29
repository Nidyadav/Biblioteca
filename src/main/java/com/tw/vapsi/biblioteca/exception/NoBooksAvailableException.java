package com.tw.vapsi.biblioteca.exception;

public class NoBooksAvailableException extends Exception {
    public NoBooksAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
