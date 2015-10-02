package se.aleer.smarthome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Timer {

    private int mId;
    private String mTitle;
    private int mTimeOnHour;
    private int mTimeOnMin;
    private int mTimeOffHour;
    private int mTimeOffMin;
    // Not implemented yet!
    private List<Integer> mSwitches; // SwitchID|SwitchName

    public Timer(int id){
        this(id, "");
    }

    public Timer(int id, String title){
        this(id,title,0,0,0,0);
    }

    public Timer(int id, String title, int onHour, int onMin, int offHour, int offMin){
        mId = id;
        mTitle = title;
        mTimeOnHour = onHour;
        mTimeOnMin = onMin;
        mTimeOffHour = offHour;
        mTimeOffMin = offMin;
        mSwitches = new ArrayList<>();
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public String getTitle(){
        return mTitle;
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

    public int getId(){
        return mId;
    }
    public void setId(int id){
        mId = id;
    }

    public void addSwitch(int sid){
        if(!mSwitches.contains(sid))
            mSwitches.add(sid);
    }

    public void removeSwitch(int sid){
        mSwitches.remove(sid);
    }

    public boolean haveSwitch(int id){
        if(mSwitches.contains(id)){
            return true;
        }else
            return false;
    }

    public List<Integer> getSwitchList(){
        return mSwitches;
    }

    public void clearSwitchList(){
        mSwitches.clear();
    }

    public String toString(){
        String switches = "";
        for( Integer swID : mSwitches){
            switches += swID + ":";
        }
        return mId + ":" + mTimeOnHour + ":" + mTimeOnMin + ":" + mTimeOffHour + ":" + mTimeOffMin + ":" + switches;
    }

    @Override
    public boolean equals(Object rhs){
        if(rhs instanceof Timer && this.getId() == ((Timer)rhs).getId()){
            return true;
        }
        return false;
    }
}
