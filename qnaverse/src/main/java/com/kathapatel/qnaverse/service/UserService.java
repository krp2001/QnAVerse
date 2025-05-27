package com.kathapatel.qnaverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kathapatel.qnaverse.dao.AnswerDAO;
import com.kathapatel.qnaverse.dao.QuestionDAO;
import com.kathapatel.qnaverse.dao.UserDAO;
import com.kathapatel.qnaverse.model.Answer;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.User; 

@Service
public class UserService {

    // reputation point values 
    private static final int ANSWER_UPVOTE_AUTHOR = 10;
    private static final int ANSWER_DOWNVOTE_AUTHOR = -2;
    private static final int ANSWER_DOWNVOTE_VOTER = -1; // Penalty for downvoting
    private static final int QUESTION_UPVOTE_AUTHOR = 5;
    private static final int QUESTION_DOWNVOTE_AUTHOR = -2;
    private static final int QUESTION_DOWNVOTE_VOTER = -1; // Penalty for downvoting

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AnswerDAO answerDAO; 

    @Autowired
    private QuestionDAO questionDAO; 

    /**
     * Updates reputation based on an answer vote.
     * @param answerId The ID of the answer being voted on.
     * @param voterEmail The email of the user casting the vote.
     * @param voteValue +1 for upvote, -1 for downvote.
     */
    public void updateReputationForAnswerVote(Long answerId, String voterEmail, int voteValue) {
        Answer answer = answerDAO.getAnswerById(answerId); // Fetch the answer
        if (answer == null) return; // Answer not found

        String authorEmail = answer.getAnsweredBy();

        // Prevent users from gaining/losing reputation from voting on their own posts
        if (authorEmail != null && authorEmail.equals(voterEmail)) {
            return;
        }

        // Update author's reputation
        if (authorEmail != null) {
            int authorRepChange = 0;
            if (voteValue == 1) {
                authorRepChange = ANSWER_UPVOTE_AUTHOR;
            } else if (voteValue == -1) {
                authorRepChange = ANSWER_DOWNVOTE_AUTHOR;
            }
            if (authorRepChange != 0) {
                userDAO.updateUserReputation(authorEmail, authorRepChange);
            }
        }

        // Apply penalty to voter for downvoting 
        if (voteValue == -1) {
             userDAO.updateUserReputation(voterEmail, ANSWER_DOWNVOTE_VOTER);
        }
    }

    /**
     * Updates reputation when a user's vote on an answer is changed or removed.
     * This effectively reverses the original reputation change.
     * @param answerId The ID of the answer.
     * @param voterEmail The email of the user whose vote is changing.
     * @param previousVoteValue The previous vote (+1 or -1).
     * @param newVoteValue The new vote (+1, -1, or 0 if removed/neutral).
     */
     public void updateReputationForAnswerVoteChange(Long answerId, String voterEmail, int previousVoteValue, int newVoteValue) {
        Answer answer = answerDAO.getAnswerById(answerId);
        if (answer == null) return;

        String authorEmail = answer.getAnsweredBy();

        if (authorEmail != null && authorEmail.equals(voterEmail)) {
            return; // No reputation change for self-votes
        }

        // Calculate reversal points for the author
        int authorRepReversal = 0;
        if (previousVoteValue == 1) authorRepReversal -= ANSWER_UPVOTE_AUTHOR;
        else if (previousVoteValue == -1) authorRepReversal -= ANSWER_DOWNVOTE_AUTHOR;

        // Calculate new points for the author
        int authorRepNew = 0;
        if (newVoteValue == 1) authorRepNew += ANSWER_UPVOTE_AUTHOR;
        else if (newVoteValue == -1) authorRepNew += ANSWER_DOWNVOTE_AUTHOR;

        // Apply net change to author
        if (authorEmail != null && (authorRepReversal + authorRepNew) != 0) {
            userDAO.updateUserReputation(authorEmail, authorRepReversal + authorRepNew);
        }

        // Calculate reversal points for the voter 
        int voterRepReversal = 0;
        if (previousVoteValue == -1) voterRepReversal -= ANSWER_DOWNVOTE_VOTER;

        // Calculate new points for the voter
        int voterRepNew = 0;
        if (newVoteValue == -1) voterRepNew += ANSWER_DOWNVOTE_VOTER;

        // Apply net change to voter
        if ((voterRepReversal + voterRepNew) != 0) {
             userDAO.updateUserReputation(voterEmail, voterRepReversal + voterRepNew);
        }
     }

     
     public User getUserByEmail(String email) {
    	    return userDAO.findByEmail(email);
    	}

    // Similar methods for Question Votes

    public void updateReputationForQuestionVote(Long questionId, String voterEmail, int voteValue) {
        Question question = questionDAO.getQuestionById(questionId); // Fetch the question
        if (question == null) return;

        String authorEmail = question.getPostedBy();
        
        System.out.println("Vote reputation update: author=" + authorEmail + 
                ", voter=" + voterEmail + 
                ", value=" + voteValue +
                ", selfVote=" + authorEmail.equals(voterEmail));

        if (authorEmail != null && authorEmail.equals(voterEmail)) {
            System.out.println("Self-vote detected - skipping reputation update");

            return;
        }

        // Update author's reputation
        if (authorEmail != null) {
            int authorRepChange = 0;
            if (voteValue == 1) {
                authorRepChange = QUESTION_UPVOTE_AUTHOR;
            } else if (voteValue == -1) {
                authorRepChange = QUESTION_DOWNVOTE_AUTHOR;
            }
             if (authorRepChange != 0) {
                userDAO.updateUserReputation(authorEmail, authorRepChange);
            }
        }

        // Apply penalty to voter for downvoting 
        if (voteValue == -1) {
            userDAO.updateUserReputation(voterEmail, QUESTION_DOWNVOTE_VOTER);
        }
    }

     public void updateReputationForQuestionVoteChange(Long questionId, String voterEmail, int previousVoteValue, int newVoteValue) {
        Question question = questionDAO.getQuestionById(questionId);
        if (question == null) return;

        String authorEmail = question.getPostedBy();

        if (authorEmail != null && authorEmail.equals(voterEmail)) {
            return;
        }

        // Calculate reversal points for the author
        int authorRepReversal = 0;
        if (previousVoteValue == 1) authorRepReversal -= QUESTION_UPVOTE_AUTHOR;
        else if (previousVoteValue == -1) authorRepReversal -= QUESTION_DOWNVOTE_AUTHOR;

        // Calculate new points for the author
        int authorRepNew = 0;
        if (newVoteValue == 1) authorRepNew += QUESTION_UPVOTE_AUTHOR;
        else if (newVoteValue == -1) authorRepNew += QUESTION_DOWNVOTE_AUTHOR;

        // Apply net change to author
        if (authorEmail != null && (authorRepReversal + authorRepNew) != 0) {
            userDAO.updateUserReputation(authorEmail, authorRepReversal + authorRepNew);
        }

        // Calculate reversal points for the voter 
        int voterRepReversal = 0;
        if (previousVoteValue == -1) voterRepReversal -= QUESTION_DOWNVOTE_VOTER;

        // Calculate new points for the voter
        int voterRepNew = 0;
        if (newVoteValue == -1) voterRepNew += QUESTION_DOWNVOTE_VOTER;

        // Apply net change to voter
        if ((voterRepReversal + voterRepNew) != 0) {
             userDAO.updateUserReputation(voterEmail, voterRepReversal + voterRepNew);
        }
     }

     // Add method for checking privileges
     public boolean hasPrivilege(String userEmail, Privilege privilege) {
         User user = userDAO.findByEmail(userEmail);
         boolean hasPriv = user != null && user.getReputation() >= privilege.getRequiredReputation();
         System.out.println("Privilege check - User: " + userEmail + 
                           ", Privilege: " + privilege.name() + 
                           ", Required Rep: " + privilege.getRequiredReputation() + 
                           ", User Rep: " + (user != null ? user.getReputation() : "null") + 
                           ", Result: " + hasPriv);
         return user != null && user.getReputation() >= privilege.getRequiredReputation();
     }

     // Define an enum for privileges and required reputation
     public enum Privilege {
         POST_QUESTION(0),       //  Need 0 rep to post
         POST_ANSWER(0),         //  Need 0 rep to post
         EDIT_ANY_POST(2000),    //  Need 2000 rep to edit others' posts
         DOWNVOTE(15);           //  Need 15 rep to downvote

         private final int requiredReputation;

         Privilege(int requiredReputation) {
             this.requiredReputation = requiredReputation;
         }

         public int getRequiredReputation() {
             return requiredReputation;
         }
     }
}