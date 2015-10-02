package se.aleer.smarthome;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerResponseParser {

    private static String TAG = "ServerResponseParser";

    private class ItemHolder {
        int swId;
        int status;
        int timerId ;
        int onHour;
        int onMinute;
        int offHour;
        int offMinute;
    }

    private List<ItemHolder> parseMessage(String m){

        try{
            List<ItemHolder> pList = new ArrayList<>();
            String[] server_list = m.split("N");
            for (String string : server_list ) {
                String[] server_item = string.split(":");
                if (server_item.length != 7) {
                    throw new Exception("Arguments from server not matching expectation");
                }
                ItemHolder item = new ItemHolder();
                item.swId = Integer.valueOf(server_item[0]);
                item.status = Integer.valueOf(server_item[1]);
                item.timerId = Integer.valueOf(server_item[2]);
                item.onHour = Integer.valueOf(server_item[3]);
                item.onMinute = Integer.valueOf(server_item[4]);
                item.offHour = Integer.valueOf(server_item[5]);
                item.offMinute = Integer.valueOf(server_item[6]);
                pList.add(item);
            }
            return pList;
        }catch (Exception e){
            Log.e(TAG, "Could not parse server message" + e.getMessage());
            return new ArrayList<>();
        }

    }

    public Map<Integer, Integer> getSwitchStatus(String m) {

        try {
            Map<Integer, Integer> map = new HashMap<>();
            List<ItemHolder> serverItems = parseMessage(m);
            for (ItemHolder item : serverItems )
            {
                if (item.swId < 10 || item.swId > 255) {
                    throw new IOException("WARNING: Corrupted data at server, switch id unvalid!");
                }
                map.put(item.swId, item.status);
            }
            return map;
        } catch (IOException e) {
            Log.e(TAG, "Could not get make switch status map:" + e.getMessage());
            return new HashMap<>();
        }
    }

    public List<Timer> getTimers(String m) {

        try {
            Map<Integer,Timer> map = new HashMap<>();
            List<ItemHolder> serverItems = parseMessage(m);
            for (ItemHolder item : serverItems )
            {
                if (item.timerId == 255) // Switch has no timer...
                    continue;
                if (item.swId < 10 || item.swId > 255 || item.timerId < 0 || item.timerId > 255 || item.onHour < 0 || item.onHour > 24 || item.onMinute < 0 || item.onMinute > 60
                        || item.offHour < 0 || item.offHour > 24 || item.offMinute < 0 || item.offMinute > 60) {
                    throw new IOException("WARNING: Corrupted data at server, some timer attribute is invalid!");
                }

                if(map.containsKey(item.timerId)){ // If timer already added, add the switch
                    map.get(item.timerId).addSwitch(item.swId); // We let the TimerFragment update the name when receiving this information
                }
                else{ // If not added, create a new timer...
                    Timer nt = new Timer(item.timerId, "Synced", item.onHour, item.onMinute, item.offHour, item.offMinute);
                    nt.addSwitch(item.swId);
                    map.put(item.timerId, nt);
                }
            }
            return new ArrayList<>(map.values());
        } catch (IOException e) {
            Log.e(TAG, "Could not get make switch status map:" + e.getMessage());
            return new ArrayList<>();
        }
    }
}


/*

|| timerId < 0 || timerId > 255 || onHour < 0 || onHour > 24 || onMinute < 0 || onMinute > 60
                        || offHour < 0 || offHour > 24 || offMinute < 0 || offMinute > 60)
 */