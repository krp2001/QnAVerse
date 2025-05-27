package com.kathapatel.qnaverse.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kathapatel.qnaverse.dao.UserDAO;
import com.kathapatel.qnaverse.model.User;

import org.springframework.ui.Model;


//Handles requests related to the user login page display

@Controller
@RequestMapping("/users")
public class LoginController {
	
	@Autowired
	private UserDAO userDAO;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new User()); // For form binding if needed
        return "login"; 
    }
   
   
}

