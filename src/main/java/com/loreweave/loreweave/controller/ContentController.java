// File Name: ContentController.java
// Created By: Wyatt Bechtle
// Created On: 9/15/2025
// Purpose: Handles HTTP requests.


// Updated By: Wyatt Bechtle on 9/21/2025
//              Added a model object to the register mapping
// Updated By: Wyatt Bechtle on 10/03/2025
//              Added password encoding to the register post mapping
// Updated By: Wyatt Bechtle on 10/20/2025
//              Deleted duplicate methods and associated imports and attributes.
//              Methods were implemented in AuthController instead.
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.User;

import com.loreweave.loreweave.service.StoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;


@Controller
public class ContentController {

    private final StoryService storyService;

    public ContentController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping("/create-story")
    public String createStory(@RequestBody Story story, @AuthenticationPrincipal User user) {
        story.setCreator(user.getCharacter());
        storyService.createStory(story);
        return "redirect:/";
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
