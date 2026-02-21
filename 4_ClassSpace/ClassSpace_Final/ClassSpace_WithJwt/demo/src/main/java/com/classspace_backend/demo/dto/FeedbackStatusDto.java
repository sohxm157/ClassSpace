package com.classspace_backend.demo.dto;

public class FeedbackStatusDto {

    private boolean submitted;
    private boolean allowed;

    public FeedbackStatusDto(boolean submitted, boolean allowed) {
        this.submitted = submitted;
        this.allowed = allowed;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isAllowed() {
        return allowed;
    }
}
