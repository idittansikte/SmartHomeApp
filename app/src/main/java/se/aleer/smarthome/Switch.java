package se.aleer.smarthome;


import android.content.res.Resources;
import android.util.Log;

import java.util.List;
import java.util.Objects;

public class Switch {

    private int mId;
    private int mController;
    private String mName;
    private int on;
    private int mProtocol;
    private boolean mSingle;
    private List<Integer> mMultiSwitchArray;

    public Switch(){
        super();
    }

    public Switch(int id, int controller, String name)
    {
        this.mId = id;
        this.mController = controller;
        this.mName = name;
        this.on = -1;
        this.mProtocol = 1;
    }

    public boolean isSingle() {return mSingle; }
    public void setSingle(boolean single) { mSingle = single; }

    public int getStatus()
    {
        return on;
    }
    public void setStatus(int state)
    {
        on = state;
    }

    public int getId(){ return mId; }
    public void setId(int id){ this.mId = id;}

    public int getController(){
        return mController;
    }

    public String getName(){ return mName; }
    public void setName(String name){ this.mName = name; }


    public void setController(int controller){ this.mController = controller; }



    public int getProtocol() { return mProtocol; }
    public void setProtocol( int protocol ) { mProtocol = protocol; }

    @Override
    public boolean equals(Object rhs)
    {
        Log.d("SWITCH", "Using equals");
        Log.d("Switch", "Comparing " + this.getId() + " with " + ((Switch) rhs).getId());
        if (rhs instanceof Switch && this.getId() == ((Switch) rhs).getId() ){
            Log.d("SWITCH", "Equal found");
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Switch [id=" + mId + ", controller=" + mController + ", name=" + mName + "]";
    }

}
