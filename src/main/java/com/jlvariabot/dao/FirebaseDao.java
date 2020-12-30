package com.jlvariabot.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Map;

public interface FirebaseDao {
    Map<String, Object> getGameResultData(String id, String gameName);
    ApiFuture<WriteResult> addGameResultData(String id, Map<String, Object> data, String gameName);
    ApiFuture<WriteResult> addUserData(String id, Update update);
}
