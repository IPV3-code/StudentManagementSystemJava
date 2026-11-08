package com.studentmanagement.notification;

import org.springframework.stereotype.Service;

@Service("smsNotificationService")
public class SMSNotificationService implements NotificationService{
    @Override
    public void sendNotification(String message){
        System.out.println("Sending SMS notification: " + message);
    }
}
