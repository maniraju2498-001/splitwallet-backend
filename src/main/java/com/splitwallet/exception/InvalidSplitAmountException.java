package com.splitwallet.exception;

public class InvalidSplitAmountException extends RuntimeException {
    public InvalidSplitAmountException(String message) {
        super(message);
    }
}
