package com.cnnfe.liteshare.connect;

public class CryptoException extends Exception {

    public CryptoException() {
    }
    //exception handling
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
