package com.example.petcareproject.models;

public class Application {

    private String applicationId;
    private String requestId;
    private String sitterId;
    private String sitterName;
    private String message;
    private String status;
    private long createdAt;

    public Application() {
    }

    public Application(String applicationId, String requestId, String sitterId, String sitterName,
                       String message, String status) {
        this.applicationId = applicationId;
        this.requestId = requestId;
        this.sitterId = sitterId;
        this.sitterName = sitterName;
        this.message = message;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getSitterId() { return sitterId; }
    public void setSitterId(String sitterId) { this.sitterId = sitterId; }

    public String getSitterName() { return sitterName; }
    public void setSitterName(String sitterName) { this.sitterName = sitterName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
