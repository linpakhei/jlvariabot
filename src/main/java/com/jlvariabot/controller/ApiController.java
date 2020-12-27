package com.jlvariabot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
    @Value("${bot.prod.username}")
    private String prodUserName;
    @Value("${bot.prod.token}")
    private String prodToken;
    @Value("${bot.dev.username}")
    private String devUserName;
    @Value("${bot.dev.token}")
    private String devToken;


    @GetMapping("/testing")
    public String greeting(@RequestParam(value = "name", required = false) String name) {
//        return "prodUserName: " + prodUserName + ", prodToken: " + prodToken;
        return "";
    }
}
