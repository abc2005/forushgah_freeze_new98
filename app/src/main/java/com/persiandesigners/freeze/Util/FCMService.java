package com.persiandesigners.freeze.Util;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.persiandesigners.freeze.Home;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "this";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {//agar az noe notifiction bud
            Log.d(TAG, "Notification : " + remoteMessage.getNotification().getBody());
            sendMessageToActivity(remoteMessage.getNotification().getBody(), remoteMessage,"");
        } else {//agar az noe Data bud
            Log.v("this", "body :" + remoteMessage.getData().get("body"));
            Log.v("this", "title :" + remoteMessage.getData().get("title"));
            Log.v("this", "icon :" + remoteMessage.getData().get("icon"));
            Log.v("this", "myData :" + remoteMessage.getData().get("myData"));
            //createNotification(remoteMessage.getData().get("body"),remoteMessage);
            sendMessageToActivity(remoteMessage.getData().get("body"), remoteMessage,
                    remoteMessage.getData().get("icon"));
        }
    }

    private void sendMessageToActivity(String body, RemoteMessage remoteMessage,String imageUrl) {
        new NotificationHelper(this).createNotification(remoteMessage.getData().get("title"),
                body, imageUrl , Home.class);
    }
}


