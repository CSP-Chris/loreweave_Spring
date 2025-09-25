// File Name: ContentController.java
// Created By: Wyatt Bechtle
// Created On: 9/15/2025
// Purpose: Handles HTTP requests.


// Updated By: <name> on <date>
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;


@Controller
public class ContentController {

    private final UserRepository userRepository;

    public ContentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/loginBSF")
    public String login() {
        return "loginBSF";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {
        userRepository.save(user);
        return "redirect:/loginBSF";
    }


    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
    
}
