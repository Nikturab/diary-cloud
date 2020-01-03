package com.null_pointer.diarycloud.model.response;

import java.util.HashMap;

/**
 * Created by null_pointer on 04.05.17.
 */

public class GradesResponse extends CommonResponse{
    private HashMap<String, MarkOfDay[]> marks;

    public HashMap<String, MarkOfDay[]> getMarks() {
        return marks;
    }

    public class MarkOfDay{
        private String mark, date;

        public String getMark() {
            return mark;
        }

        public String getDate() {
            return date;
        }
    }
}
