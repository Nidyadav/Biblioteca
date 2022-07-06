package com.tw.vapsi.biblioteca.exception;

public class BookAlreadyExistsException extends Exception {
    public BookAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
