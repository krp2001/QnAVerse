package com.kathapatel.qnaverse.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email cannot be empty") 
    @Email(message = "Please provide a valid email address") 
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")

    private String password;
    
    @Column
    private Integer reputation = 0;
    
    // New field to track when the user last posted a question (for rate limiting)
    @Column
    private LocalDateTime lastQuestionTime;
    
    @Column
    private String resetToken;
    
    @Column
    private LocalDateTime resetTokenExpiry;
    
    // Getters and setters for new fields
    public String getResetToken() {
        return resetToken;
    }
    
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
    
    // Getters and setters
    public Integer getReputation() {
        return reputation != null ? reputation : 0;
    }
    
    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }
    
    public LocalDateTime getLastQuestionTime() {
        return lastQuestionTime;
    }
    
    public void setLastQuestionTime(LocalDateTime lastQuestionTime) {
        this.lastQuestionTime = lastQuestionTime;
    }
    
    // Convenience methods to check privileges based on reputation
    public boolean canCreateQuestion() {
        return true; 
    }
    
    public boolean canCreateAnswer() {
        return true; // Everyone can post answers
    }
    
    public boolean canVote() {
        return reputation >= 15; // Need 15+ reputation to vote
    }
    
    public boolean canEditOthersContent() {
        return reputation >= 100; // Need 100+ reputation to edit others' posts
    }
    
    public boolean canAccessModTools() {
        return reputation >= 500; // Need 500+ reputation to access moderation tools
    }
    
    // Method to check if the user has reached their question limit
    public boolean hasReachedQuestionLimit() {
        if (lastQuestionTime == null) {
            return false;
        }
        
        // New users (rep < 50) can only post 1 question per 12 hours
        if (reputation < 50) {
            return lastQuestionTime.plusHours(12).isAfter(LocalDateTime.now());
        }
        
        // Users with 50-200 rep can post 1 question per 6 hours
        if (reputation < 200) {
            return lastQuestionTime.plusHours(6).isAfter(LocalDateTime.now());
        }
        
        // Users with 200+ rep can post 1 question per hour
        return lastQuestionTime.plusHours(1).isAfter(LocalDateTime.now());
    }


    // Constructors
    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}