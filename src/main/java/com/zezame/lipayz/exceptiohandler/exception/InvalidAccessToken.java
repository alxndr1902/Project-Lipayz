package com.zezame.lipayz.exceptiohandler.exception;

public class InvalidAccessToken extends RuntimeException {
    public InvalidAccessToken(String message) {
        super(message);
    }
}
