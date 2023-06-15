package com.example.myapplication;

public class Feedback {

    private String feedbackId;
    private String feedbackText;
    private String giverName;
    private String timestamp;

    public Feedback() {
        // Default constructor required for Firebase
    }

    public Feedback(String feedbackId, String feedbackText, String giverName, String timestamp) {
        this.feedbackId = feedbackId;
        this.feedbackText = feedbackText;
        this.giverName = giverName;
        this.timestamp = timestamp;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
