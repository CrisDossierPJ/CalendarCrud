package com.example.testcalendrier;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.net.URI;

public class Event {
    private long Event_ID;
    private long calendar_ID;
    private String Title;
    private String location;
    private long dateBegin;
    private long dateEnd;
    private Context context;

    public Event(long Event_id, long calendar_ID, String Title, String location, long dateBegin, long dateEnd){
        this.dateBegin = dateBegin;
        this.calendar_ID = calendar_ID;
        this.Event_ID = Event_id;
        this.Title = Title;
        this.location = location;
        this.dateEnd = dateEnd;
    }
    public Event(String Title, String location, long dateBegin, long dateEnd){
        this.dateBegin = dateBegin;
        this.Title = Title;
        this.location = location;
        this.dateEnd = dateEnd;
    }

    public long getEvent_ID() {
        return Event_ID;
    }

    public void setEvent_ID(long event_ID) {
        Event_ID = event_ID;
    }

    public long getCalendar_ID() {
        return calendar_ID;
    }

    public void setCalendar_ID(long calendar_ID) {
        this.calendar_ID = calendar_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(long dateBegin) {
        this.dateBegin = dateBegin;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

}
