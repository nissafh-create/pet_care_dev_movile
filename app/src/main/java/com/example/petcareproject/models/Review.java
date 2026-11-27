package com.example.petcareproject.models;

public class Review {

    private String reviewId;
    private String requestId;
    private String ownerId;
    private String sitterId;
    private int rating;
    private String comment;
    private long createdAt; // TODO Firebase: replace with Timestamp

    public Review() {
    }

    public Review(String reviewId, String requestId, String ownerId, String sitterId, int rating, String comment) {
        this.reviewId = reviewId;
        this.requestId = requestId;
        this.ownerId = ownerId;
        this.sitterId = sitterId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = System.currentTimeMillis();
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getSitterId() { return sitterId; }
    public void setSitterId(String sitterId) { this.sitterId = sitterId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
