package com.null_pointer.diarycloud.model.response;

import java.util.ArrayList;
import java.util.HashMap;

public class Week {
    private long monday;
    private HashMap<String, Day> days;

    public Week(long monday, HashMap<String, Day> days) {
        this.monday = monday;
        this.days = days;
    }

    public HashMap<String, Day> getDays(){
        return days;
    }

    public long getMonday(){
        return monday;
    }

    public int hashCode(){
        int result = (int) (monday ^ (monday >>> 32));
        result = 31 * result + ( new ArrayList<>(days.values()).hashCode() );
        return result;
    }
}
