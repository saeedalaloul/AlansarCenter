package com.alansar.center.Notifications;


import androidx.annotation.NonNull;

import com.alansar.center.Common.Common;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {
    private FirebaseFirestore db;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        db = FirebaseFirestore.getInstance();
        updateToken(s);
    }

    private void updateToken(String s) {
        if (s != null && Common.currentPerson != null) {
            db.collection("Token")
                    .document(Common.currentPerson.getId())
                    .set(new Token(s));
        }
    }
}
