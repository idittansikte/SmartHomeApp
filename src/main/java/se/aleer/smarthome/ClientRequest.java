package se.aleer.smarthome;

import android.content.Context;
import android.content.Intent;

public class ClientRequest {

    public static Intent getListIntent(Context context, MyResultReceiver receiver, String client){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "G");
        return i;
    }

    public static Intent saveTimerIntent(Context context, MyResultReceiver receiver, String client, String timer){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "T:" + timer);
        return i;
    }

    public static Intent removeTimerIntent(Context context, MyResultReceiver receiver, String client, String timer){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "Q:" + timer);
        return i;
    }

    public static Intent saveSwitchIntent(Context context, MyResultReceiver receiver, String client, String swch){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "A:" + swch);
        return i;
    }

    public static Intent removeSwitchIntent(Context context, MyResultReceiver receiver, String client, String swch){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "R:" + swch);
        return i;
    }

    public static Intent statusSwitchIntent(Context context, MyResultReceiver receiver, String client, String swch){
        Intent i = new Intent(context, ClientIntentService.class);
        i.putExtra(ClientIntentService.recTag, receiver);
        i.putExtra(ClientIntentService.recClient, client);
        i.putExtra(ClientIntentService.recRequest, "S:" + swch);
        return i;
    }
}
