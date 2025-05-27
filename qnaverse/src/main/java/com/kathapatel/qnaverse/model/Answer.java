package com.kathapatel.qnaverse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="answers")
public class Answer {
	
	@Id // Mark 'id' as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
	private Long id;
	
	 @Column(length = 5000) // Define column details (max length for content)
    private String content;
    private String answeredBy;
    private java.time.LocalDateTime answeredAt;
    
    public String getFormattedDate() {
        if (answeredAt == null) return "unknown date";
        return answeredAt.toString().replace('T', ' ');
    }
    
    @ManyToOne(fetch = FetchType.LAZY)   // Many answers belong to one question        
    @JoinColumn(name = "question_id")    // Specifies the FK column ("question_id") in "answers" table       
    private Question question;
    
    @Column
    private Integer votes = 0;
    
    private String postedBy;

    
    @ManyToOne(fetch = FetchType.EAGER) // Many answers can be written by one user
    @JoinColumn(name = "user_id", nullable = false) // Name of the foreign key column in 'questions' table referencing users table
    private User author;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    
    public String getPostedBy() { return postedBy; }

     public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
    
    public Integer getVotes() { return votes != null ? votes : 0;  }
    public void setVotes(Integer votes) { this.votes = votes; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question;}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAnsweredBy() { return answeredBy; }
    public void setAnsweredBy(String answeredBy) { this.answeredBy = answeredBy; }

    public java.time.LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(java.time.LocalDateTime answeredAt) { this.answeredAt = answeredAt; }

}
