package com.loreweave.loreweave;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomePageController {

    @GetMapping("/c")
    public String redirectToStaticPage() {
        return "redirect:contributors.html";
    }
}

