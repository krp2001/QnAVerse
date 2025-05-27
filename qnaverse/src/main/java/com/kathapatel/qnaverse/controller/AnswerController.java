package com.kathapatel.qnaverse.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import com.kathapatel.qnaverse.dao.AnswerDAO;
import com.kathapatel.qnaverse.dao.QuestionDAO;
import com.kathapatel.qnaverse.dao.VoteDAO;
import com.kathapatel.qnaverse.model.Answer;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.User;
import com.kathapatel.qnaverse.model.Vote;
import com.kathapatel.qnaverse.service.UserService;


@Controller
@RequestMapping("/answers")
public class AnswerController {

    // Injecting necessary DAOs and Services
    @Autowired
    private AnswerDAO answerDAO;
     
    @Autowired
    private QuestionDAO questionDAO;
    
    @Autowired 
    private VoteDAO voteDAO; 
    
    @Autowired
    private UserService userService; // For getting user details and handling reputation
    
    //Processes the submission of a new answer to a specific question
    @PostMapping("/post")
    public String postAnswer(
            @RequestParam("questionId") Long questionId,
            @RequestParam("content") String content,
            Principal principal, 
            RedirectAttributes redirectAttributes) {
        
        // Make sure someone is actually logged in
    	if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error. Please login.");
            return "redirect:/users/login?error=auth";
        }
        String userEmail = principal.getName(); 
        
        // Fetch the full User object for the logged-in user
        User currentUser = userService.getUserByEmail(userEmail); // Assuming you have this method
        if (currentUser == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Could not find user details.");
             // Redirect back to the question page might be best here
             return "redirect:/questions/" + questionId; 
        }
        // Find the question this answer belongs to
        Question question = questionDAO.getQuestionById(questionId);
        if (question == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Question not found.");
             return "redirect:/home"; // Redirect home if the question doesn't exist
        }
        
        // Create and populate the new Answer object
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setAnsweredBy(userEmail);
        answer.setPostedBy(userEmail);
        answer.setAnsweredAt(LocalDateTime.now()); // set timestamp
        answer.setVotes(0); //start with zero vote
        answer.setQuestion(question);
        answer.setAuthor(currentUser); 
        
        // Try saving the answer
        boolean success = answerDAO.saveAnswer(answer);
        if (!success) {
             redirectAttributes.addFlashAttribute("errorMessage", "Failed to save answer.");
        } else {
             redirectAttributes.addFlashAttribute("successMessage", "Answer posted successfully.");
        }

        return "redirect:/questions/" + questionId;
    }
 
    // handles voting for answer
    @PostMapping("/{id}/vote")
    public String voteAnswer(@PathVariable("id") Long id,
                             @RequestParam("vote") int vote, // vote = 1 for upvote, -1 for downvote
                             @RequestParam("questionId") Long questionId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

    	// voting requires login 
    	 if (principal == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to vote.");
             return "redirect:/questions/" + questionId;
         }
         String userEmail = principal.getName();

        // Validate vote value just in case
        if (vote != 1 && vote != -1) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid vote value.");
            return "redirect:/questions/" + questionId;
        }

        // Check if the user already voted on this answer
        // Assuming VoteType 2 = Answer
        Vote existingVote = voteDAO.getVoteByUserAndContent(userEmail, 2, id); 
        if (existingVote != null) {
            // User has voted before. Check if they are changing their vote or clicking the same button.
            if (existingVote.getValue() == vote) {
                // Clicking the same button again - maybe undo the vote? Or just ignore.
                // Current logic: Do nothing, inform user.
                redirectAttributes.addFlashAttribute("infoMessage", "You have already voted this way.");

            } else {
                // Changing vote (e.g., up to down, or down to up)
                int previousVoteValue = existingVote.getValue();
                existingVote.setValue(vote); // Update the vote value in the existing record
                boolean voteUpdated = voteDAO.updateVote(existingVote); // Persist the change

                if (voteUpdated) {
                    // Update the answer's total score.
                    // The change is the new vote minus the old vote (e.g., 1 - (-1) = 2, or -1 - 1 = -2)
                    int scoreChange = vote - previousVoteValue;
                    answerDAO.updateVote(id, scoreChange);
                    
                    // Also update user reputations involved
                    userService.updateReputationForAnswerVoteChange(id, userEmail, previousVoteValue, vote);
                    redirectAttributes.addFlashAttribute("successMessage", "Vote updated.");
                } else {
                    // Handle potential update failure
                    redirectAttributes.addFlashAttribute("errorMessage", "Could not update your vote. Please try again.");
                }
            }
        } else {
            // This is a completely new vote for this user on this answer
            Vote newVote = new Vote(userEmail, 2, id, vote); // Assuming a Vote class exists
            newVote.setUserEmail(userEmail); // Set appropriate fields
            newVote.setVoteType(2); // Or constant for Answer
            newVote.setContentId(id);
            newVote.setValue(vote);
            // Setting other fields like vote timestamp 

            boolean voteSaved = voteDAO.saveVote(newVote); // Save the new vote record

            if (voteSaved) {
                // Update the answer score with the new vote value
                answerDAO.updateVote(id, vote);
                // Update user reputations involved for a new vote
                userService.updateReputationForAnswerVote(id, userEmail, vote);
                redirectAttributes.addFlashAttribute("successMessage", "Vote recorded.");
            } else {
                // Handle potential save failure
                redirectAttributes.addFlashAttribute("errorMessage", "Could not record your vote. Please try again.");
            }
        }

        return "redirect:/questions/" + questionId;
    }
    
    
    //Shows the form for editing an existing answer
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, 
    							Principal principal, 
    							RedirectAttributes redirectAttributes) {

        // Editing requires login
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error.");
            return "redirect:/users/login?error=auth";
        }
        String email = principal.getName();
        
        // Fetch the answer to be edited
        Answer answer = answerDAO.getAnswerById(id);
        if (answer == null) {
            return "redirect:/home";
        }
        
        // Check if the logged-in user is the owner of this answer
        if (!answer.getAnsweredBy().equals(email)) {
            return "redirect:/questions/" + answer.getQuestion().getId() + "?error=unauthorized";
        }
        
        // If authorized, pass the answer object to the view
        model.addAttribute("answer", answer);
        // Pass user email perhaps for display purposes in the view
        model.addAttribute("userEmail", email);
        
        return "edit-answer";
    }
    
    //Processes the submission of an updated answer
    @PostMapping("/{id}/update")
    public String updateAnswer(
            @PathVariable("id") Long id,
            @RequestParam("content") String content,
            Principal principal, 
            RedirectAttributes redirectAttributes) {
        
        // Editing requires login
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error.");
            return "redirect:/users/login?error=auth";
        }
        String email = principal.getName();
        
        // Fetch the existing answer
        Answer answer = answerDAO.getAnswerById(id);
        if (answer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Answer not found.");
            return "redirect:/home";
       }
        // Check if the logged-in user is the owner of this answer
        // Using a check that handles if answeredBy might be null initially
        if ((answer.getAnsweredBy() == null && (answer.getPostedBy() != null && !answer.getPostedBy().equals(email))) || 
        	    (answer.getAnsweredBy() != null && !answer.getAnsweredBy().equals(email))) {
        	    redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this answer.");
        	    return "redirect:/questions/" + answer.getQuestion().getId();
        	}
        
        answer.setContent(content);
        
        boolean success = answerDAO.updateAnswer(answer); // Assumes DAO updates based on ID in answer object
        if(success) {
            redirectAttributes.addFlashAttribute("successMessage", "Answer updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update answer.");
        }        
        return "redirect:/questions/" + answer.getQuestion().getId();
    }
    
    
    //Handles the request to delete an answer
    @PostMapping("/{id}/delete")
    public String deleteAnswer(
            @PathVariable("id") Long answerId, 
            Principal principal, 
            RedirectAttributes redirectAttributes) {

         if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error.");
            return "redirect:/users/login?error=auth";
        }
        String email = principal.getName(); // Get email from Security Context

        Answer answer = answerDAO.getAnswerById(answerId); // Get answer first to check ownership and get question ID
        if (answer == null) {
             // Answer already deleted or never existed
             redirectAttributes.addFlashAttribute("infoMessage", "Answer not found.");
             return "redirect:/home"; // Redirect to home or a sensible default
        }

         Long questionId = answer.getQuestion().getId(); // Get question ID for redirect BEFORE deleting

        // Check ownership using email from Principal
         if ((answer.getAnsweredBy() == null && (answer.getPostedBy() != null && !answer.getPostedBy().equals(email))) || 
        		    (answer.getAnsweredBy() != null && !answer.getAnsweredBy().equals(email))) {
        		    redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this answer.");
        		    return "redirect:/questions/" + questionId;
        		}

        boolean success = answerDAO.deleteAnswer(answerId); // Now delete
        if(success) {
             redirectAttributes.addFlashAttribute("successMessage", "Answer deleted successfully.");
        } else {
             redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete answer.");
        }

        return "redirect:/questions/" + questionId;
    }
}
