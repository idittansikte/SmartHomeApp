package se.aleer.smarthome;

public class Timer {

    private int mId;
    private String mTitle;
    private int mTimeOnHour;
    private int mTimeOnMin;
    private int mTimeOffHour;
    private int mTimeOffMin;

    public Timer(){
        this("No name");
    }

    public Timer(String title){
        this(-1,title,0,0,0,0);
    }

    public Timer(int id, String title, int onHour, int onMin, int offHour, int offMin){
        mId = id;
        mTitle = title;
        mTimeOnHour = onHour;
        mTimeOnMin = onMin;
        mTimeOffHour = offHour;
        mTimeOffMin = offMin;
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

    @Override
    public boolean equals(Object rhs){
        if(rhs instanceof Timer && this.getId() == ((Timer)rhs).getId()){
            return true;
        }
        return false;
    }
}
