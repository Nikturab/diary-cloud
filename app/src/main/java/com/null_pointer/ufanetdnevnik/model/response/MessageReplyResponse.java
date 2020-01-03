package com.null_pointer.diarycloud.model.response;

/**
 * Created by null_pointer on 05.05.17.
 */

public class MessageReplyResponse extends CommonResponse {
    private boolean status;
    public boolean isMessageSent(){
        return status;
    }
}
