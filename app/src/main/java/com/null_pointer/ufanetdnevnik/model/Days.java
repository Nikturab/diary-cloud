package com.null_pointer.diarycloud.model;

import com.null_pointer.diarycloud.model.response.Day;

import java.util.ArrayList;

/*
    Удобное представление группы дней
    TODO заменить использование Days на использование Week
 */
public class Days {
    private ArrayList<Day> days = new ArrayList<>();

    public void add(Day day){
        days.add(day);
    }


    public ArrayList<String> getDates(){
        ArrayList<String> dates = new ArrayList<>();
        for (Day day : days) {
            dates.add(day.getDate());
        }
        return dates;
    }

    public Day get(int index){
        return days.get(index);
    }

    public Day get(String key){
        for(Day day: days){
            if(day.getDate().equals(key)){
                return day;
            }
        }
        return null;
    }

    public void clear(){
        days.clear();
    }
    public int size(){
        return days.size();
    }
}
