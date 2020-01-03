package com.null_pointer.diarycloud;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.null_pointer.diarycloud.comparator.DayComparator;
import com.null_pointer.diarycloud.exception.DiaryException;
import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.response.Day;
import com.null_pointer.diarycloud.model.Days;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.model.response.DiaryResponse;
import com.null_pointer.diarycloud.view.diary_page.adapter.DaysAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// представляет одну страницу дневника(одну неделю)
public class DiaryPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mWeekRecyclerView;// список дней в неделе
    private ProgressBar mDiaryLoader; // загрузка недели
    private SwipeRefreshLayout mRefreshLayout; //родительский layout, позволяющий перезагружать неделю свайпом сверху
    private DiaryCloud client;
    private String currentDate;

    private MainActivity mActivity;

    private int page;
    private MainActivity getMainActivity(){
        return mActivity;
    }

    public DiaryPageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DiaryPageFragment newInstance(String week, int page ) {
        DiaryPageFragment fragment = new DiaryPageFragment();
        Bundle args = new Bundle();
        args.putString("week", week);
        // день недели, которой должен соответствовать фрагмент
        fragment.setArguments(args);
        fragment.page = page;
        return fragment;
    }

    // отрисовка фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);
        mActivity = (MainActivity) getActivity();
        client = getMainActivity().getClient();
        mWeekRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_day);
        if(getMainActivity().isTablet()){
            mWeekRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        //для планшетов
        }else{
            //для телефонов
            mWeekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        //прогресбар недели
        mDiaryLoader = (ProgressBar) rootView.findViewById(R.id.diaryLoader);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        String week = (String) getArguments().get("week");
        // текущий день
        runUpdateTask(week);

        return rootView;
    }

    private void setNewAdapter(Days days){
        DaysAdapter mDaysAdapter = new DaysAdapter(days, getContext(), getMainActivity(), getParentFragment().getFragmentManager(), page);

        mWeekRecyclerView.setAdapter(mDaysAdapter);
    }

    /**
     * Запускает  getDiaryPage в новом потоке и устанавливает новый адаптер
     * @param date день, содержащийся в недели, которую надо получить
     */
    private void runUpdateTask(String date){
        DiaryLoadTask task = new DiaryLoadTask();
        task.execute(date);
    }

    // при обновлении свайпом сверху
    @Override
    public void onRefresh() {
        runUpdateTask(currentDate);
        mRefreshLayout.setRefreshing(false);
    }

// загружает неделю
    private class DiaryLoadTask extends AsyncTask<String, Void, Void> {

        private Exception e;
        private Days days;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            if(!isAdded()) return null; // на случай, если юзер уже перелистнул страницу
            if(params.length == 0) return null;
            String date = params[0];
            currentDate = date;
            client.setConnectionStatus(getMainActivity().checkInternet());
            try{
                days = getDiaryPage(date);
            }catch (Exception ignored){}
            return null;
        }

        /**
         * Получает days, чтобы впоследствии добавить их в адаптер
         * @param date день, содержащийся в недели, которую надо получить
         */
        private Days getDiaryPage(String date) {

            HashMap<String, Day> diary;
            Days days = new Days();
            try {
                DiaryResponse.DiaryPage page = client.getDiary(date).getPage();
                diary =  page.getDiary() ;
                ArrayList<String> set = new ArrayList<>(diary.keySet());
                Collections.sort(set, new DayComparator());

                for (String key : set) {
                    Day day = diary.get(key);
                    days.add(day);
                }
            }catch(Exception e){
                this.e = e;
            }
            return days;
        }

    // покзывает сообщение вместо недели(если интернета нет, например)
        private void updateDiaryPageMessage(final String message, final boolean error) {
            getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
                @Override
                public void runOperation() {
                    try {
                        TextView tw = (TextView) getView().findViewById(R.id.fragment_main__fragment_diary_page__no_days);
                        if (error) {
                            tw.setVisibility(View.VISIBLE);
                            tw.setText(message);
                        }else{
                            tw.setVisibility(View.GONE);
                        }
                    }catch(Exception e){ e.printStackTrace(); }
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            if(!isAdded()) return;
            super.onPostExecute(result);
            setNewAdapter(days);
            mDiaryLoader.setVisibility(View.GONE);
            // Обрабатываем ошибку
            try {
                if(e != null) throw e;
            } catch (IOException e) {
                e.printStackTrace();
                getMainActivity().showConnectionWarning(null);
            } catch (DiaryException e) {
                e.printStackTrace();
                e.getMessage();
                getMainActivity().showConnectionWarning(getString(R.string.error_server));
            }catch (DiaryCloudLoginRequiredException e) {
                if(client != null)
                    client.signOut(getContext());
                getMainActivity().showLoginRequiredWarning(null);
            }catch (InternetConnectionRequiredException e){
                updateDiaryPageMessage(getString(R.string.error_internet_required_for_page), true);
                getMainActivity().showConnectionWarning(null);
            } catch(Exception e1){
                if(client != null)
                    client.signOut(getContext());
                e1.printStackTrace();
                getMainActivity().showLoginRequiredWarning(null);
            }
            updateDiaryPageMessage(getString(R.string.no_days), days.size()==0);
        }

    }




}
