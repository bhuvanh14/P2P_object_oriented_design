package com.skillshare.skillshareapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root(HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }

        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "home";
    }
}