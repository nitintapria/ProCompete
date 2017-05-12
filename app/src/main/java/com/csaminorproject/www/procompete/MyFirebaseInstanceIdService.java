package com.csaminorproject.www.procompete;

/**
 * Created by Nitin on 19/04/2017.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

//The class MyFirebaseInstanceIdService will be a service used to handle FCM logic.
// This service is used to alert the application when a new InstanceID token is generated,
// and to retrieve the generated token. Here we override the onTokenRefresh method to
// subscribe to a topic.
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String PROCOMPETE_ENGAGE_TOPIC = "procompete_engage";

    /**
     * The Application's current Instance ID token is no longer valid and thus a new
     * one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(PROCOMPETE_ENGAGE_TOPIC);
    }
}
