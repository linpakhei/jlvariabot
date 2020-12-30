package com.jlvariabot.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.jlvariabot.utils.log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class ApiController {

    @GetMapping("/testing")
    public String greeting(@RequestParam(value = "name", required = false) String name) {
        log.info("enter controller");
        String output = "";

        try {
            Firestore db = FirestoreClient.getFirestore();

            // asynchronously retrieve all users
            ApiFuture<QuerySnapshot> query = db.collection("user").get();
            // ...
            // query.get() blocks on response
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot document : documents) {
//                System.out.println("User: " + document.getId());
//                System.out.println("First: " + document.getString("first"));
//                if (document.contains("middle")) {
//                    System.out.println("Middle: " + document.getString("middle"));
//                }
//                System.out.println("Last: " + document.getString("last"));
//                System.out.println("Born: " + document.getLong("born"));
                output += "name: " + document.getString("name") + ", age: " + document.getString("age") + ", location: " + document.getString("location") + "\n";
                output += "id: " + document.getId();

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        log.info("finish controller");

        return output;
    }
}
