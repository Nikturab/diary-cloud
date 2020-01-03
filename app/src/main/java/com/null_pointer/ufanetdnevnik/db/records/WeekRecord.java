package com.null_pointer.diarycloud.db.records;

import android.util.Log;

import com.null_pointer.diarycloud.model.response.Day;
import com.null_pointer.diarycloud.model.response.Lesson;
import com.null_pointer.diarycloud.model.response.Week;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/*
ORM представление недели
Содержит статические методы для получения данных.
Используется в классе DiaryCloud
 */
public class WeekRecord extends SugarRecord {
    @Unique long monday; // timestamp

    int hash;
    /*
    При добавлении недели сохраняется хеш, чтобы быстрее сверять неделю на факт изменения
    т.е.
    Week w;
    WeekRecord wr;
    w.hashCode() == wr.hash
     */

    @Ignore
    List<DayRecord> days = new ArrayList<>();

    public WeekRecord(){}

    public WeekRecord(long monday){
        this.monday = monday;
        hash = hashCode();
    }

    public void updateHash(){
        hash = hashCode();
    }
    public void addDay(DayRecord day){
        days.add(day);
    }

    public static Week getWeek(long monday){
        HashMap<String, Day> map = new HashMap<>();
        WeekRecord weekRecord = WeekRecord.getWeekRecord(monday);
        if(weekRecord == null){
            //если такой недели нет
            return null;
        }
        List<DayRecord> days = WeekRecord.getDaysRecords(weekRecord);
        //происходит сбор всех реляций и сохранение в формат Week
        for(DayRecord dayRecord: days){
            List<LessonRecord> lessonsRecords = dayRecord.getLessonsRecords(dayRecord);
            Lesson[] lessons = new Lesson[lessonsRecords.size()];
            int i = 0;
            for(LessonRecord lessonRecord: lessonsRecords){
                lessons[i++] =  new Lesson(lessonRecord.getSubject(), lessonRecord.getTime(), lessonRecord.getHomework(),lessonRecord.getCabinet(), lessonRecord.getMark());
            }
            map.put(dayRecord.getDate(), new Day(lessons, dayRecord.getDate(), dayRecord.getReal_date()));
        }
        return new Week(monday, map);
    }

    public static void addWeek(Week week){
        WeekRecord originalWeek = getWeekRecord(week.getMonday());
        if(originalWeek != null && originalWeek.hash == week.hashCode()) { // week has not any changes
            return;
        }else if(originalWeek != null){
            originalWeek.delete();
        }
        originalWeek = new WeekRecord(week.getMonday());
        originalWeek.save(); // creating new week

        Collection<Day> values = week.getDays().values();

        for(Day day: values){
            DayRecord originalDay = new DayRecord(day.getDate(), day.getRealDate(), originalWeek);
            originalDay.save();

            for(Lesson lesson: day.getLessons()){
                originalDay.lessons.add(
                        new LessonRecord(
                                lesson.getSubject(),
                                lesson.getTime(),
                                lesson.getCabinet(),
                                lesson.getHomework(),
                                lesson.getMark(),
                                originalDay
                        ));
            }
            SugarRecord.saveInTx(originalDay.lessons); // batch insert for more speed
            originalDay.save();
            originalWeek.addDay(originalDay);
        }
        originalWeek.hash = week.hashCode();
        originalWeek.save();
    }


    public static WeekRecord getWeekRecord(long monday){
        List<WeekRecord> list = SugarRecord.find(WeekRecord.class, "monday = ?", monday + "");
        if(list == null || list.size() == 0) return null;
        return list.get(0);
    }


    public static List<DayRecord> getDaysRecords(WeekRecord weekRecord){
        return SugarRecord.find(DayRecord.class, "week = ?", weekRecord.getId() + "");
    }

}
