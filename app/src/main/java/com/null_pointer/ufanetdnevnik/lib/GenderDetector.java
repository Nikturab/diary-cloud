package com.null_pointer.diarycloud.lib;

public class GenderDetector {

    /**
     * @return gender boolean true if man, false if woman
     */
    public static boolean getGenderByPatronymic(String name){
        String last = name.substring(name.length()-2, name.length());
        return last.equals("ич");
    }

}
