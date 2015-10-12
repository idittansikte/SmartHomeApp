package se.aleer.smarthome;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by alex on 2015-10-07.
 */
public class ClientIntentService extends IntentService {

    public final static String recTag = "RecIntServTag";
    public final static String recRequest = "RecIntServReq";
    public final static String recResponce = "RecIntServRes";
    public final static String recResponseCode = "RecIntServResCode";
    public final static String recClient = "RecClient";

    public ClientIntentService(){
        super("ClientService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        ResultReceiver rec = intent.getParcelableExtra(recTag);
        if(rec == null){
            Log.d("CIS", "resultReceiver is null!!");
            return;
        }
        Bundle bundle = new Bundle();
        synchronized (this){
            try{
                String request = intent.getStringExtra(recRequest);
                String client = intent.getStringExtra(recClient);
                bundle.putString(recClient, client);
                StorageSetting ss = new StorageSetting(getApplicationContext());
                String port_string = ss.getString(StorageSetting.PREFS_SERVER_PORT);
                String url = ss.getString(StorageSetting.PREFS_SERVER_URL);

                String result;
                if(url == null || url.isEmpty()) {
                    throw new IOException(getString(R.string.error_no_url_config));
                }
                if(port_string == null || port_string.isEmpty()) {
                    throw new IOException(getString(R.string.error_no_port_config));
                }
                int port = Integer.parseInt(port_string);
                try{
                    // Create client socket
                    Socket socket = new Socket(url,port);
                    socket.setSoTimeout(5000); // 5 Seconds timeout
                    // Get input stream of the  client socket
                    InputStream is = socket.getInputStream();
                    // Get output stream of the client socket
                    OutputStream os = socket.getOutputStream();
                    // Write data to the output stream
                    PrintStream printStream = new PrintStream(os);
                    printStream.println(request);
                    // Buffer data coming from input stream
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    result = br.readLine();
                    printStream.close();
                    socket.close();
                } catch (SocketTimeoutException s) {
                    throw new Exception(getString(R.string.no_response_from_server));
                } catch (Exception e) {
                    throw new Exception(getString(R.string.error_connection));
                }

                bundle.putString(recResponce, result);
                rec.send(0, bundle);
            } catch (Exception e){
                bundle.putString(recResponce, e.getMessage());
                rec.send(1, bundle);
            }

        }
    }
}
