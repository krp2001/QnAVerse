package com.kathapatel.qnaverse.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import com.kathapatel.qnaverse.model.Answer;

@Repository
public class AnswerDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    //Saves a new Answer entity to the database
    public boolean saveAnswer(Answer answer) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(answer);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    //Retrieves all answers for a specific question ID, ordered by votes and then date
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Answer a JOIN FETCH a.author WHERE a.question.id = :questionId ORDER BY a.votes DESC, a.answeredAt DESC", 
                    Answer.class)
                    .setParameter("questionId", questionId)
                    .list();
        }
    }
    
    //Retrieves a single Answer by its ID
    public Answer getAnswerById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            
            String hql = "from Answer a join fetch a.question join fetch a.author where a.id = :id";
            return session.createQuery(hql, Answer.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }
    
    //Updates the vote count for a specific answer by adding the delta
    public boolean updateVote(Long answerId, int delta) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            Answer answer = session.get(Answer.class, answerId);
            if (answer != null) {
                answer.setVotes(answer.getVotes() + delta);
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
    
    //Updates the content of an existing Answer
    public boolean updateAnswer(Answer answer) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Answer> update = builder.createCriteriaUpdate(Answer.class);
            Root<Answer> root = update.from(Answer.class);
            
            // Set the fields to update
            update.set(root.get("content"), answer.getContent());
                       
            // Where clause to match the answer ID
            update.where(builder.equal(root.get("id"), answer.getId()));
            
            // Execute the update
            int result = session.createMutationQuery(update).executeUpdate();
            
            tx.commit();
            return result > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    //Deletes an Answer from the database based on its ID
    public boolean deleteAnswer(Long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<Answer> delete = builder.createCriteriaDelete(Answer.class);
            Root<Answer> root = delete.from(Answer.class);
            
            // Where clause to match the answer ID
            delete.where(builder.equal(root.get("id"), id));
            
            // Execute the delete
            int result = session.createMutationQuery(delete).executeUpdate();
            
            tx.commit();
            return result > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }


    //Retrieves all answers posted by a specific user
    public List<Answer> getAnswersByUser(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Answer a JOIN FETCH a.question WHERE a.answeredBy = :email OR a.postedBy = :email ORDER BY a.answeredAt DESC", 
                    Answer.class)
                    .setParameter("email", email)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}