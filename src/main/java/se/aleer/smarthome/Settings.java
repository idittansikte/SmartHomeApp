package se.aleer.smarthome;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    StorageSetting mStorageSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        mStorageSetting = new StorageSetting(this);
        // Enable up button
        assert getSupportActionBar() != null;
        // BUGG
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Get all views
        mHolder = new ViewHolder();
        mHolder.url = (EditText) findViewById(R.id.settings_serverURL_field);
        mHolder.port = (EditText) findViewById(R.id.settings_serverPort_field);
        mHolder.user = (EditText) findViewById(R.id.settings_serverUser_field);
        mHolder.password = (EditText) findViewById(R.id.settings_serverPwd_field);
        mHolder.test_connnection = (Button) findViewById(R.id.settings_test_connection);
        mHolder.progressBar = (ProgressBar) findViewById(R.id.settings_connection_progressbar);
        mHolder.success = (ImageView) findViewById(R.id.setting_connection_success);
        mHolder.failure = (ImageView) findViewById(R.id.setting_connection_failure);
        mHolder.vibration = (android.widget.Switch) findViewById(R.id.setting_other_touch_vib_switch);
        mHolder.failure.setVisibility(View.INVISIBLE);
        mHolder.success.setVisibility(View.INVISIBLE);
        mHolder.progressBar.setVisibility(View.INVISIBLE);

        // Set focus listeners for edittext to hide keyboard when focus lost
        mHolder.url.setOnFocusChangeListener(mFocusListener);
        mHolder.port.setOnFocusChangeListener(mFocusListener);
        mHolder.user.setOnFocusChangeListener(mFocusListener);
        mHolder.password.setOnFocusChangeListener(mFocusListener);

        // Fill views with saved values
        mHolder.url.setText(mStorageSetting.getString(StorageSetting.PREFS_SERVER_URL));
        mHolder.port.setText(mStorageSetting.getString(StorageSetting.PREFS_SERVER_PORT));
        mHolder.user.setText(mStorageSetting.getString(StorageSetting.PREFS_SERVER_USER));
        mHolder.password.setText(mStorageSetting.getString(StorageSetting.PREFS_SERVER_PASSWORD));
        mHolder.vibration.setChecked(mStorageSetting.getBoolean(StorageSetting.PREFS_VIBRATION));

        mHolder.vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.widget.Switch toggle = (android.widget.Switch) v;
                if (toggle.isChecked())
                {
                    Vibrate.vibrate(getApplicationContext());
                }
            }
        });

        // Set on click listeners for buttons
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
    protected void onPause(){
        super.onPause();
        // Save settings cuz we may not come back...
        if(!mHolder.url.getText().toString().isEmpty()) {
            mStorageSetting.save(StorageSetting.PREFS_SERVER_URL, mHolder.url.getText().toString());
        }
        if(!mHolder.port.getText().toString().isEmpty()) {
            mStorageSetting.save(StorageSetting.PREFS_SERVER_PORT, mHolder.port.getText().toString());
        }
        if (!mHolder.user.getText().toString().isEmpty()) {
            mStorageSetting.save(StorageSetting.PREFS_SERVER_USER, mHolder.user.getText().toString());
        }
        if (!mHolder.password.getText().toString().isEmpty()) {
            mStorageSetting.save(StorageSetting.PREFS_SERVER_PASSWORD, mHolder.password.getText().toString());
        }

        mStorageSetting.save(StorageSetting.PREFS_VIBRATION, mHolder.vibration.isChecked());

        // Hide keyboard
        View view = this.getCurrentFocus();
        hideKeyboard(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ViewHolder {
        EditText url;
        EditText port;
        EditText user;
        EditText password;
        Button test_connnection;
        ProgressBar progressBar;
        ImageView success;
        ImageView failure;
        android.widget.Switch vibration;
    }

    private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    public void hideKeyboard(View view) {
        if (view != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
