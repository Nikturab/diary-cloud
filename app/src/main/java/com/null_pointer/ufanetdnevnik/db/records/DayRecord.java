package com.null_pointer.diarycloud.db.records;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.List;

/*
    ORM представление дня
 */
public class DayRecord extends SugarRecord /* My sugar baby love */
{
    @Unique String date;
    String real_date;
    WeekRecord week;

    @Ignore
    List<LessonRecord> lessons  = new ArrayList<>();

    public DayRecord(){}

    public DayRecord(String date, String real_date, WeekRecord week) {
        this.date = date;
        this.real_date = real_date;
        this.week = week;
    }

    public static DayRecord getDayRecord(String date){
        List<DayRecord> list = SugarRecord.find(DayRecord.class, "date = ?", date);
        if(list == null || list.size() == 0) return null;
        return list.get(0);
    }

    public String getReal_date() {
        return real_date;
    }

    public void setReal_date(String real_date) {
        this.real_date = real_date;
    }

    public void setWeek(WeekRecord week) {
        this.week = week;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<LessonRecord> getLessons() {
        return lessons;
    }

    public void addLesson(LessonRecord lesson){
        lessons.add(lesson);
    }

    public String getDate() {
        return date;
    }

    public WeekRecord getWeek() {
        return week;
    }

    public List<LessonRecord> getLessonsRecords(DayRecord day) {
        return SugarRecord.find(LessonRecord.class, "day = ?", day.getId() + "");
    }
}
