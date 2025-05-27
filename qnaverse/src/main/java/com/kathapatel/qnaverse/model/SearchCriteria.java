package com.kathapatel.qnaverse.model;

import java.time.LocalDateTime;


 //Class to store search and filter criteria for question searches
 
public class SearchCriteria {
    private String query;
    private boolean searchTitle = true;
    private boolean searchContent = true;
    private boolean searchUser = true;
    
    // Sort settings
    private String sortField = "postedAt";  // postedAt, votes, answers
    private String sortDirection = "desc";  // asc, desc
    
    // Date filters
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    // Vote filters
    private Integer minVotes;
    private Integer maxVotes;
    
    // User filters
    private String userEmail;
    
    // Answer filters
    private Boolean hasAnswers; // true = has at least 1 answer, false = no answers, null = don't filter
    
    // Constructors
    public SearchCriteria() {
    }
    
    public SearchCriteria(String query) {
        this.query = query;
    }
    
    // Static factory methods for common searches
    public static SearchCriteria forNewestQuestions() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setSortField("postedAt");
        criteria.setSortDirection("desc");
        return criteria;
    }
    
    public static SearchCriteria forOldestQuestions() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setSortField("postedAt");
        criteria.setSortDirection("asc");
        return criteria;
    }
    
    public static SearchCriteria forMostVotedQuestions() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setSortField("votes");
        criteria.setSortDirection("desc");
        return criteria;
    }
    
    public static SearchCriteria forUnansweredQuestions() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setHasAnswers(false);
        return criteria;
    }
    
    public static SearchCriteria forUserQuestions(String userEmail) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setUserEmail(userEmail);
        return criteria;
    }
    
    // Getters and setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public boolean isSearchTitle() {
        return searchTitle;
    }
    
    public void setSearchTitle(boolean searchTitle) {
        this.searchTitle = searchTitle;
    }
    
    public boolean isSearchContent() {
        return searchContent;
    }
    
    public void setSearchContent(boolean searchContent) {
        this.searchContent = searchContent;
    }
    
    public boolean isSearchUser() {
        return searchUser;
    }
    
    public void setSearchUser(boolean searchUser) {
        this.searchUser = searchUser;
    }
    
    public String getSortField() {
        return sortField;
    }
    
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    public LocalDateTime getFromDate() {
        return fromDate;
    }
    
    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }
    
    public LocalDateTime getToDate() {
        return toDate;
    }
    
    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }
    
    public Integer getMinVotes() {
        return minVotes;
    }
    
    public void setMinVotes(Integer minVotes) {
        this.minVotes = minVotes;
    }
    
    public Integer getMaxVotes() {
        return maxVotes;
    }
    
    public void setMaxVotes(Integer maxVotes) {
        this.maxVotes = maxVotes;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public Boolean getHasAnswers() {
        return hasAnswers;
    }
    
    public void setHasAnswers(Boolean hasAnswers) {
        this.hasAnswers = hasAnswers;
    }
}