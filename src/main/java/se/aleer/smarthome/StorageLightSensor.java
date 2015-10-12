package se.aleer.smarthome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageLightSensor {

    public static final String PREFS_NAME = "SMARTHOME_APP";
    public static final String PREFS_LIGHTSENSOR = "LIGHTSENSORS";

    public StorageLightSensor(){
        super();
    }

    public void saveList (Context context, List<LightSensor> timers){
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSwitches = gson.toJson(timers);

        editor.putString(PREFS_LIGHTSENSOR, jsonSwitches);
        editor.apply();
    }

    public ArrayList<LightSensor> getList(Context context){
        SharedPreferences settings;
        List<LightSensor> timerList;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(PREFS_LIGHTSENSOR)) {
            String jsonFavorites = settings.getString(PREFS_LIGHTSENSOR, null);
            Gson gson = new Gson();
            LightSensor[] timerItems = gson.fromJson(jsonFavorites,
                    LightSensor[].class);

            timerList = Arrays.asList(timerItems);
            timerList = new ArrayList<>(timerList);
        } else
            return null;

        return (ArrayList<LightSensor>) timerList;
    }
}
