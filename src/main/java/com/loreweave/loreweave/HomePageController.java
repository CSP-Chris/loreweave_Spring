package com.loreweave.loreweave;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Loreweave";
    }

}

