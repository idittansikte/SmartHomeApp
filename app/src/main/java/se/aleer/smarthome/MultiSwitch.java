package se.aleer.smarthome;

import java.util.List;

/**
 * Created by alex on 2015-09-26.
 */
public class MultiSwitch extends Switch {

    private List<Integer> mMultiSwitchArray;

    MultiSwitch(int id, String name, List<Integer> switches){
        super(id, name);
        mMultiSwitchArray = switches;
    }

    List<Integer> getSwitches(){
        return mMultiSwitchArray;
    }
}
