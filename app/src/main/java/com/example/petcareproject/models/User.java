package com.example.petcareproject.models;

import com.example.petcareproject.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String role;
    private String bio;
    private long createdAt; // TODO Firebase: replace with Timestamp

    // Sitter-only fields
    private List<String> acceptedAnimals;
    private int completedCount;
    private double averageRating;
    private String badge;

    public User() {
        this.acceptedAnimals = new ArrayList<>();
    }

    // Constructor for owner
    public User(String userId, String fullName, String email, String phone, String city, String role, String bio) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.role = role;
        this.bio = bio;
        this.createdAt = System.currentTimeMillis();
        this.acceptedAnimals = new ArrayList<>();
        this.completedCount = 0;
        this.averageRating = 0.0;
        this.badge = Constants.BADGE_NEW;
    }

    // Constructor for sitter
    public User(String userId, String fullName, String email, String phone, String city, String role, String bio,
                List<String> acceptedAnimals, int completedCount, double averageRating, String badge) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.role = role;
        this.bio = bio;
        this.createdAt = System.currentTimeMillis();
        this.acceptedAnimals = acceptedAnimals;
        this.completedCount = completedCount;
        this.averageRating = averageRating;
        this.badge = badge;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<String> getAcceptedAnimals() { return acceptedAnimals; }
    public void setAcceptedAnimals(List<String> acceptedAnimals) { this.acceptedAnimals = acceptedAnimals; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
}
