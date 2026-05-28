package com.nageoffer.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShortLinkNotFoundController {

    @GetMapping("/page/notFound")
    public String shortLinkNotFound() {
        return "notFound";
    }

}
