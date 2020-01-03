package com.null_pointer.diarycloud.model.response;

/**
 * Created by null_pointer on 04.05.17.
 */

public class CommonResponse {
    boolean error;
    String error_type;
    String message;

    public boolean hasError(){
        return error;
    }

    public String getErrorType(){
        return error_type;
    }

    public String getMessage(){
        return message;
    }
}
