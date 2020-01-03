package com.null_pointer.diarycloud.model.response;

import java.io.Serializable;

/**
 * Created by student2 on 19.05.17.
 */

public class Contact implements Serializable {
    String name, id;
    boolean teacher;
    public boolean isTeacher(){
        return teacher;
    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }

    public Contact(){}
    public Contact(String name, String id, boolean teacher){
        this.name = name;
        this.id = id;
        this.teacher = teacher;
    }

}