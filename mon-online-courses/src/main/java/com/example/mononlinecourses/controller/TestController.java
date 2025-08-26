package com.example.mononlinecourses.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/test")
public class TestController {

    @GetMapping("/create-course")
    public String createCourse() {
        return "create-course";
    }

    @GetMapping("/show-image")
    public String showImage() {
        return "show-course-image";
    }


    @GetMapping("/login-page")
    public String loginPage() {
        return "login-page";
    }

}
