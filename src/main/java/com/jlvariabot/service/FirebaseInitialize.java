package com.jlvariabot.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Service
public class FirebaseInitialize {
    @Value("${bot.env}")
    private String env;

    @Value("${bot.prod.json}")
    private String prodJson;
    @Value("${bot.dev.json}")
    private String devJson;

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("prod".equals(env) ? prodJson : devJson);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
