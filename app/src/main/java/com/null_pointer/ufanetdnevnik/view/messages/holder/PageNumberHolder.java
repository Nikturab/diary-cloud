package com.null_pointer.diarycloud.view.messages.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.view.messages.adapter.PageNumbersAdapter;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

public class PageNumberHolder extends RecyclerView.ViewHolder {

    private TextView mNumber;
    private int number;
    private PageNumbersAdapter adapter;
    private Context context;
    private View rootView;
    public PageNumberHolder(View view, Context context, PageNumbersAdapter adapter) {
        super(view);
        mNumber = (TextView) view.findViewById(R.id.page_number);
        this.adapter = adapter;
        this.context = context;
        this.rootView = view;
    }

    public void bindNumber(int number, View.OnClickListener listener) {
        this.number = number;
        rootView.setOnClickListener(listener);
        mNumber.setText(number+"");
        if(number == adapter.getPage()){
            mNumber.setBackgroundResource(R.drawable.border_page_number);
            mNumber.setTextColor(Color.WHITE);
        }else{
            mNumber.setBackgroundColor(Color.TRANSPARENT);
            mNumber.setTextColor(context.getResources().getColor(R.color.messages__page_number));
        }
    }
}

