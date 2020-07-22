package com.dreamsalive.fbdb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dreamsalive.fbdb.utils.TaskExecutor;
import com.dreamsalive.fbdb.utils.Utility;

public class SyncApiAlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Utility utility = new Utility(context);
        if ((utility.isNetworkConnected())) {
            new TaskExecutor(context).syncServerData();
        }
    }


}