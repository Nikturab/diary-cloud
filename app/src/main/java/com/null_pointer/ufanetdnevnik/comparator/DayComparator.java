package com.null_pointer.diarycloud.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/*
    В ответе неделя приходит несортированной (нормальное поведение JSON)
    (Если вдруг что, т.к. сейчас все нормально)
 */
public class DayComparator implements Comparator<String> {

    boolean reverse = false;
    public DayComparator(){

    }
    public DayComparator(boolean reverse){
        this.reverse = reverse;
    }

    @Override
    public int compare(String o1, String o2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yy");
        long time1, time2;
        try {
            time1 = dateFormat.parse(o1).getTime();
            time2 = dateFormat.parse(o2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        if(time1==time2){
            return 0;
        }
        return reverse ? ( time1<time2 ? 1 : -1 ) : ( time1<time2 ? -1 : 1 );
    }

}
