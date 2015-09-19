package se.aleer.smarthome;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPAsyncTask extends AsyncTask<String, Void, String> {

    String mHost;
    int mPort;

    TCPAsyncTask()
    {
        mHost = null;
        mPort = 0;
    }
    TCPAsyncTask(String host, int port)
    {
        mHost = host;
        mPort = port;
    }
    @Override
    protected String doInBackground(String... params){
        //mSwitchView.setVisibility();
        String result = null;
        String ipAddress;
        int port;
        if(mHost == null || mPort == 0) {
            ipAddress = "192.168.1.151";
            port = 8888;
        }
        else
        {
            ipAddress = mHost;
            port = mPort;
        }
        try{
            // Create client socket
            Socket socket = new Socket(ipAddress,port);
            socket.setSoTimeout(20000); // 20 Seconds timeout
            // Get input stream of the  client socket
            InputStream is = socket.getInputStream();
            // Get output stream of the client socket
            OutputStream os = socket.getOutputStream();
            // Write data to the output stream
            PrintStream printStream = new PrintStream(os);
            printStream.println(params[0]);
            // Buffer data coming from input stream
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            result = br.readLine();
            printStream.close();
            socket.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*@Override
    protected void onPostExecute(String s)
    {
        // TODO: Do something with the result
        mViewHolder.switchButton.setImageResource(R.drawable.button_on_128);
    }*/
}
