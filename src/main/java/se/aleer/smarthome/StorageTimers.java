package se.aleer.smarthome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageTimers {

    public static final String PREFS_NAME = "SMARTHOME_APP";
    public static final String TIMERS = "TIMERS";

    public StorageTimers(){
        super();
    }

    public void saveTimerList (Context context, List<Timer> timers){
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSwitches = gson.toJson(timers);

        editor.putString(TIMERS, jsonSwitches);
        editor.apply();
    }

    public ArrayList<Timer> getTimerList(Context context){
        SharedPreferences settings;
        List<Timer> timerList;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(TIMERS)) {
            String jsonFavorites = settings.getString(TIMERS, null);
            Gson gson = new Gson();
            Timer[] timerItems = gson.fromJson(jsonFavorites,
                    Timer[].class);

            timerList = Arrays.asList(timerItems);
            timerList = new ArrayList<>(timerList);
        } else
            return null;

        return (ArrayList<Timer>) timerList;
    }
}
