package com.example.farm.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFireBaseMsgService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "OK");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data: " + remoteMessage.getData().toString());

        String message,title,uid;
        title=getString(R.string.app_name);
        message="No data";
        uid = "";
        if(remoteMessage.getNotification() != null)
        {
            if(remoteMessage.getNotification().getTitle() !=null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if(remoteMessage.getNotification().getBody() !=null) {
                message = remoteMessage.getNotification().getBody();
            }
        }
        if(remoteMessage.getData()!=null)
        {
//            if(remoteMessage.getData().get("pick_data")!=null)
//            {
//                pickInfo = remoteMessage.getData().get("pick_data");
//            }
            if(remoteMessage.getData().get("title")!=null) {
                title = remoteMessage.getData().get("title");
            }
            if(remoteMessage.getData().get("message")!=null) {
                message = remoteMessage.getData().get("message");
            }
            if(remoteMessage.getData().get("sender")!=null) {
                uid = remoteMessage.getData().get("sender");
            }
        }
        if(FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            sendNotification(message, title);
        }
    }

    //    This method is only generating push notification
    private void sendNotification(String messageBody, String title) {

        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        if(!pickInfo.equals(""))
//        {
//            Log.d(TAG, "Data: Pick");
////            PickInfo pickInfoItem= new Gson().fromJson(pickInfo, PickInfo.class);
////            pickInfoItem.setStatus(1);
//            CommonMethods.setStringPreference(this,getString(R.string.app_name),PickInfo.class.getSimpleName(),pickInfo);
//            intent.putExtra(PickInfo.class.getSimpleName(),pickInfo);
//        }

//        if(isAppIsInBackground(this)) {
//            Log.d(TAG, "Data: Notification");
//        }
        showNotification(this,title,messageBody);

    }

    public void showNotification(Context context, String title, String messageBody) {

        Intent intent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                /*.setLargeIcon(largeIcon)*/
                .setSmallIcon(R.mipmap.ic_launcher) //needs white icon with transparent BG (For all platforms)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent)
                .setPriority(android.app.Notification.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    public String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "Channel_id";

            CharSequence channelName = "Application_name";
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return null;
        }
    }
}
