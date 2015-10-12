package se.aleer.smarthome;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class StorageSetting {

    /* DO NOT EDIT THESE */
    private final String PREFS_NAME = "SmartHome_Settings";
    public static final String PREFS_SERVER_URL = "server_url";
    public static final String PREFS_SERVER_PORT = "server_port";
    public static final String PREFS_SERVER_USER = "server_user";
    public static final String PREFS_SERVER_PASSWORD = "server_password";
    public static final String PREFS_VIBRATION = "setting_vibration";
    /* DO NOT EDIT THESE */

    private SharedPreferences mSharedPreferences = null;

    public StorageSetting(Context context)
    {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void save(String key, String string){

        if (string != null && !string.isEmpty()){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, string);
            editor.apply();
        }
    }

    public void save(String key, Integer value){
        if (value != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public void save(String key, Boolean value){
        if (value != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public String getString(String key){
       return mSharedPreferences.getString(key, null);
    }

    public int getInt(String key){
        return mSharedPreferences.getInt(key, 0);
    }

    public Boolean getBoolean(String key){
        return mSharedPreferences.getBoolean(key, false);
    }
}
