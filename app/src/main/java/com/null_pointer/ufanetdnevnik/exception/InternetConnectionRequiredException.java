package com.null_pointer.diarycloud.exception;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

public class InternetConnectionRequiredException extends DiaryCloudException {
    public InternetConnectionRequiredException(String message) {
        super(message);
    }

    public InternetConnectionRequiredException() {
        super("");
    }
}
