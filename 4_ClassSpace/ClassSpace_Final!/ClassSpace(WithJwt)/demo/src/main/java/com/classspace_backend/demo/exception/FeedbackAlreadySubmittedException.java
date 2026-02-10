package com.classspace_backend.demo.exception;

public class FeedbackAlreadySubmittedException extends RuntimeException {
    public FeedbackAlreadySubmittedException(String message) {
        super(message);
    }
}
