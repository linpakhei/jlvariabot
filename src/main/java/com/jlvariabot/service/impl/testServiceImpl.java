package com.jlvariabot.service.impl;


import com.jlvariabot.service.testService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class testServiceImpl implements testService {
    @Value("${bot.prod.username}")
    private String prodUserName;
    @Value("${bot.prod.token}")
    private String prodToken;
    @Value("${bot.dev.username}")
    private String devUserName;
    @Value("${bot.dev.token}")
    private String devToken;

    @Override
    public String resource() {
        return "prod.username: " + prodUserName + ", prod.token: " + prodToken + ", dev.username: " + devUserName + ", dev.token: " + devToken;
    }
}
