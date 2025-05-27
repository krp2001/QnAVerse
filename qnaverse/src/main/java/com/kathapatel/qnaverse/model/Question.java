package com.kathapatel.qnaverse.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    private String postedBy;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "user_id", nullable = false) // Name of the foreign key column in 'questions' table referencing users table
    private User author;

    private LocalDateTime postedAt = LocalDateTime.now();
    
    private Integer votes = 0;
    
    @Column
    private Integer views = 0; 
    
    public Integer getViews() { 
        return views != null ? views : 0; 
    }

    public void setViews(Integer views) { 
        this.views = views; 
    }

    public Integer getVotes() {
		return votes!= null ? votes : 0;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();
	
	public List<Answer> getAnswers() {
	    return answers;
	}

	public void setAnswers(List<Answer> answers) {
	    this.answers = answers;
	}
;
	
	@Transient // Transient field to hold the count of answers
	private int answersCount;

	public int getAnswersCount() {
	    return answersCount;
	}

	public void setAnswersCount(int answersCount) {
	    this.answersCount = answersCount;
	}

	public String getFormattedDate() {
	    if (postedAt == null) return "unknown date";
	    return postedAt.toString().replace('T', ' ');
	}
	
    public User getAuthor() { return author; }

    public void setAuthor(User author) {
        this.author = author;
        if (author != null) {
             this.postedBy = author.getEmail();
        } else {
             this.postedBy = null;
        }
    }

	public Question() {}

    // Getters & setters
	
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    @Transient                    
    public java.util.Date getPostedAt() {
        return java.util.Date.from(
            postedAt.atZone(java.time.ZoneId.systemDefault()).toInstant()
        );
    }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
}