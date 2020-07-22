package com.dreamsalive.fbdb.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    public String notificationId;
    public String notificationImage;
    public String title;
    public String message;
    public boolean readStatus = false;
}
