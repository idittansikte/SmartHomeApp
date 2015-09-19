package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class Settings extends AppCompatActivity {

    public ViewHolder mHolder;
    Context context;
    Storage mStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        // Enable up button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View setting_view = inflater.inflate(R.layout.activity_settings, null);
        mStorage = new Storage();

        mHolder = new ViewHolder();
        mHolder.url = (EditText) findViewById(R.id.settings_serverURL_field);
        mHolder.port = (EditText) findViewById(R.id.settings_serverPort_field);
        mHolder.user = (EditText) findViewById(R.id.settings_serverUser_field);
        mHolder.password = (EditText) findViewById(R.id.settings_serverPwd_field);
        mHolder.test_connnection = (Button) findViewById(R.id.settings_test_connection);
        mHolder.progressBar = (ProgressBar) findViewById(R.id.settings_connection_progressbar);
        mHolder.success = (ImageView) findViewById(R.id.setting_connection_success);
        mHolder.failure = (ImageView) findViewById(R.id.setting_connection_failure);
        mHolder.failure.setVisibility(View.INVISIBLE);
        mHolder.success.setVisibility(View.INVISIBLE);
        mHolder.progressBar.setVisibility(View.INVISIBLE);
        mStorage.getSettings(this, mHolder);
        mHolder.test_connnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int port;
                String host;
                try {
                    port = Integer.parseInt(mHolder.port.getText().toString());
                    host = mHolder.url.getText().toString();
                }catch (Exception e){
                    return;
                }
                mHolder.progressBar.setVisibility(View.VISIBLE);
                mHolder.success.setVisibility(View.INVISIBLE);
                mHolder.failure.setVisibility(View.INVISIBLE);
                mHolder.test_connnection.setEnabled(false);
                mHolder.test_connnection.setHovered(true);
                // TODO: Cancel option
                // TODO: check port range
                TCPAsyncTask serverStatus = new TCPAsyncTask(host, port){
                    @Override
                    protected void onPostExecute(String s) {

                        mHolder.progressBar.setVisibility(View.INVISIBLE);
                        mHolder.test_connnection.setEnabled(true);
                        mHolder.test_connnection.setHovered(false);
                        if (s == null || s.equals("")){
                            mHolder.failure.setVisibility(View.VISIBLE);
                            return;
                        }
                        else if (s.equals("OK")){
                            mHolder.success.setVisibility(View.VISIBLE);
                        }
                        else
                            mHolder.failure.setVisibility(View.VISIBLE);
                        Log.d("SsSSSssS", s);

                    }
                };
                serverStatus.execute("C");
            }
        });

        Log.d("asDASDE", "<------------");
        //ViewHolder g = storage.getSettings(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStorage.saveSettings(this, mHolder);
    }

    public class HttpAsyncTask extends AsyncTask<String, Integer, Boolean> {

        private String mUrl;
        private String mUser;
        private String mPwd;
        private int mPort;


        HttpAsyncTask(String url, int port, String user, String pwd) {
            mUrl = url;
            mPort = port;
            mUser = user;
            mPwd = pwd;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            return isServerAvailable();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mHolder.progressBar.setVisibility(View.INVISIBLE);
            mHolder.test_connnection.setEnabled(true);
            mHolder.test_connnection.setHovered(false);
            if(result) {
                mHolder.success.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.failure.setVisibility(View.VISIBLE);
            }

        }

        public boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isServerAvailable() {

            if ( ! isNetworkAvailable(getApplicationContext())) {
                mHolder.failure.setVisibility(View.VISIBLE);
                return false;
            }

            try {
                String pingCmd = "ping -c 5 " + mUrl;
                String pingResult = "";

                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    pingResult += inputLine;
                }

                in.close();
                Log.d("::::::", pingResult);
            }
            catch (Exception e){
                Log.e("URL ERROR", e.getMessage());
                return false;
            }
            return true;
        }
    }

    public class ViewHolder {
        EditText url;
        EditText port;
        EditText user;
        EditText password;
        Button test_connnection;
        ProgressBar progressBar;
        ImageView success;
        ImageView failure;

        ViewHolder(){}

        ViewHolder(EditText url, EditText port, EditText user, EditText password){
            this.url = url;
            this.port = port;
            this.user = user;
            this.password = password;
        }

        @Override
        public String toString() {
            return "[url=" + ((url == null) ? "" : url.getText().toString()) + "," +
                    " port=" + ((port == null) ? "" : port.getText().toString()) + "," +
                    " user=" + ((user == null) ? "" : user.getText().toString()) + "," +
                    " password=" + ((password == null) ? "" : password.getText().toString()) + "]";
        }
    }

}
