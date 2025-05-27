package com.kathapatel.qnaverse.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userEmail;
    
    @Column(nullable = false)
    private Integer voteType; // 1 for question, 2 for answer
    
    @Column(nullable = false)
    private Long contentId; // Question or Answer ID
    
    @Column(nullable = false)
    private Integer value; // 1 for upvote, -1 for downvote
    
    @Column(nullable = false)
    private LocalDateTime votedAt;
    
    // Constructors, getters, and setters
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Integer getVoteType() {
		return voteType;
	}

	public void setVoteType(Integer voteType) {
		this.voteType = voteType;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public LocalDateTime getVotedAt() {
		return votedAt;
	}

	public void setVotedAt(LocalDateTime votedAt) {
		this.votedAt = votedAt;
	}

	public Vote() {}
    
    public Vote(String userEmail, Integer voteType, Long contentId, Integer value) {
        this.userEmail = userEmail;
        this.voteType = voteType;
        this.contentId = contentId;
        this.value = value;
        this.votedAt = LocalDateTime.now();
    }
   
}
