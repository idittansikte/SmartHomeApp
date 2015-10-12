package se.aleer.smarthome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Timer extends ItemList {

    private int mTimeOnHour;
    private int mTimeOnMin;
    private int mTimeOffHour;
    private int mTimeOffMin;

    public Timer(int id){
        this(id, "");
    }

    public Timer(int id, String title){
        this(id,title,0,0,0,0);
    }

    public Timer(int id, String title, int onHour, int onMin, int offHour, int offMin){
        super(id, title);
        mTimeOnHour = onHour;
        mTimeOnMin = onMin;
        mTimeOffHour = offHour;
        mTimeOffMin = offMin;
    }

    public void setTimeOff(int hour, int minute){
        mTimeOffHour = hour; mTimeOffMin = minute;
    }

    public int getTimeOffMin(){
        return mTimeOffMin;
    }
    public int getTimeOffHour(){
        return mTimeOffHour;
    }

    public void setTimeOn(int hour, int minute){
        mTimeOnHour = hour; mTimeOnMin = minute;
    }

    public int getTimeOnMin(){
        return mTimeOnMin;
    }
    public int getTimeOnHour(){
        return mTimeOnHour;
    }

    public String toString(){
        return getId() + ":" + mTimeOnHour + ":" + mTimeOnMin + ":" + mTimeOffHour + ":" + mTimeOffMin + ":" + switchListToString();
    }

    @Override
    public boolean equals(Object rhs){
        if(rhs instanceof Timer && this.getId() == ((Timer)rhs).getId()){
            return true;
        }
        return false;
    }
}
