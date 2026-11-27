package com.example.petcareproject.models;

public class CareRequest {

    private String requestId;
    private String ownerId;
    private String petId;
    private String petName;
    private String petSpecies;
    private String city;
    private String startDate;
    private String endDate;
    private double pricePerDay;
    private String description;
    private String status;
    private String chosenSitterId;
    private long createdAt;

    public CareRequest() {
    }

    public CareRequest(String requestId, String ownerId, String petId, String petName, String petSpecies,
            String city, String startDate, String endDate, double pricePerDay,
            String description, String status, String chosenSitterId) {
        this.requestId = requestId;
        this.ownerId = ownerId;
        this.petId = petId;
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricePerDay = pricePerDay;
        this.description = description;
        this.status = status;
        this.chosenSitterId = chosenSitterId;
        this.createdAt = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChosenSitterId() {
        return chosenSitterId;
    }

    public void setChosenSitterId(String chosenSitterId) {
        this.chosenSitterId = chosenSitterId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
