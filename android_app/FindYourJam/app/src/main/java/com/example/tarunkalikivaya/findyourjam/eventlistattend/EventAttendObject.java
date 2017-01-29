package com.example.tarunkalikivaya.findyourjam.eventlistattend;

/**
 * Created by chrisexn on 1/28/2017.
 */

public class EventAttendObject {
    private String title;
    private String description;
    private String id;

    public EventAttendObject(String title, String description, String id){
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getId(){
        return id;
    }
}
