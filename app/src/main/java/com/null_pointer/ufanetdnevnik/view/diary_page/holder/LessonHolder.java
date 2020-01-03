package com.null_pointer.diarycloud.view.diary_page.holder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.null_pointer.diarycloud.AddMessageFragment;
import com.null_pointer.diarycloud.MainActivity;
import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.dialog.LessonDescriptionDialog;
import com.null_pointer.diarycloud.model.response.Contact;
import com.null_pointer.diarycloud.model.response.Lesson;

import java.util.HashMap;

/*
Холдэр одного урока одного дня
 */
public class LessonHolder extends RecyclerView.ViewHolder {
    private Lesson lesson;

    public void setLesson(Lesson lesson){
        this.lesson = lesson;
    }
    public TextView mTitle;
    public TextView mSubTitle;
    public TextView mMark;
    public TextView mWrite;

    public LessonHolder(View view, final Context context, final LessonHolder.OnClickListener writeClickListener) {
        super(view);
        mTitle = (TextView) view.findViewById(R.id.lesson_title);
        mSubTitle = (TextView) view.findViewById(R.id.lesson_subtitle);
        mMark = (TextView) view.findViewById(R.id.lesson_mark);
        mWrite = (TextView) view.findViewById(R.id.lesson_write);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LessonDescriptionDialog(context, lesson.getSubject(), lesson.getFullDescription(), "ОК").getAlert().show();
            }
        });
        // клик на "написать сообщение"
        mWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                writeClickListener.onClick(view, lesson);
            }
        });
    }

    public static class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }

        public void onClick(View view, Lesson lesson) {

        }

    }
}
