package com.example.farm.dueDateAlarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.farm.AnimalsExpectingToCalveActivity;
import com.example.farm.R;

import java.util.Date;

public class OnReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intN) {
        sendNotification(context,intN.getStringExtra("message"),intN.getStringExtra("title"));
    }

    private void sendNotification(Context context,String messageBody, String title) {
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showNotification(context, title, messageBody);
    }
        public void showNotification (Context context, String title, String messageBody){

            Intent intent = new Intent(context, AnimalsExpectingToCalveActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_notification_icon);

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


        public String createNotificationChannel(Context context){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String channelId = "Channel_id";
                CharSequence channelName = "Application_name";
                String channelDescription = "Application_name Alert";
                int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
                boolean channelEnableVibrate = true;

                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
                notificationChannel.setDescription(channelDescription);
                notificationChannel.enableVibration(channelEnableVibrate);

                // Adds NotificationChannel to system. Attempting to create an existing notification
                // channel with its original values performs no operation, so it's safe to perform the
                // below sequence.
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);

                return channelId;
            } else {
                return null;
            }
        }
    }
