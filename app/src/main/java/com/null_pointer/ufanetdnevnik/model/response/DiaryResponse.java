package com.null_pointer.diarycloud.model.response;

import com.null_pointer.diarycloud.model.DiaryCloud;

import java.util.HashMap;

public class DiaryResponse extends CommonResponse {
    private DiaryPage page;

    public DiaryPage getPage(){
        return page;
    }

    public void fromDays(HashMap<String, Day> diary){
        this.page = new DiaryPage();
        this.page.fromDiary(diary);
    }

    public class DiaryPage {

        private HashMap<String, Day> diary;

        private String monday;

        public long getMonday(){
            return DiaryCloud.getUnFormattedDate(monday, DiaryCloud.DATE_FORMAT).getTime();
//            if(diary.size() == 0){
//                //Если неделя пустая, то не страшно, если понедельник не указан
//                return 0;
//            }
//            Day first = (Day) diary.values().toArray()[0];
//            return DiaryCloud.getMonday(DiaryCloud.getUnFormattedDate(first.getDate(), DiaryCloud.DATE_FORMAT)).getTime();
        }

        public Week getWeek(){
            return new Week(getMonday(), diary);
        }

        /**
         * Используется для формирования ответа из бд, к примеру
         * @param diary days of the week
         */
        public void fromDiary(HashMap<String, Day> diary){
            this.diary = diary;
        }

        public HashMap<String, Day> getDiary(){
            return diary;
        }
    }

}
