package com.tw.vapsi.biblioteca.exception;

public class BookAlreadyReturnedException extends RuntimeException{
    public BookAlreadyReturnedException(String message) {
        super(message);
    }
}
