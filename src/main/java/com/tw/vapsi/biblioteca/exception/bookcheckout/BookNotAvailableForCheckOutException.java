package com.tw.vapsi.biblioteca.exception.bookcheckout;


public class BookNotAvailableForCheckOutException extends BookCheckOutException {
    public BookNotAvailableForCheckOutException(String message) {
        super(message);
    }
}
