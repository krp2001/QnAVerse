package com.kathapatel.qnaverse.dao;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kathapatel.qnaverse.model.Answer;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.SearchCriteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Repository
public class QuestionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Save a Question using manual transaction management
    public boolean saveQuestion(Question question) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(question);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }
    
    //update an existing question 
    public boolean updateQuestion(Question updated, String ownerEmail) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Question db = session.get(Question.class, updated.getId());
            if (db == null || !db.getPostedBy().equals(ownerEmail)) {
                return false;                     // not the owner → abort
            }

            db.setTitle(updated.getTitle());
            db.setContent(updated.getContent());
            session.merge(db);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Deletes a Question by its ID
    public boolean deleteById(Long id, String ownerEmail) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Question ref = session.get(Question.class, id);
            if (ref == null || !ref.getPostedBy().equals(ownerEmail)) return false;
            session.remove(ref);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Retrieve all Questions from the database
    
    public List<Question> getAllQuestions() {
        try (Session session = sessionFactory.openSession()) {

        	// HQL to fetch questions and their answers
            String hql =
                "select distinct q " +
                "from Question q " +
                "left join fetch q.answers " +
                "order by q.postedAt desc";

            return session.createQuery(hql, Question.class).list();
        }
    }
    
    //Retrieves a single Question by ID, including its author and answers
    public Question getQuestionById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT q FROM Question q JOIN FETCH q.author WHERE q.id = :id";
            Question question = session.createQuery(hql, Question.class)
                    .setParameter("id", id)
                    .uniqueResult();
            
            if (question != null) {
                // Then separately fetch the answers with their authors
                String answerHql = "SELECT a FROM Answer a JOIN FETCH a.author WHERE a.question.id = :questionId";
                List<Answer> answers = session.createQuery(answerHql, Answer.class)
                        .setParameter("questionId", id)
                        .list();
                
                // Set the fetched answers on the question
                question.setAnswers(answers);
            }
            
            return question;
        }
    }
   
    //Retrieves a paginated list of questions, ordered by posting date descending
    public List<Question> getPaginatedQuestions(int offset, int limit) {
        try (Session session = sessionFactory.openSession()){
            List<Long> ids = session.createQuery(
                    "SELECT q.id FROM Question q ORDER BY q.postedAt DESC", Long.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
                
                if (ids.isEmpty()) {
                    return new ArrayList<>();
                }
                
                // Then fetch the full entities with authors
                List<Question> questions = session.createQuery(
                    "SELECT DISTINCT q FROM Question q JOIN FETCH q.author WHERE q.id IN (:ids) ORDER BY q.postedAt DESC", 
                    Question.class)
                    .setParameterList("ids", ids)
                    .list();
                
                // For each question, query and set the answer count
                for (Question q : questions) {
                    Long count = session.createQuery(
                        "select count(*) from Answer where question.id = :questionId", 
                        Long.class)
                        .setParameter("questionId", q.getId())
                        .uniqueResult();
                    q.setAnswersCount(count != null ? count.intValue() : 0);
                }
                
                return questions;
            } catch (Exception e) {
                System.out.println("Error retrieving paginated questions: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    
    //Updates the vote count for a specific question
    public boolean updateVote(Long questionId, int delta) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            Question question = session.get(Question.class, questionId);
            if (question != null) {
                Integer currentVotes = question.getVotes();
                if (currentVotes == null) {
                    currentVotes = 0;
                }
                question.setVotes(currentVotes + delta);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    //Increments the view count for a specific question
    public boolean incrementViewCount(Long questionId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            Question question = session.get(Question.class, questionId);
            if (question != null) {
                Integer views = question.getViews();
                if (views == null) {
                    views = 0;
                }
                question.setViews(views + 1);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    //Finds questions related to a given question based on simple title word matching
    public List<Question> getRelatedQuestions(Long currentQuestionId, int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            // get questions with similar words in the title
            Question currentQuestion = session.get(Question.class, currentQuestionId);
            if (currentQuestion == null) {
                return new ArrayList<>();
            }
            
            String[] titleWords = currentQuestion.getTitle().toLowerCase().split("\\s+");
            
            if (titleWords.length == 0) {
                return new ArrayList<>();
            }
            
            // Build a query to find questions with similar words in the title
            StringBuilder hqlBuilder = new StringBuilder("from Question q where q.id != :currentId and (");
            
            for (int i = 0; i < titleWords.length; i++) {
                if (i > 0) {
                    hqlBuilder.append(" or ");
                }
                hqlBuilder.append("lower(q.title) like :word").append(i);
            }
            
            hqlBuilder.append(") order by q.votes desc, q.postedAt desc");
            
            Query<Question> query = session.createQuery(hqlBuilder.toString(), Question.class)
                    .setParameter("currentId", currentQuestionId)
                    .setFirstResult(offset)  // Add offset parameter
                    .setMaxResults(limit);
            
            for (int i = 0; i < titleWords.length; i++) {
                if (titleWords[i].length() >= 4) { // Only use words with at least 4 characters
                    query.setParameter("word" + i, "%" + titleWords[i] + "%");
                } else {
                    query.setParameter("word" + i, "%%"); // Match anything for short words
                }
            }
            
            List<Question> relatedQuestions = query.list();
            
            // Set answer counts for each question
            for (Question q : relatedQuestions) {
                Long count = session.createQuery(
                    "select count(*) from Answer where question.id = :questionId", 
                    Long.class)
                    .setParameter("questionId", q.getId())
                    .uniqueResult();
                q.setAnswersCount(count != null ? count.intValue() : 0);
            }
            
            return relatedQuestions;
        }
    }
    
    //Counts the total number of questions in the database
    public long countQuestions() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.createQuery("select count(*) from Question", Long.class)
                    .uniqueResult();
        } catch (Exception e) {
            System.out.println("Error counting questions: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Performs a simple text search across title, content, and postedBy fields
    public List<Question> searchQuestions(String query, int offset, int limit) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            
            // Create a query that searches in title, content, and postedBy
            String hql = "from Question where lower(title) like lower(:query) or " +
                         "lower(content) like lower(:query) or " +
                         "lower(postedBy) like lower(:query) " +
                         "order by postedAt desc";
            
            return session.createQuery(hql, Question.class)
                    .setParameter("query", "%" + query + "%")
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        } catch (Exception e) {
            System.out.println("Error searching questions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    //Counts the total number of questions matching a simple text search query
    public long countSearchResults(String query) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            
            String hql = "select count(*) from Question where lower(title) like lower(:query) or " +
                         "lower(content) like lower(:query) or " +
                         "lower(postedBy) like lower(:query)";
            
            return session.createQuery(hql, Long.class)
                    .setParameter("query", "%" + query + "%")
                    .uniqueResult();
        } catch (Exception e) {
            System.out.println("Error counting search results: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // Performs a search with specific field filtering and sorting options
    public List<Question> searchQuestionsWithFilters(
            String query, boolean searchTitle, boolean searchContent, 
            boolean searchUser, String sortBy, int offset, int limit) {
        

        System.out.println("Search query in DAO: '" + query + "'");
        System.out.println("Search filters - Title: " + searchTitle + ", Content: " + searchContent + ", User: " + searchUser);
        System.out.println("Sort by: " + sortBy);
    	
        Session session = null;
        try {
            session = sessionFactory.openSession();
            
            // Build the base query
            StringBuilder hqlBuilder = new StringBuilder("from Question q where ");
            
            // search conditions
            List<String> conditions = new ArrayList<>();
            if (conditions.isEmpty()) {
                System.out.println("No search conditions selected, searching in all fields");
                conditions.add("lower(q.title) like lower(:query)");
                conditions.add("lower(q.content) like lower(:query)");
                conditions.add("lower(q.postedBy) like lower(:query)");
            }
            
            // If no search fields are selected return empty list
            if (conditions.isEmpty()) {
                System.out.println("No search conditions selected!");
                return new ArrayList<>();
            }
            
            hqlBuilder.append("(").append(String.join(" or ", conditions)).append(")");
            
            // sorting based on the selected option
            switch (sortBy) {
                case "oldest":
                    hqlBuilder.append(" order by q.postedAt asc");
                    break;
                case "most-votes":
                    hqlBuilder.append(" order by q.votes desc, q.postedAt desc");
                    break;
                case "least-votes":
                    hqlBuilder.append(" order by q.votes asc, q.postedAt desc");
                    break;
                case "most-answers":
                    hqlBuilder.append(" order by size(q.answers) desc, q.postedAt desc");
                    break;
                case "least-answers":
                    hqlBuilder.append(" order by size(q.answers) asc, q.postedAt desc");
                    break;
                default: // newest
                    hqlBuilder.append(" order by q.postedAt desc");
                    break;
            }
            
            String finalHql = hqlBuilder.toString();
            System.out.println("Final HQL query: " + finalHql);
            

            List<Question> results = session.createQuery(finalHql, Question.class)
                    .setParameter("query", "%" + query + "%")
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            System.out.println("Found " + results.size() + " results");
            return results;
        } catch (Exception e) {
            System.out.println("Error searching questions with filters: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    

	//Performs a search using the Criteria API based on the provided SearchCriteria object
	public List<Question> searchWithCriteria(
	        SearchCriteria criteria,
	        int offset, 
	        int limit) {
	    
	    try (Session session = sessionFactory.openSession()) {
	        CriteriaBuilder builder = session.getCriteriaBuilder();
	        CriteriaQuery<Question> criteriaQuery = builder.createQuery(Question.class);
	        Root<Question> root = criteriaQuery.from(Question.class);
	        root.fetch("author", JoinType.LEFT);
	        // Build conditions based on search criteria
	        List<Predicate> predicates = new ArrayList<>();
	        
	        // Text search (query)
	        if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
	            List<Predicate> searchPredicates = new ArrayList<>();
	            
	            if (criteria.isSearchTitle()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("title")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (criteria.isSearchContent()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("content")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (criteria.isSearchUser()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("postedBy")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (!searchPredicates.isEmpty()) {
	                predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
	            }
	        }
	        
	        // Date range filtering
	        if (criteria.getFromDate() != null) {
	            predicates.add(builder.greaterThanOrEqualTo(
	                root.get("postedAt"), criteria.getFromDate()));
	        }
	        
	        if (criteria.getToDate() != null) {
	            predicates.add(builder.lessThanOrEqualTo(
	                root.get("postedAt"), criteria.getToDate()));
	        }
	        
	        // Vote range filtering
	        if (criteria.getMinVotes() != null) {
	            predicates.add(builder.greaterThanOrEqualTo(
	                root.get("votes"), criteria.getMinVotes()));
	        }
	        
	        if (criteria.getMaxVotes() != null) {
	            predicates.add(builder.lessThanOrEqualTo(
	                root.get("votes"), criteria.getMaxVotes()));
	        }
	        
	        // User filtering
	        if (criteria.getUserEmail() != null && !criteria.getUserEmail().isEmpty()) {
	            predicates.add(builder.equal(
	                root.get("postedBy"), criteria.getUserEmail()));
	        }
	        
	        // Has answers filtering (requires a subquery)
	        if (criteria.getHasAnswers() != null) {
	            Subquery<Long> answerSubquery = criteriaQuery.subquery(Long.class);
	            Root<Answer> answerRoot = answerSubquery.from(Answer.class);
	            answerSubquery.select(builder.count(answerRoot.get("id")));
	            answerSubquery.where(builder.equal(answerRoot.get("question"), root));
	            
	            if (criteria.getHasAnswers()) {
	                predicates.add(builder.greaterThan(answerSubquery, 0L));
	            } else {
	                predicates.add(builder.equal(answerSubquery, 0L));
	            }
	        }
	        
	        // all conditions to the query
	        if (!predicates.isEmpty()) {
	            criteriaQuery.where(predicates.toArray(new Predicate[0]));
	        }
	        
	        // sorting
	        List<Order> orderList = new ArrayList<>();
	        
	        switch (criteria.getSortField()) {
	            case "votes":
	                if ("asc".equals(criteria.getSortDirection())) {
	                    orderList.add(builder.asc(root.get("votes")));
	                } else {
	                    orderList.add(builder.desc(root.get("votes")));
	                }
	                // secondary sort by date for consistent ordering
	                orderList.add(builder.desc(root.get("postedAt")));
	                break;
	            case "answers":
	                // For answers sort, we need a join or subquery
	                Subquery<Long> answerCountSubquery = criteriaQuery.subquery(Long.class);
	                Root<Answer> answerRoot = answerCountSubquery.from(Answer.class);
	                answerCountSubquery.select(builder.count(answerRoot.get("id")));
	                answerCountSubquery.where(builder.equal(answerRoot.get("question"), root));
	                
	                if ("asc".equals(criteria.getSortDirection())) {
	                    orderList.add(builder.asc(answerCountSubquery));
	                } else {
	                    orderList.add(builder.desc(answerCountSubquery));
	                }
	                // secondary sort
	                orderList.add(builder.desc(root.get("postedAt")));
	                break;
	            case "postedAt":
	            default:
	                if ("asc".equals(criteria.getSortDirection())) {
	                    orderList.add(builder.asc(root.get("postedAt")));
	                } else {
	                    orderList.add(builder.desc(root.get("postedAt")));
	                }
	                break;
	        }
	        
	        criteriaQuery.orderBy(orderList);
	        
	        // Execute query with pagination
	        List<Question> questions = session.createQuery(criteriaQuery)
	                .setFirstResult(offset)
	                .setMaxResults(limit)
	                .getResultList();
	        
	        // set the answer counts for each question
	        for (Question q : questions) {
	            Long count = session.createQuery(
	                "select count(*) from Answer where question.id = :questionId", 
	                Long.class)
	                .setParameter("questionId", q.getId())
	                .uniqueResult();
	            q.setAnswersCount(count != null ? count.intValue() : 0);
	        }

	        return questions;
	    }
	}

	
	//Count total results for a given search criteria (for pagination)
	 
	public long countWithCriteria(SearchCriteria criteria) {
	    try (Session session = sessionFactory.openSession()) {
	        CriteriaBuilder builder = session.getCriteriaBuilder();
	        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
	        Root<Question> root = countQuery.from(Question.class);
	        
	        // Select count
	        countQuery.select(builder.count(root));
	        
	        // Build conditions based on search criteria (same as in search method)
	        List<Predicate> predicates = new ArrayList<>();
	        
	        // Text search (query)
	        if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
	            List<Predicate> searchPredicates = new ArrayList<>();
	            
	            if (criteria.isSearchTitle()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("title")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (criteria.isSearchContent()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("content")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (criteria.isSearchUser()) {
	                searchPredicates.add(
	                    builder.like(builder.lower(root.get("postedBy")), "%" + criteria.getQuery().toLowerCase() + "%"));
	            }
	            
	            if (!searchPredicates.isEmpty()) {
	                predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
	            }
	        }
	        
	        // Date range filtering
	        if (criteria.getFromDate() != null) {
	            predicates.add(builder.greaterThanOrEqualTo(
	                root.get("postedAt"), criteria.getFromDate()));
	        }
	        
	        if (criteria.getToDate() != null) {
	            predicates.add(builder.lessThanOrEqualTo(
	                root.get("postedAt"), criteria.getToDate()));
	        }
	        
	        // Vote range filtering
	        if (criteria.getMinVotes() != null) {
	            predicates.add(builder.greaterThanOrEqualTo(
	                root.get("votes"), criteria.getMinVotes()));
	        }
	        
	        if (criteria.getMaxVotes() != null) {
	            predicates.add(builder.lessThanOrEqualTo(
	                root.get("votes"), criteria.getMaxVotes()));
	        }
	        
	        // User filtering
	        if (criteria.getUserEmail() != null && !criteria.getUserEmail().isEmpty()) {
	            predicates.add(builder.equal(
	                root.get("postedBy"), criteria.getUserEmail()));
	        }
	        
	        // Apply all conditions to the query
	        if (!predicates.isEmpty()) {
	            countQuery.where(predicates.toArray(new Predicate[0]));
	        }
	        
	        // Execute count query
	        return session.createQuery(countQuery).getSingleResult();
	    }
	}

	//Retrieves all questions posted by a specific user
	public List<Question> getQuestionsByUser(String email) {
	    try (Session session = sessionFactory.openSession()) {
	        return session.createQuery("FROM Question WHERE postedBy = :email ORDER BY postedAt DESC", Question.class)
	                .setParameter("email", email)
	                .list();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
    
    
}
