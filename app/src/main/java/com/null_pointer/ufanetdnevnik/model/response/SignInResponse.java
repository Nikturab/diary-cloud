package com.null_pointer.diarycloud.model.response;

import java.util.Collection;

/**
 * Created by null_pointer on 06.05.17.
 */
public class SignInResponse extends CommonResponse{
    private String token;
    private String name;

    public String getToken(){
        return token;
    }
    public String getName(){
        return name;
    }
}
