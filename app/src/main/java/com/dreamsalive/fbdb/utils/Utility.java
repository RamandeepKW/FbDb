package com.dreamsalive.fbdb.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.dreamsalive.fbdb.receivers.SyncApiAlarmReceiver;

import java.util.Calendar;

public class Utility {
    Context context;

    public Utility(Context context) {
        this.context = context;
    }


    public void setRecurringApiSync(int timeDelay, boolean isReleased) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, SyncApiAlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context, 111, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (alarms != null)
            if (!isReleased) {
                Calendar updateTime = Calendar.getInstance();
                updateTime.set(Calendar.SECOND, 1);
                alarms.cancel(recurringDownload);
                alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), timeDelay * 3600000 , recurringDownload); //will run it after every 30 minute.
            } else {
                alarms.cancel(recurringDownload);
            }
    }

    public boolean isNetworkConnected() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr != null)
                return conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
