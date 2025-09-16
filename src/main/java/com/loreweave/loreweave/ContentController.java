// File Name: login.html
// Created By: Wyatt Bechtle
// Created On: 9/15/2025
// Purpose: To provide login capabilities to the loreweave application


// Updated By: <name> on <date>
package com.loreweave.loreweave;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ContentController {
    
    @GetMapping("/loginBSF")
    public String login() {
        return "loginBSF";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
    
}
