package com.loreweave.loreweave.service;

/// ==========================================
/// File Name:    StoryPartService.java
/// Created By:   Chris Ennis
/// Created On:   2025-10-08
/// Purpose:      Service for story part-related business logic
/// Update History:
/// 
/// Updated By:   Wyatt Bechtle
/// Update Notes: Ensured that the user has a character before allowing them to contribute to a story.
///               Created createPartForStory method to encapsulate logic for creating a new story part,
///               including order calculation and character validation. (Had to create new method to 
///               align with MVC Structure, but some with a little more knowledge on this could probably
///               refactor)
/// 
/// Updated By:   Wyatt Bechtle
/// Update Notes: Added notification logic to alert story creators when new parts are added to their stories.
///               Notifications are sent via WebSocket to the creator's personal queue.
/// ==========================================



import java.util.Optional;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.Character;
//import com.loreweave.loreweave.model.Story;
import com.loreweave.loreweave.model.User;
import com.loreweave.loreweave.repository.StoryRepository;
import com.loreweave.loreweave.repository.StoryPartRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.loreweave.loreweave.model.Notification;
import com.loreweave.loreweave.service.NotificationService;
import com.loreweave.loreweave.dto.ws.NotificationView;

@Service
public class StoryPartService {

    private final StoryPartRepository storyPartRepository;
    private final StoryRepository storyRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public StoryPartService(StoryPartRepository storyPartRepository,
                            StoryRepository storyRepository,
                            NotificationService notificationService,
                            SimpMessagingTemplate simpMessagingTemplate) {
        this.storyPartRepository = storyPartRepository;
        this.storyRepository = storyRepository;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @Transactional
    public StoryPart addStoryPart(StoryPart storyPart, Character character) throws Exception {

        // Require an attached character up front 
        if (character == null) {
            throw new IllegalStateException("You must create/select a character before contributing to a story.");
        }
        // Enforce no consecutive contributions by the same character
        List<StoryPart> latestParts = storyPartRepository.findLatestStoryPartsForStory(storyPart.getStory().getId());
        if (!latestParts.isEmpty()) {
            StoryPart lastPart = latestParts.getFirst();
            if (lastPart.getContributor().getId().equals(character.getId())) {
                throw new Exception("You cannot add another story part until another user contributes.");
            }
        }
        // Set the contributor and save
        storyPart.setContributor(character);
        StoryPart saved = storyPartRepository.save(storyPart);

        // Create and persist a notification for the story creator (author of the story)
        try {
            // Only notify if the story has a creator with an associated user
            if (saved.getStory() != null && saved.getStory().getCreator() != null) {

                // Get the creator's user
                User creatorUser = saved.getStory().getCreator().getUser();
                
                if (creatorUser != null) {

                    // Get story title, message, and sender username
                    String storyTitle = saved.getStory().getTitle() != null ? saved.getStory().getTitle() : "a story";
                    String fromUsername = character.getUser() != null ? character.getUser().getUsername() : "unknown";
                    String message = String.format("New contribution to %s from %s", storyTitle, fromUsername);

                    // Create and save the notification
                    Notification notif = new Notification(creatorUser, character.getUser(), message);
                    Notification savedNotif = notificationService.createNotification(notif);

                    // Send via WebSocket to the creator's personal queue
                    try {
                        simpMessagingTemplate.convertAndSendToUser(
                                creatorUser.getUsername(),
                                "/queue/notifications",
                                new NotificationView(
                                        savedNotif.getMessage(),
                                        savedNotif.getSender().getUsername(),
                                        savedNotif.getCreatedAt().toString()
                                )
                        );
                    } catch (Exception e) {
                        // ignore websocket send failures (leave notification persisted)
                    }
                }
            }
        } catch (Exception e) {
            // intentionally swallow notification-related errors to avoid breaking contribution flow
        }
        return saved;
    }
    // Create a new story part for a given story
    @Transactional
    public void createPartForStory(Long storyId, String content, User user) throws Exception {

        // Fetch the story
        var story = storyRepository.findById(storyId).orElseThrow();

        // Require an attached character up front
        var character = user.getCharacter();
        if (character == null) {
            throw new IllegalStateException("You must create/select a character before contributing to a story.");
        }
        // Determine the next order number
        int nextOrder = storyPartRepository.findMaxPartOrderForStory(storyId) + 1;

        /* Create and save the new story part */
        StoryPart sp = new StoryPart(story, character, content, nextOrder);
        sp.setAuthor(user);
        addStoryPart(sp, character);
    }

//    public Optional<StoryPart> getStoryPartById(Long id) {
//        return storyPartRepository.findById(id);
//    }

    public Optional<StoryPart> getStoryPartByIdWithContributorAndUser(Long id) {
        return storyPartRepository.findByIdWithContributorUserAndStory(id);
    }

}
