package com.null_pointer.diarycloud.view.statistics.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.null_pointer.diarycloud.dialog.LessonDescriptionDialog;
import com.null_pointer.diarycloud.model.response.GradesResponse;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */



public class MarkHolder extends RecyclerView.ViewHolder {

    private TextView mMark;

    private Context context;

    public MarkHolder(View view, Context context) {
        super(view);
        mMark = (TextView) view;
        this.context = context;
    }

    public void bindMark(final GradesResponse.MarkOfDay mark) {
        mMark.setText(mark.getMark());
        mMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LessonDescriptionDialog(context, "Оценка "+mark.getMark(), "Дата получения оценки: "+mark.getDate(), "OK").getAlert().show();
            }
        });
    }
}