// File Name: ContentController.java
// Created By: Wyatt Bechtle
// Created On: 9/15/2025
// Purpose: Handles HTTP requests.


// Updated By: Wyatt Bechtle on 9/21/2025
//              Added a model object to the register mapping
// Updated By: Wyatt Bechtle on 10/03/2025
//              Added password encoding to the register post mapping
// Updated By: Wyatt Bechtle on 10/20/2025
//              Deleted duplicate methods and associated imports and attributes
//              for register.
//              Methods were implemented in AuthController instead.
//              Deleted a duplicate method for create story. This was implemented
//              in StoryPageController instead.

package com.loreweave.loreweave.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {


    public ContentController() {
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
    
}
