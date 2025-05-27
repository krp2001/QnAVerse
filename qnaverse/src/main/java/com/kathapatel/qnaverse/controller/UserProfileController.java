package com.kathapatel.qnaverse.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kathapatel.qnaverse.dao.AnswerDAO;
import com.kathapatel.qnaverse.dao.QuestionDAO;
import com.kathapatel.qnaverse.dao.UserDAO;
import com.kathapatel.qnaverse.model.Answer;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.User;

@Controller
@RequestMapping("/users")
public class UserProfileController {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private QuestionDAO questionDAO;
    
    @Autowired
    private AnswerDAO answerDAO;
    
    //Handles requests related to viewing user profiles
    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to view profiles.");
            return "redirect:/users/login";
        }
        
        User requestedUser = userDAO.getUserById(id);
        if (requestedUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/home";
        }
        
        // Get the currently logged-in user
        User currentUser = userDAO.findByEmail(principal.getName());
        
        // Only allow users to access their own profile
        if (!requestedUser.getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own profile.");
            return "redirect:/home";
        }
        
        // Get user's questions with EAGER fetching
        List<Question> userQuestions = questionDAO.getQuestionsByUser(requestedUser.getEmail());
        
        // Get user's answers with EAGER fetching of questions
        List<Answer> userAnswers = answerDAO.getAnswersByUser(requestedUser.getEmail());
        
        model.addAttribute("user", requestedUser);
        model.addAttribute("userQuestions", userQuestions);
        model.addAttribute("userAnswers", userAnswers);
        
        return "user-profile";
    }

    @GetMapping("/profile-by-email")
    public String viewProfileByEmail(@RequestParam("email") String email, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to view profiles.");
            return "redirect:/users/login";
        }
        
        // Only allow users to access their own profile
        if (!email.equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own profile.");
            return "redirect:/home";
        }
        
        User user = userDAO.findByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/home";
        }
        
        return "redirect:/users/profile/" + user.getId();
    }
}
