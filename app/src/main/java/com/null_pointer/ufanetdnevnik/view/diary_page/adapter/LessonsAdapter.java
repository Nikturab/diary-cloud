package com.null_pointer.diarycloud.view.diary_page.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.null_pointer.diarycloud.AddMessageFragment;
import com.null_pointer.diarycloud.MainActivity;
import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.response.Contact;
import com.null_pointer.diarycloud.model.response.Lesson;
import com.null_pointer.diarycloud.view.diary_page.holder.LessonHolder;

import java.util.HashMap;
import java.util.List;

/*
    Адаптер уроков одного дня
 */
public class LessonsAdapter extends RecyclerView.Adapter<LessonHolder> {

    private List<Lesson> mLessons;

    private Context context;


    private LessonHolder.OnClickListener listener;
    public LessonsAdapter(List<Lesson> lessons, Context context, LessonHolder.OnClickListener listener) {
        mLessons = lessons;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_work, parent, false);

        return new LessonHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(LessonHolder holder, int position) {
        Lesson lesson = mLessons.get(position);
        holder.setLesson(lesson);
        holder.mTitle.setText(++position + ". " + lesson.getSubject() );
        holder.mSubTitle.setText(lesson.getShortDescription());
        holder.mMark.setText( lesson.getMark());
    }

    @Override
    public int getItemCount() {
        return mLessons.size();
    }
}