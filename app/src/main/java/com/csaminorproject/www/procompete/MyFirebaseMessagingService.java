package com.csaminorproject.www.procompete;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// You can use Firebase Cloud Messaging (FCM) to send notifications to users of your app.
// The class MyFirebaseMessagingService will be the background service that handles incoming
// FCM messages. It automatically handles notification messages, which are messages that
// the server specifies should produce a notification.
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }

}
