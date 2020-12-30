package com.jlvariabot.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.jlvariabot.dao.FirebaseDao;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseDaoImpl implements FirebaseDao {
    @Override
    public Map<String, Object> getGameResultData(String id, String gameName) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> query = db.collection(id).get();

            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                if(gameName.equals(document.getId())) {
                    Map<String, Object> result = new HashMap<>();

                    result.put("win", document.get("win"));
                    result.put("draw", document.get("draw"));
                    result.put("lose", document.get("lose"));

                    return result;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ApiFuture<WriteResult> addGameResultData(String id, Map<String, Object> data, String gameName) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(id).document(gameName);
        return docRef.set(data);
    }

    @Override
    public ApiFuture<WriteResult> addUserData(String id, Update update) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(id).document("userInfo");

        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> userInfo = oMapper.convertValue(update.getCallbackQuery().getFrom(), Map.class);

        return docRef.set(userInfo);
    }
}
