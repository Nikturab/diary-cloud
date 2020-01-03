package com.null_pointer.diarycloud.model.response;

import java.util.Arrays;
public class Day {
    private Lesson[] lessons;
    private String date;
    private String real_date;

    public Day(Lesson[] lessons, String date, String real_date){
        this.lessons = lessons;
        this.date = date;
        this.real_date = real_date;
    }
    public Lesson[] getLessons(){
        return lessons;
    }
    public String getDate(){
        return date;
    }
//    public long getWeek(){
//       return DiaryCloud.getMonday( DiaryCloud.getUnFormattedDate(getDate(), "dd.MM.yy") ).getTime();
//    }
//    public void setDate(String date){
//        this.date = date;
//    }
    public String getRealDate(){
        return real_date.split("\n")[0];
    }

    public boolean isHoliday(){
        return real_date.split("\n").length>1;
    }

    public int hashCode(){
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (real_date != null ? real_date.hashCode() : 0);
        result = 31 * result + (lessons != null ? Arrays.asList(lessons).hashCode() : 0);
        return result;
    }
}
