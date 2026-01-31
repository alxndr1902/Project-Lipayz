package com.zezame.lipayz.exceptiohandler.exception;

public class OptimisticLockException extends RuntimeException{
    public OptimisticLockException(String message) {
        super(message);
    }
}
