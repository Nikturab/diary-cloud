package com.null_pointer.diarycloud.model;

/*
    Сырое представление ответа.
    В DiaryCloud оно конвертируется в DiaryResponse и т.д.
 */
public class DiaryCloudResponse {
    private int code;
    private String body;


    public DiaryCloudResponse(String body, int code){
        this.code = code;
        this.body = body;
    }

    public String getBody(){
        return body;
    }

}
