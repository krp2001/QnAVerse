package com.kathapatel.qnaverse.dao;

import com.kathapatel.qnaverse.model.User;

import java.time.LocalDateTime;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    
    //Finds a User entity by their email address
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            var query = session.createQuery("from User u where u.email = :email",User.class);
            query.setParameter("email", email);
            return (User) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Saves a new User entity to the database
    public boolean saveUser(User user) {
        Session session = null;
        Transaction tx = null;
        
        try {
            System.out.println("Attempting to save user: " + user.getEmail());
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            System.out.println("User saved successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.out.println("Error rolling back: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    
    }
    
    //Sets the password reset token and its expiry time for a user identified by email
    public boolean setResetToken(String email, String token, LocalDateTime expiry) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            User user = findByEmail(email);
            if (user == null) {
                return false;
            }
            
            user.setResetToken(token);
            user.setResetTokenExpiry(expiry);
            
            session.merge(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    //Finds a User entity by their password reset token
    public User findByResetToken(String token) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where resetToken = :token", User.class)
                    .setParameter("token", token)
                    .uniqueResult();
        }
    }

    //Updates a user's password and clears the reset token fields
    public boolean updatePassword(Long userId, String newPassword) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            
            User user = session.get(User.class, userId);
            if (user == null) {
                return false;
            }
            
            user.setPassword(newPassword);
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            
            session.merge(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    

    //Updates a user's reputation score by adding a specified amount
     public boolean updateUserReputation(String email, int reputationChange) {
         Transaction tx = null;
         try (Session session = sessionFactory.openSession()) {
             tx = session.beginTransaction();

             String hql = "UPDATE User SET reputation = reputation + :change WHERE email = :email";
             int updated = session.createMutationQuery(hql)
                     .setParameter("change", reputationChange)
                     .setParameter("email", email)
                     .executeUpdate();
             
             tx.commit();
             System.out.println("Updated reputation for " + email + " by " + reputationChange + 
                     ", rows affected: " + updated);
             return updated > 0;
         } catch (Exception e) {
             if (tx != null && tx.isActive()) tx.rollback();
             e.printStackTrace();
             return false;
         }
     }

     //Retrieves the current reputation score for a user by email
     public int getUserReputation(String email) {
         try (Session session = sessionFactory.openSession()) {
             User user = session.createQuery("FROM User WHERE email = :email", User.class)
                 .setParameter("email", email)
                 .uniqueResult();
             return user != null ? user.getReputation() : 0;
         }
     }

     //Sets a user's reputation score to an absolute value
     public boolean setUserReputation(String email, int reputation) {
         Transaction tx = null;
         try (Session session = sessionFactory.openSession()) {
             tx = session.beginTransaction();
             User user = session.createQuery("FROM User WHERE email = :email", User.class)
                 .setParameter("email", email)
                 .uniqueResult();
             if (user != null) {
                 user.setReputation(reputation);
                 session.merge(user);
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
     
     //Retrieves a User entity by its primary key
     public User getUserById(Long id) {
    	    try (Session session = sessionFactory.openSession()) {
    	        return session.get(User.class, id);
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        return null;
    	    }
    	}
    
    
}