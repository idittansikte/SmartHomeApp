package se.aleer.smarthome;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemList extends Item {


    private List<Integer> mSwitches; // SwitchID|SwitchName

    public ItemList(int id){
        this(id, "");
    }

    public ItemList(int id, String name){
        super(id, name);
        mSwitches = new ArrayList<>();
    }

    public void addSwitch(int sid){
        if(!mSwitches.contains(sid))
            mSwitches.add(sid);
    }

    public void removeSwitch(int sid){
        mSwitches.remove(sid);
    }

    public boolean haveSwitch(int id) {
        return mSwitches.contains(id);
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
        return super.toString() + switches;
    }

    public String switchListToString(){
        String switches = "";
        for( Integer swID : mSwitches){
            switches += swID + ":";
        }
        return switches;
    }
}
