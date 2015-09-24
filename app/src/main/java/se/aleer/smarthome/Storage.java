package se.aleer.smarthome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

public class Storage {

    public static final String PREFS_NAME = "SMARTHOME_APP";
    public static final String SWITCHES = "SWITCHES";

    public Storage(){
        super();
    }

    public void saveSwitchList (Context context, List<Switch> switches){
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSwitches = gson.toJson(switches);

        editor.putString(SWITCHES, jsonSwitches);
        editor.apply();
    }

    public ArrayList<Switch> getSwitchList(Context context){
        SharedPreferences settings;
        List<Switch> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(SWITCHES)) {
            String jsonFavorites = settings.getString(SWITCHES, null);
            Gson gson = new Gson();
            Switch[] favoriteItems = gson.fromJson(jsonFavorites,
                    Switch[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Switch>(favorites);
        } else
            return null;

        return (ArrayList<Switch>) favorites;
    }

    public void addSwitch(Context context, Switch swtch){
        List<Switch> switches = getSwitchList(context);
        if (switches == null){
            switches = new ArrayList<Switch>();
        }
        switches.add(swtch);
        saveSwitchList(context, switches);
    }

    public void removeSwitch(Context context, Switch swtch){
        ArrayList<Switch> switches = getSwitchList(context);
        if(switches != null){
            switches.remove(swtch);
            saveSwitchList(context, switches);
        }

    }
}
