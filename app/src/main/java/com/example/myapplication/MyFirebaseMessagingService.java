//package com.example.myapplication;
//
//import android.app.Service;
//import android.util.Log;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Map;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMessaging";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());
//
//        // Check if the message contains a notification payload
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            Log.d(TAG, "Notification Title: " + title);
//            Log.d(TAG, "Notification Body: " + body);
//
//            // Customize the handling of the notification message here
//            // You can display a notification, update the UI, or perform any other action
//        }
//
//        // Check if the message contains a data payload
//        if (remoteMessage.getData().size() > 0) {
//            Map<String, String> data = remoteMessage.getData();
//
//            // Extract the data values from the message
//            String key1 = data.get("key1");
//            String key2 = data.get("key2");
//
//            Log.d(TAG, "Data Payload: key1=" + key1 + ", key2=" + key2);
//
//            // Customize the handling of the data message here
//            // You can process the data, update the UI, or perform any other action
//        }
//    }
//
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed Token: " + token);
//
//        // Send the new FCM token to your server for further processing or storing
//        // Implement your logic here to handle token refresh
//    }
//}
