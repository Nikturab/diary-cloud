package com.null_pointer.diarycloud.view.diary_page.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.Days;
import com.null_pointer.diarycloud.model.response.Day;
import com.null_pointer.diarycloud.view.diary_page.holder.DayHolder;

import java.util.List;

/*
    Адаптер дней одной недели
 */
public class DaysAdapter extends RecyclerView.Adapter<DayHolder> {
    private List<String> dates;
    private RecyclerView mDayRecyclerView;
    private Days days = new Days();

    private Context context;
    private Activity activity;
    private int page;
    private FragmentManager manager;

    public DaysAdapter(Days days, Context context, Activity activity, FragmentManager manager, int page) {
        this.dates = days.getDates();
        this.days = days;
        this.activity = activity;
        this.manager = manager;
        this.context = context;
        this.page = page;
    }

    @Override
    public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.list_item_days, parent, false);
        mDayRecyclerView = (RecyclerView) view.findViewById(R.id.list_work);
        mDayRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        return new DayHolder(view, context, mDayRecyclerView, page);
    }

    @Override
    public void onBindViewHolder(DayHolder holder, int position) {
        Day day = days.get(position);
        holder.bindDay(day, manager);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}
