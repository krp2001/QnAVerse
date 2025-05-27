package com.kathapatel.qnaverse.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kathapatel.qnaverse.model.Vote;

@Repository
public class VoteDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    //Retrieves a specific vote cast by a user on a piece of content (question or answer)
    public Vote getVoteByUserAndContent(String userEmail, int voteType, Long contentId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Vote where userEmail = :email and voteType = :type and contentId = :id", 
                    Vote.class)
                    .setParameter("email", userEmail)
                    .setParameter("type", voteType)
                    .setParameter("id", contentId)
                    .uniqueResult();
        }
    }
    
    //Saves a new Vote entity to the database
    public boolean saveVote(Vote vote) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(vote);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    //Updates an existing Vote entity
    public boolean updateVote(Vote vote) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(vote);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    //Removes a Vote entity from the database, typically used if vote retraction is implemented
    public boolean removeVote(Long voteId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Vote vote = session.get(Vote.class, voteId);
            if (vote != null) {
                session.remove(vote);
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
    
    
    
    
}
