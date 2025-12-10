package com.loreweave.loreweave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/errors/cannot-vote-own")
    public String cannotVoteOwn() {
        return "errors/cannot-vote-own";
    }
}
