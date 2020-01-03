package com.null_pointer.diarycloud.view.statistics.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.response.GradesResponse;
import com.null_pointer.diarycloud.view.statistics.holder.SubjectHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectHolder> {

    private HashMap<String, GradesResponse.MarkOfDay[]> marksByDay;
    private ArrayList<String> subjects;

    private Context context;

    private final Activity activity;

    private int spanCount;

    public SubjectsAdapter(Context context, Activity activity, int spanCount) {
        marksByDay = new HashMap<>();
        subjects = new ArrayList<>();
        this.context = context;
        this.activity = activity;
        this.spanCount = spanCount;
    }

    public void setMarks(HashMap<String, GradesResponse.MarkOfDay[]> marks){
        marksByDay = marks;
        subjects.addAll(marks.keySet());
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.word__card_subject, parent, false);
        return new SubjectHolder(view, context, spanCount);
    }

    @Override
    public void onBindViewHolder(SubjectHolder holder, int position) {
        GradesResponse.MarkOfDay[] marks = marksByDay.get(subjects.get(position));
        MarksAdapter adapter = new MarksAdapter(context, activity);
        adapter.setMarks(marks);
        holder.setNewAdapter(adapter);
        holder.setTitle(subjects.get(position));

    }

    @Override
    public int getItemCount() {
        return marksByDay.size();
    }
}