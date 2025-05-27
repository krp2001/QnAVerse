package com.kathapatel.qnaverse.controller;

import com.kathapatel.qnaverse.model.User;

import jakarta.validation.Valid;

import com.kathapatel.qnaverse.dao.UserDAO;

import java.time.LocalDateTime;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.kathapatel.qnaverse.service.EmailService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private EmailService emailService;

   
    @Autowired 
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; 
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, 
                                    BindingResult bindingResult,
                                    Model model) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        // Check if user already exists
        if (userDAO.findByEmail(user.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.user", "An account already exists for this email.");
            return "register";
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        
        // Save the user
        boolean success = userDAO.saveUser(user);
        if (success) {
            return "redirect:/users/login";
        } else {
            model.addAttribute("error", "Registration failed.");
            return "register";
        }
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                       RedirectAttributes redirectAttributes) {
        User user = userDAO.findByEmail(email);
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "No account found with that email address.");
            return "redirect:/users/forgot-password";
        }
        
        // Generate a random token
        String token = UUID.randomUUID().toString();
        
        // Set token expiry to 24 hours from now
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        
        // Save token to database
        boolean updated = userDAO.setResetToken(email, token, expiry);
        
        if (!updated) {
            redirectAttributes.addFlashAttribute("error", "Error processing your request. Please try again.");
            return "redirect:/users/forgot-password";
        }
        
        try {
            // Send email with reset link
            emailService.sendPasswordResetEmail(email, token);
            
            redirectAttributes.addFlashAttribute("success", 
                "A password reset link has been sent to your email address. Please check your inbox.");
            return "redirect:/users/forgot-password";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error sending email. Please try again later.");
            return "redirect:/users/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, 
                                       RedirectAttributes redirectAttributes) {
        User user = userDAO.findByResetToken(token);
        
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", 
                "Invalid or expired password reset link. Please request a new one.");
            return "redirect:/users/forgot-password";
        }
        
        model.addAttribute("token", token);
        return "reset-password";
    }

    //Processes the password reset form submission
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                     @RequestParam("password") String password,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     RedirectAttributes redirectAttributes) {
        User user = userDAO.findByResetToken(token);
        
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", 
                "Invalid or expired password reset link. Please request a new one.");
            return "redirect:/users/forgot-password";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/users/reset-password";
        }
        
        // Validate password strength
        if (!isPasswordValid(password)) {
            redirectAttributes.addFlashAttribute("error", 
                "Password must be at least 8 characters and include uppercase, lowercase, number and special character.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/users/reset-password";
        }
        
        // Hash the new password
        String hashedPassword = passwordEncoder.encode(password);
        
        // Update the password in the database
        boolean updated = userDAO.updatePassword(user.getId(), hashedPassword);
        
        if (updated) {
            redirectAttributes.addFlashAttribute("success", 
                "Your password has been reset successfully. You can now login with your new password.");
            return "redirect:/users/login";
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "Error resetting password. Please try again.");
            return "redirect:/users/forgot-password";
        }
    }

    private boolean isPasswordValid(String password) {
        // At least 8 chars, with uppercase, lowercase, digit and special character
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }
}