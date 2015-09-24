package se.aleer.smarthome;


public class Timer {

    private String mTitle;
    private String mTimeOn;
    private String mTimeOff;

    public Timer(String title){
        mTitle = title;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTimeOff(String time){
        mTimeOff = time;
    }

    public String getTimeOff(){
        return mTimeOff;
    }

    public void setTimeOn(String time){
        mTimeOn = time;
    }

    public String getTimeOn(){
        return mTimeOn;
    }
}
