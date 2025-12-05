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
///
/// Updated By:   Jamie Coker on 2025-11-30
/// Update Notes: Added enforcement that prevents users from contributing twice in a row
///               to the same story. This uses StoryPartRepository.findTopByStoryIdOrderByPartOrderDesc
///               to retrieve the last contributor before allowing a new StoryPart.
/// 
/// Updated By:   Wyatt Bechtle
/// Update Notes: Refactored notification creation logic to ensure notifications include a link to the story
/// ==========================================


import java.util.Optional;
import com.loreweave.loreweave.model.StoryPart;
import com.loreweave.loreweave.model.Character;
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

        // ================================================
        // TURN ORDER RULE:
        // Prevent users from adding two consecutive parts.
        //
        // Uses new repository method:
        //   findTopByStoryIdOrderByPartOrderDesc(Long storyId)
        //
        // We compare the last contributor's userId with the current user's userId.
        // ================================================
        storyPartRepository.findTopByStoryIdOrderByPartOrderDesc(
                storyPart.getStory().getId())
                .ifPresent(lastPart -> {
            if (lastPart.getAuthor() != null &&
                    lastPart.getAuthor().getId().equals(character.getUser().getId())) {
                throw new RuntimeException(
                        "You cannot add another story part until another user contributes."
                );
            }
        });

        // Set the contributor and save
        storyPart.setContributor(character);
        StoryPart saved = storyPartRepository.save(storyPart);

        // Create and persist notification to story creator
        try {
            if (saved.getStory() != null && saved.getStory().getCreator() != null) {

                User creatorUser = saved.getStory().getCreator().getUser();

                if (creatorUser != null) {

                    String storyTitle = saved.getStory().getTitle() != null ? saved.getStory().getTitle() : "a story";
                    String fromUsername = character.getUser() != null ? character.getUser().getUsername() : "unknown";
                    String message = String.format("New contribution to %s from %s", storyTitle, fromUsername);

                    // Create and save the notification with a link to the story so the creator can navigate to it
                    String storyLink = "/story/" + saved.getStory().getId();
                    Notification notif = new Notification(creatorUser, character.getUser(), message, storyLink);

                    // Persist the notification to the db
                    Notification savedNotif = notificationService.createNotification(notif);

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
                        // ignore websocket errors
                    }
                }
            }
        } catch (Exception e) {
            // keep story part creation even if notifications fail
        }

        return saved;
    }

    // Create a new story part for a given story
    @Transactional
    public void createPartForStory(Long storyId, String content, User user) throws Exception {

        // Fetch the story
        var story = storyRepository.findById(storyId).orElseThrow();

        // Require attached character
        var character = user.getCharacter();
        if (character == null) {
            throw new IllegalStateException("You must create/select a character before contributing to a story.");
        }

        // Determine next partOrder
        int nextOrder = storyPartRepository.findMaxPartOrderForStory(storyId) + 1;

        /* Create and save the new story part */
        StoryPart sp = new StoryPart(story, character, content, nextOrder);
        sp.setAuthor(user);

        // Uses updated addStoryPart() which now enforces turn limits
        addStoryPart(sp, character);
    }

    public Optional<StoryPart> getStoryPartByIdWithContributorAndUser(Long id) {
        return storyPartRepository.findByIdWithContributorUserAndStory(id);
    }


}
