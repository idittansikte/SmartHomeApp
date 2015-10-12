package se.aleer.smarthome;


import android.content.res.Resources;
import android.util.Log;

import java.util.List;
import java.util.Objects;

public class Switch extends Item {

    private int mStatus;
    private int mProtocol;
    private boolean mSingle;
    private int mTimerId;
    public transient boolean waitingUpdate;
    public transient boolean hasTimer;
    private List<Integer> mMultiSwitchArray;

    public Switch(){ // This is called when loaded from memory for some reason?
        this(-1, -1, "No name");
    }

    public Switch(int id, int controller, String name)
    {
        super(id, name);
        this.mStatus = -1;
        this.mProtocol = 1;
        this.mSingle = true;
        this.mTimerId=-1;
        this.waitingUpdate = false;
        this.hasTimer = false;
    }

    public Switch(int id, String name)
    {
        this(id, -1, name);
    }

    public boolean isSingle() {return mSingle; }
    public void setSingle(boolean single) { mSingle = single; }

    public int getTimerId()
    {
        return mTimerId;
    }
    public void setTimerId(int timerId)
    {
        mTimerId = timerId;
    }

    public int getStatus()
    {
        return mStatus;
    }
    public void setStatus(int state)
    {
        mStatus = state;
    }

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
        return super.toString() + mStatus + ":" ;
    }

}
