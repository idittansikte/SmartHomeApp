package se.aleer.smarthome;

import android.content.Context;
import android.os.Vibrator;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by alex on 2015-10-08.
 */
public class Vibrate {
    public static void vibrate(Context context){
        if(new StorageSetting(context).getBoolean(StorageSetting.PREFS_VIBRATION)){
            Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(100);
        }
    }
}
