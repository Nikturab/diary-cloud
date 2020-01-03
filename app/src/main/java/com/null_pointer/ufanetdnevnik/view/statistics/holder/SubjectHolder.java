package com.null_pointer.diarycloud.view.statistics.holder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.null_pointer.diarycloud.R;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

public class SubjectHolder extends RecyclerView.ViewHolder {

    private RecyclerView mView;

    private TextView mSubjectTitle;

    private Context context;

    public SubjectHolder(View view, Context context, int spanCount) {
        super(view);
        this.context = context;
        mView = (RecyclerView) view.findViewById(R.id.work__card__marks_list);
        mView.setLayoutManager(new GridLayoutManager(context,7));
        mSubjectTitle = (TextView) view.findViewById(R.id.work__card__subject_title);
    }

    public void setTitle(String title){
        this.mSubjectTitle.setText(title);
    }

    public void setNewAdapter(RecyclerView.Adapter adapter){
       this.mView.setAdapter(adapter);
    }
}

