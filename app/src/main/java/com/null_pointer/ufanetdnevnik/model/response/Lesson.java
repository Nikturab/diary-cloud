package com.null_pointer.diarycloud.model.response;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Lesson {
    private String subject;
    private String homework;
    private String cabinet;
    private String mark;
    private String teacher_name;
    private String teacher_id;
    private String time;

    public Lesson() {
    }
    public Lesson(String subject, String time, String  homework, String cabinet, String mark) {
        this.subject = subject;
        this.time = time;
        this.homework = homework;
        this.cabinet = cabinet;
        this.mark = mark;
    }

    public String getSubject() {
        return subject;
    }


    public String getHomework() {
        return homework;
    }

    public String getCabinet() {
        return cabinet;
    }

    public String getMark() {
        return mark;
    }

    public String getTime() {
        return time;
    }

    public String getFullDescription(){
        return  "Время урока:\t" + time +
                "\nКабинет:\t" + cabinet +
                (!homework.isEmpty() ? "\nД/з:\t" + homework : "") +
                (!mark.isEmpty() ? "\nОценка: " + mark : "" );

    }

    public String getShortDescription(){
        return "кабинет «" + cabinet + "» " + ( homework.length() > 20 ? homework.substring(0, 20) + "..." : homework );
    }


    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (cabinet != null ? cabinet.hashCode() : 0);
        result = 31 * result + (homework != null ? homework.hashCode() : 0);
        result = 31 * result + (mark != null ? mark.hashCode() : 0);
        return result;
    }

    public String getTeacherName() {
        return teacher_name;
    }

    public String getTeacherId() {
        return teacher_id;
    }
}

