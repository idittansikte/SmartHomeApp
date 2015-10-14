package se.aleer.smarthome;

import android.content.Context;
import android.content.Intent;

public class ServerRequestMaker {

    /*public static Intent getListIntent(Context context, MyResultReceiver receiver, String client){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "G");
        return i;
    }*/

    /*public static Intent statusSwitchIntent(Context context, MyResultReceiver receiver, String client, String swch){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "S:" + swch);
        return i;
    }*/


    public static String makeGetListRequest(){
        return "G";
    }

    public static String makeSwitchStatusRequest(Switch sw) {
        return "S:" + sw.getId() + ":" + (sw.getStatus() == 1 ? "0" : "1");
    }

    public static String makeSaveSwitchRequest(Switch sw){
        return "A:" + sw.getId();
    }

    public static String makeRemoveSwitchRequest(Switch sw){
        return "R:" + sw.getId();
    }

    public static String makeSaveTimerRequest(Timer t){
        return "T:" + t.toString();
    }

    public static String makeRemoveTimerRequest(Timer t){
        return "Q:" + t.getId();
    }
}
