package com.null_pointer.diarycloud.model.response;

/**
 * Created by Ayili Nikturab on 10.05.2017.
 */

public class MessageReadResponse extends CommonResponse {
    MessageReadStatus status;

    public MessageReadStatus getStatus(){
        return status;
    }
    public class MessageReadStatus {
        boolean success;
        String source;
        int mid;
        public boolean hasError(){
            return !success;
        }
    }
}
