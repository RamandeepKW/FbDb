package com.dreamsalive.fbdb.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.dreamsalive.fbdb.R;
import com.dreamsalive.fbdb.models.NotificationModel;
import com.dreamsalive.fbdb.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.dreamsalive.fbdb.utils.Constants.CHANNEL_ID;
import static com.dreamsalive.fbdb.utils.Constants.MESSAGE;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATIONS;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_ID;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_IMAGE;
import static com.dreamsalive.fbdb.utils.Constants.READ_STATUS;
import static com.dreamsalive.fbdb.utils.Constants.TITLE;
import static com.dreamsalive.fbdb.utils.Constants.channelName;

public class TaskExecutor {
    Context context;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
    boolean skip = false;

    public TaskExecutor(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void syncServerData() {
        getNotificationData();
    }

    private void getNotificationData() {
        mDatabaseReference = mDatabase.getReference(NOTIFICATIONS);
        mDatabaseReference.orderByChild(READ_STATUS).equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    skip = false;
                    final NotificationModel notificationModel = new NotificationModel();
                    notificationModel.title = Objects.requireNonNull(data.getValue(NotificationModel.class)).title;
                    notificationModel.readStatus = Objects.requireNonNull(data.getValue(NotificationModel.class)).readStatus;
                    notificationModel.message = Objects.requireNonNull(data.getValue(NotificationModel.class)).message;
                    notificationModel.notificationId = Objects.requireNonNull(data.getValue(NotificationModel.class)).notificationId;
                    notificationModel.notificationImage = Objects.requireNonNull(data.getValue(NotificationModel.class)).notificationImage;
                    sendNotification(notificationModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.showMsg("onError : " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(NotificationModel notificationModel) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NOTIFICATION_IMAGE, notificationModel.notificationImage);
        intent.putExtra(TITLE, notificationModel.title);
        intent.putExtra(MESSAGE, notificationModel.message);
        intent.putExtra(NOTIFICATION_ID, notificationModel.notificationId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationModel.title)
                .setContentText(notificationModel.message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(Integer.parseInt(notificationModel.notificationId), notificationBuilder.build());
    }
}
