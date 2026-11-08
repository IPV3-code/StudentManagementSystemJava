package com.studentmanagement.notification;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService{
    @Override
    public void sendNotification(String message){
        System.out.println(("Sending Email notification : " + message));
    }
}
