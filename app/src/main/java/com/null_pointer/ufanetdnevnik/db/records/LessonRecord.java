package com.null_pointer.diarycloud.db.records;


import com.orm.SugarRecord;

/*
    ORM представление Урока
 */

public class LessonRecord extends SugarRecord {


    String subject;
    String time;
    String cabinet;
    String homework;
    String mark;

    DayRecord day;


    public LessonRecord(){}

    public LessonRecord(String subject, String time, String cabinet, String homework, String mark, DayRecord day) {

        this.subject = subject;
        this.time = time;
        this.cabinet = cabinet;
        this.homework = homework;
        this.mark = mark;
        this.day = day;
    }

    /*
        Not similar to hashCode()
        generateHash() uses only entity data LIKE subject, time, homework, NOT LIKE SugarRecord etc
     */
    public int generateHash() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (cabinet != null ? cabinet.hashCode() : 0);
        result = 31 * result + (homework != null ? homework.hashCode() : 0);
        result = 31 * result + (mark != null ? mark.hashCode() : 0);
        return result;
    }

    public static void removeLessonsByDayId(long day){
        SugarRecord.deleteAll(LessonRecord.class, "day = ?", day + "");
    }


    public String getSubject() {
        return subject;
    }

    public String getTime() {
        return time;
    }

    public String getCabinet() {
        return cabinet;
    }

    public String getHomework() {
        return homework;
    }

    public String getMark() {
        return mark;
    }

    public DayRecord getDay() {
        return day;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setDay(DayRecord day) {
        this.day = day;
    }

}
