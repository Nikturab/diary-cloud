package com.null_pointer.diarycloud.view.diary_page.holder;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.null_pointer.diarycloud.AddMessageFragment;
import com.null_pointer.diarycloud.MainActivity;
import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.response.Contact;
import com.null_pointer.diarycloud.model.response.Day;
import com.null_pointer.diarycloud.model.response.Lesson;
import com.null_pointer.diarycloud.view.diary_page.adapter.LessonsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

/*
    Холдэр одного дня одной недели
 */
public class DayHolder extends RecyclerView.ViewHolder {

    private TextView mTitleDay;
    private RecyclerView mDayRecyclerView;

    private Context context;
    private int page;

    public DayHolder(View view, Context context, RecyclerView mDayRecyclerView, int page) {
        super(view);
        this.mDayRecyclerView = mDayRecyclerView;
        this.mTitleDay = (TextView) view.findViewById(R.id.titleDay);
        this.context = context;
        this.page =  page;
    }

    public void bindDay(Day day, final FragmentManager manager) {
        mTitleDay.setText(day.getRealDate());
        if(day.isHoliday()){
            // есоли выходоной, То красным выделяем
            mTitleDay.setTextColor(Color.parseColor("#e74c3c"));
        }
        Lesson[] lessonList = day.getLessons();
        mDayRecyclerView.setAdapter(new LessonsAdapter(new ArrayList<>(Arrays.asList(lessonList)), context,  new LessonHolder.OnClickListener() {
            @Override
            public void onClick(View view, Lesson lesson) {
                Fragment fragment = new AddMessageFragment();
                Bundle args = new Bundle();

                HashMap<Integer, Contact[]> contacts = new HashMap<>();
                if(lesson.getTeacherName()==null || lesson.getTeacherId() == null){
                    // Если из базы данных
                    return;
                }

                contacts.put(0, new Contact[]{new Contact(lesson.getTeacherName(), lesson.getTeacherId(), true)});
                args.putSerializable("users", contacts);
                fragment.setArguments(args);
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_main__content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        }) );
    }
}

