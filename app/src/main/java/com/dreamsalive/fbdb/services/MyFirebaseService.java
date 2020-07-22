package com.dreamsalive.fbdb.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.dreamsalive.fbdb.ui.MainActivity;
import com.dreamsalive.fbdb.R;
import com.dreamsalive.fbdb.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.dreamsalive.fbdb.utils.Constants.CHANNEL_ID;
import static com.dreamsalive.fbdb.utils.Constants.MESSAGE;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_ID;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_IMAGE;
import static com.dreamsalive.fbdb.utils.Constants.TITLE;
import static com.dreamsalive.fbdb.utils.Constants.channelName;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful())
                            return;
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                        }
                    }
                });
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            NotificationModel notificationModel = new NotificationModel();
            if (data.containsKey(NOTIFICATION_ID)) {
                notificationModel.notificationId = data.get(NOTIFICATION_ID);
                if (data.containsKey(TITLE))
                    notificationModel.title = data.get(TITLE);
                if (data.containsKey(MESSAGE))
                    notificationModel.message = data.get(MESSAGE);
                if (data.containsKey(NOTIFICATION_IMAGE))
                    notificationModel.notificationImage = data.get(NOTIFICATION_IMAGE);
                sendNotification(notificationModel);
            }
        }
    }


    private void sendNotification(NotificationModel notificationModel) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NOTIFICATION_IMAGE, notificationModel.notificationImage);
        intent.putExtra(TITLE, notificationModel.title);
        intent.putExtra(MESSAGE, notificationModel.message);
        intent.putExtra(NOTIFICATION_ID, notificationModel.notificationId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notificationModel.title)
                .setContentText(notificationModel.message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }
}


