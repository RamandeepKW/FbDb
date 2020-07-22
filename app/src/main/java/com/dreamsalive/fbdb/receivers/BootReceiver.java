package com.dreamsalive.fbdb.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dreamsalive.fbdb.utils.Logger;
import com.dreamsalive.fbdb.utils.Utility;


public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility utility= new Utility(context);
        try {
            NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
            context.registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            context.registerReceiver(networkChangeReceiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
            utility.setRecurringApiSync(6, false);
        } catch (Exception e) {
            Logger.showMsg(" exception here: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
