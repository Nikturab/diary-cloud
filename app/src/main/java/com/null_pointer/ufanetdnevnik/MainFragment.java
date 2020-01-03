package com.null_pointer.diarycloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.null_pointer.diarycloud.model.DiaryCloud;
import java.util.Calendar;
import java.util.GregorianCalendar;


/*
    https://habrahabr.ru/post/258195/#article3
*/
public class MainFragment extends android.support.v4.app.Fragment {


    private ViewPager mViewPager;

    private MainActivity mActivity;

    private MainActivity getMainActivity(){
        return mActivity;
    }


    private GregorianCalendar getCalendar(){
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.set     (Calendar.HOUR_OF_DAY, 0);
        cal.clear   (Calendar.MINUTE);
        cal.clear   (Calendar.SECOND);
        cal.clear   (Calendar.MILLISECOND);
        cal.set     (Calendar.DAY_OF_WEEK, 2);
        return cal;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        mActivity = (MainActivity) getActivity();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(SectionsPagerAdapter.START_PAGE);
        mViewPager.setOffscreenPageLimit(1);
        return view;
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        static final int START_PAGE = 200;//200;
        int mSize = 500;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }

        /*

         */
        public String getWeek(int position){ // ���������� ���� ������� ������, � ����������� �� ������� ������� viewpager'�
            if(mSize - position <=5){
                // ���� ���� ������� � ����� viewpager'�
                mSize++;
               getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
                   @Override
                   public void runOperation() {
                       notifyDataSetChanged();
                   }
               });
            }
            GregorianCalendar cal = getCalendar();
            cal.add(Calendar.WEEK_OF_MONTH, position-START_PAGE);
            return DiaryCloud.getFormattedDate(cal);

        }
        @Override
        public  Fragment getItem(final int position) {
            // ���������� �������� � �������
            final String week = getWeek(position);
            getMainActivity().addActionBarTitle(getWeek(mViewPager.getCurrentItem()));
            return DiaryPageFragment.newInstance(week, position);
        }



        @Override
        public int getCount() {
           return mSize;
        }

    }

}
