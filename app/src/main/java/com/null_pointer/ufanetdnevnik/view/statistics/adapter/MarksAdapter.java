package com.null_pointer.diarycloud.view.statistics.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.null_pointer.diarycloud.model.response.GradesResponse;
import com.null_pointer.diarycloud.view.statistics.holder.MarkHolder;

public class MarksAdapter extends RecyclerView.Adapter<MarkHolder> {
    private final Activity activity;
    private  GradesResponse.MarkOfDay[] marks;

    private Context context;

    public MarksAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void setMarks(GradesResponse.MarkOfDay[] marks){
        this.marks = marks;
    }

    @Override
    public MarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MarkHolder(view, context);
    }

    @Override
    public void onBindViewHolder(MarkHolder holder, int position) {
        GradesResponse.MarkOfDay mark = marks[position];
        holder.bindMark(mark);
    }

    @Override
    public int getItemCount() {
        return marks.length;
    }
}
