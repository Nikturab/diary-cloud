package com.null_pointer.diarycloud;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.null_pointer.diarycloud.exception.GradesException;
import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.response.GradesResponse;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.view.statistics.adapter.SubjectsAdapter;

import java.io.IOException;
import java.util.HashMap;

// успеваемость
public class StatisticsFragment extends Fragment {

    private EditText mFromDate; // от
    private EditText mToDate; // и до
    private Button mShowButton; // успеваемость
    private Button mShowTotalsButton; // итоги
    private RecyclerView mTable; // список предметов с оценками
    private View mStatisticsLoader; // прогрессбар для статистики
    private WebView mHtmlView; // для вывода итоговых оценок

    private DiaryCloud client;
    private SubjectsAdapter subjectsAdapter;

    private View rootView;

    private static final String DATE_DIALOG = "DialogDate";

    private MainActivity getMainActivity(){
        return (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        client = getMainActivity().getClient();
        /*
            Получили инстанс объекта-аккаунта.
         */


        this.prepareViews(view);
        this.prepareAdapters();
        this.prepareListeners();

        rootView = view;

        return view;
    }

    private void prepareListeners() {
        // показ диалога выбора даты
        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.etTo = mToDate;
                dialog.show(manager, DATE_DIALOG);
            }
        });
        // тут тоже
        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.etTo = mFromDate;
                dialog.show(manager, DATE_DIALOG);

            }
        });

        // показ итогов в webview
        mShowTotalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHtmlView.setVisibility(View.VISIBLE);
                mTable.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    /*
                      Грузим итоговую статистику
                     */
                    public void run() {
                        getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
                            @Override
                            public void runOperation() {
                                mStatisticsLoader.setVisibility(View.VISIBLE);
                            }
                        });
                        try {
                            final String html = client.getTotals().getPage().getHtmlTable();
                            getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
                                @Override
                                public void runOperation() {
                                    // ответ - это html таблица. Такое решение оправдано тем, что иначе как таблицей, такое красиво не сделать. А tablelayout кушает память.
                                    mHtmlView.loadDataWithBaseURL("file:///android_asset/", "<html><head></head><body>"+html+"</body></html>", "text/html", "UTF-8", "");
                                }
                            });
                        } catch (IOException | GradesException e) {
                            getMainActivity().showSnackbar(getString(R.string.error_try_later), Snackbar.LENGTH_SHORT);
                        }catch (DiaryCloudLoginRequiredException e){
                            getMainActivity().showSnackbar(getString(R.string.error_try_relogin), Snackbar.LENGTH_SHORT);
                        }catch (InternetConnectionRequiredException e){
                            getMainActivity().showConnectionWarning(null);
                        }catch (Exception ignored){}
                        getMainActivity().sendMessage(new MainActivity.MainActivityMessage() {
                            @Override
                            public void runOperation() {
                                mStatisticsLoader.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
            }
        });

        // статистика
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHtmlView.setVisibility(View.GONE);
                mTable.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    /*
                     * Грузим статистику по времени
                     */
                    public void run() {
                        String begin = mFromDate.getText().toString();
                        String end = mToDate.getText().toString();
                        if (begin.isEmpty() || end.isEmpty()) {
                            getMainActivity().showSnackbar("Укажите временной отрезок", Snackbar.LENGTH_SHORT);
                            return;
                        }
                        client.setConnectionStatus(getMainActivity().checkInternet());
                        getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
                            @Override
                            public void runOperation() {
                                mStatisticsLoader.setVisibility(View.VISIBLE);
                            }
                        });
                        try {
                            final HashMap<String, GradesResponse.MarkOfDay[]> obj = client.getGrades(begin, end).getMarks() ;

                            getMainActivity().sendMessage(new MainActivity.MainActivityMessage() {
                                @Override
                                public void runOperation() {
                                    mStatisticsLoader.setVisibility(View.GONE);
                                    subjectsAdapter.setMarks(obj);
                                    subjectsAdapter.notifyDataSetChanged();
                                    if(obj!=null && obj.size()==0){
                                        getMainActivity().showSnackbar("За данный период оценок не найдено", Snackbar.LENGTH_LONG);
                                    }
                                }
                            });
                        } catch (IOException | GradesException e) {
                            getMainActivity().showSnackbar(getString(R.string.error_try_later), Snackbar.LENGTH_SHORT);
                        }catch (DiaryCloudLoginRequiredException e){
                            getMainActivity().showSnackbar(getString(R.string.error_try_relogin), Snackbar.LENGTH_SHORT);
                        }catch (InternetConnectionRequiredException e){
                            getMainActivity().showConnectionWarning(null);
                        }catch (Exception ignored){}
                    }
                }).start();
            }
        });
    }

    private void prepareViews(View view){
        mTable = (RecyclerView) view.findViewById(R.id.fragment_statistics__marks_table);
        if(getMainActivity().isTablet()){
            mTable.setLayoutManager(new GridLayoutManager(getContext(),2));
        }else{
            mTable.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        mToDate = (EditText) view.findViewById(R.id.fragment_statistics__et_to_date);
        mFromDate = (EditText) view.findViewById(R.id.fragment_statistics__et_from_date);
        mShowButton = (Button) view.findViewById(R.id.fragment_statistics__button_show_statistics);
        mShowTotalsButton = (Button) view.findViewById(R.id.fragment_statistics__button_show_totals);
        mStatisticsLoader = view.findViewById(R.id.statisticsLoader);
        mHtmlView = (WebView) view.findViewById(R.id.fragment_statistics__html_table);
    }

    private void prepareAdapters(){

        subjectsAdapter = new SubjectsAdapter(getContext(), getMainActivity(), 10);
        mTable.setAdapter(subjectsAdapter);
    }
}