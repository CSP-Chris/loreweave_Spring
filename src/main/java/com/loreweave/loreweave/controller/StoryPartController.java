/// ==========================================
/// File Name:    StoryPartController.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Controller for handling story part creation
/// Update History: 
/// 
///  Updated By:    Wyatt Bechtle
///  Update Notes:  Set story part author to authenticated user
///  
/// ==========================================

package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.service.StoryPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class StoryPartController {

    private final StoryPartService storyPartService;

    public StoryPartController(StoryPartService storyPartService) {
        this.storyPartService = storyPartService;
    }

    @MessageMapping("/story/{storyId}")
    @SendTo("/topic/story/{storyId}")
    public StoryPart addPart(@RequestBody StoryPart storyPart, @AuthenticationPrincipal User user) throws Exception {
        storyPart.setAuthor(user);
        return storyPartService.addStoryPart(storyPart, user.getCharacter());
    }

    @PostMapping("/api/storyparts")
    public ResponseEntity<StoryPart> addPartRest(@RequestBody StoryPart storyPart, @AuthenticationPrincipal User user) {
        try {
            StoryPart newStoryPart = storyPartService.addStoryPart(storyPart, user.getCharacter());
            return ResponseEntity.ok(newStoryPart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
