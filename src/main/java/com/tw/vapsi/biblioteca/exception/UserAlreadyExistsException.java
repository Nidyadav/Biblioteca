package com.tw.vapsi.biblioteca.exception;

public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
