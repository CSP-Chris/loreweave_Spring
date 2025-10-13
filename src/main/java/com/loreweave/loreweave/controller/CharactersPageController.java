/// ==========================================
/// File Name:    CharactersPageController.java
/// Created By:   Wyatt Bechtle
/// Created On:   2025-10-08
/// Purpose:      Handles character listing requests
///  Updated By: 
///  Update Notes:
///  
/// ==========================================
package com.loreweave.loreweave.controller;

import com.loreweave.loreweave.model.Character;
import com.loreweave.loreweave.repository.CharacterRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CharactersPageController {

    private final CharacterRepository characterRepository;

    public CharactersPageController(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }
    // Characters listing page
    @GetMapping("/characters")
    public String characters(Model model) {
        List<Character> characters = characterRepository.findAll();
        model.addAttribute("characters", characters);
        return "characters"; 
    }
    // Character detail page
    @GetMapping("/characters/{id}")
    public String character(@PathVariable Long id, Model model) {
        var ch = characterRepository.findById(id).orElseThrow();
        model.addAttribute("character", ch);
        return "character-detail"; 
    }
}
