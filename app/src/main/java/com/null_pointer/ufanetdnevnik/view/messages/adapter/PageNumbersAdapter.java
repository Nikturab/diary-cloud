package com.null_pointer.diarycloud.view.messages.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.view.messages.holder.MessageHolder;
import com.null_pointer.diarycloud.view.messages.holder.PageNumberHolder;

/*
 Адаптер пагинации в разделе сообщений
 */
public class PageNumbersAdapter extends RecyclerView.Adapter<PageNumberHolder> {
    private int page = 1;
    /*
        На будущее
     */
    private boolean hasNext;
    private int lastPage;

    private PageNumbersAdapter.Clickable listener;

    private Context context;

    public void setPage(int p){
        this.page = p;
    }
    private int[] numbers;

    public int getPage() {
        return this.page;
    }

    public PageNumbersAdapter(int[] numbers, Context context, PageNumbersAdapter.Clickable listener) {
        this.numbers = numbers;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(PageNumberHolder holder, final int position) {
        final int page = numbers[position];
        holder.bindNumber(page, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view,page );
            }
        });
    }

    @Override
    public PageNumberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_page_number, parent, false);
        return new PageNumberHolder(view, context, PageNumbersAdapter.this);
    }

    @Override
    public int getItemCount() {
        return numbers.length;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
        this.numbers = new int[lastPage];
        for(int i=0;i<lastPage;i++){
            numbers[i] = i+1;
        }
    }
    public interface Clickable {
        void onClick(View view, int pageNo);
    }
}
