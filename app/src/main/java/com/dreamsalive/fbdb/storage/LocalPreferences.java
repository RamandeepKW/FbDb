package com.dreamsalive.fbdb.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.dreamsalive.fbdb.utils.Constants;

import static com.dreamsalive.fbdb.utils.Constants.PREF_NAME;

public class LocalPreferences {
    Context context;
    private SharedPreferences sharedPreferencesEnc;
    private SharedPreferences.Editor editorEnc;

    @SuppressLint("CommitPrefEdits")
    public LocalPreferences(Context context1) {
        this.context = context1;
        sharedPreferencesEnc = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editorEnc = sharedPreferencesEnc.edit();
    }

    public void setAppOpen(String value) {
         editorEnc.putString(Constants.APP_OPEN, value).commit();
    }

    public String getAppOpen() {
        return sharedPreferencesEnc.getString(Constants.APP_OPEN, "0");
    }

    public void setAppClose(String value) {
         editorEnc.putString(Constants.APP_CLOSE, value).commit();
    }

    public String getAppClose() {
        return sharedPreferencesEnc.getString(Constants.APP_CLOSE, "0");
    }

    public String getUserId() {
        return sharedPreferencesEnc.getString(Constants.USER_ID, "1");
    }
}



