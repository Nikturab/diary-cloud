package com.null_pointer.diarycloud.model.response;

import com.null_pointer.diarycloud.model.DiaryCloud;

import java.util.HashMap;

public class NotificationsResponse extends CommonResponse {

    Notifications notifications;

    public Notifications getNotifications(){
        return notifications;
    }

    public class Notifications {


        boolean success;
        long mail;
        long rating; // �������, ��� ������� ���������
        String source;


        public boolean isSuccess() {
            return success;
        }

        public long getMail() {
            return mail;
        }

        public long getRating() {
            return rating;
        }

        public String getSource() {
            return source;
        }

    }

}