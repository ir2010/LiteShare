package com.cnnfe.liteshare.encryption;

public class CryptoException extends Exception {

    public CryptoException() {
    }
    //exception handling
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
