package com.dreamsalive.fbdb.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import com.dreamsalive.fbdb.R;

import com.dreamsalive.fbdb.databinding.ActivityMainBinding;
import com.dreamsalive.fbdb.models.NotificationModel;
import com.dreamsalive.fbdb.models.User;
import com.dreamsalive.fbdb.receivers.NetworkChangeReceiver;
import com.dreamsalive.fbdb.storage.LocalPreferences;
import com.dreamsalive.fbdb.utils.Logger;
import com.dreamsalive.fbdb.utils.Utility;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.dreamsalive.fbdb.utils.Constants.CLOSE_TIME;
import static com.dreamsalive.fbdb.utils.Constants.MESSAGE;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATIONS;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_ID;
import static com.dreamsalive.fbdb.utils.Constants.NOTIFICATION_IMAGE;
import static com.dreamsalive.fbdb.utils.Constants.READ_STATUS;
import static com.dreamsalive.fbdb.utils.Constants.TITLE;
import static com.dreamsalive.fbdb.utils.Constants.USERS;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    boolean closeSent = false, openSent = false;
    LocalPreferences localPreferences;
    ActivityMainBinding binding;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        localPreferences = new LocalPreferences(getApplicationContext());
        utility = new Utility(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(USERS);
        registerBroadcastReceiver();
        sendOpenData();
        if (getIntent().getExtras() != null) {
            fetchNotificationData();
        }
    }

    public void registerBroadcastReceiver() {
        utility.setRecurringApiSync(6, false);
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        getApplicationContext().registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        getApplicationContext().registerReceiver(networkChangeReceiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
    }

    int open, close;
    User user;

    private void sendOpenData() {
        user = new User();
        open = (Integer.parseInt(localPreferences.getAppOpen()) + 1);
        close = (Integer.parseInt(localPreferences.getAppClose()) + 1);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Date date = new Date(currentTime);
        Logger.showMsg("date : " + date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
        String dateString = sdf.format(date);
        user.userId = localPreferences.getUserId();
        user.openTime = dateString;
        mDatabaseReference.child(user.userId).child(String.valueOf(open)).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                openSent = true;
                localPreferences.setAppOpen(String.valueOf(open));
                Logger.showMsg("date : onsuccess");
            }
        });
    }

    private void fetchNotificationData() {
        Intent i = getIntent();
        Bundle data = i.getExtras();
        if (data != null) {
            NotificationModel notificationModel = new NotificationModel();
            if (data.containsKey(NOTIFICATION_ID))
                notificationModel.notificationId = data.getString(NOTIFICATION_ID);
            if (data.containsKey(TITLE))
                notificationModel.title = data.getString(TITLE);
            if (data.containsKey(MESSAGE))
                notificationModel.message = data.getString(MESSAGE);
            if (data.containsKey(NOTIFICATION_IMAGE))
                notificationModel.notificationImage = data.getString(NOTIFICATION_IMAGE);
            updateNotificationReadStatus(notificationModel);
        }
    }

    private void updateNotificationReadStatus(NotificationModel notificationModel) {
        DatabaseReference mDatabaseReference = mDatabase.getReference(NOTIFICATIONS);
        mDatabaseReference.child(notificationModel.notificationId).child(READ_STATUS).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.showMsg("success update");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
        String dateString = sdf.format(date);
        user.closeTime = dateString;
        mDatabaseReference.child(user.userId).child(String.valueOf(open)).child(CLOSE_TIME).setValue(dateString).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                closeSent = true;
                localPreferences.setAppClose(String.valueOf(close));
            }
        });
    }
}