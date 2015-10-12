package se.aleer.smarthome;

import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class RemoteControl extends AppCompatActivity implements TimerFragment.TimerFragmentListener, MyResultReceiver.Receiver, SwitchListFragment.SwitchFragmentListener,
                                                                LightSensorFragment.LightSensorFragmentListener {

    private static final String TAG = "RemoteControl";
    public final static String serviceTag = "RCServiceTag";
    private MyFragmentPagerAdapter mAdapterViewPager;
    private FragmentManager mFm;
    private ViewPager mViewPager;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    public MyResultReceiver mReceiver;

    private final Handler mHandler = new Handler();
    // If edit-switch mode selected in SwitchListFragment this is runs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageMargin(20);
        //viewPager.setPageMarginDrawable(R.color.black);
        mFm = getSupportFragmentManager();
        mAdapterViewPager = new MyFragmentPagerAdapter(mFm, RemoteControl.this);
        mViewPager.setAdapter(mAdapterViewPager);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        // Force full width
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);

        /** Create receiver to handle communication between this and ClientIntentService*/
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        /** We are now safe to start runnable */
        //mRunnable.run();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*if (contentFragment instanceof SettingFragment) {
            outState.putString("content", SettingFragment.ARG_ITEM_ID);
        } else {*/
        outState.putString("content", SwitchListFragment.ARG_ITEM_ID);
        //}
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRunnable.run();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
/*
    public void showUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
    public void hideUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(false); }
*/

    /*
 * We call super.onBackPressed(); when the stack entry count is > 0. if it
 * is instanceof ProductListFragment or if the stack entry count is == 0, then
 * we finish the activity.
 * In other words, from ProductListFragment on back press it quits the app.
 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Called when SwitchListFragment have updated id's or names in
     * its switch list.
     */
    public Map<Integer,String> onGetSwitchList(){
        SwitchListFragment sf = mAdapterViewPager.getSwitchFragment();
        if (sf != null){
            return sf.mapList();
        }
        return new HashMap<>();
    }


    public void saveLightSensor(String timer){

    }

    public void removeLightSensor(String timer){


    }

    public void onLightSensorListChange(TreeSet<Integer/*Switch ID*/> switchTree){

    }

    /**
     * Send a switch save/add request to the service
     *
     * @param swch The string representation of the purpose of the task. See server code for more info.
     */
    public void saveSwitch(String swch){
        startService(ClientRequest.saveSwitchIntent(getApplicationContext(), mReceiver, SwitchListFragment.TAG, swch));
    }

    /**
     * Send a switch remove request to the service
     *
     * @param swch The string representation of the purpose of the task. See server code for more info.
     */
    public void removeSwitch(String swch){
        startService(ClientRequest.removeSwitchIntent(getApplicationContext(), mReceiver, SwitchListFragment.TAG, swch));
    }

    /**
     * Send a switch status change request to the service
     *
     * @param swch The string representation of the purpose of the task. See server code for more info.
     */
    public void changeSwitchStatus(String swch){
        startService(ClientRequest.statusSwitchIntent(getApplicationContext(), mReceiver, SwitchListFragment.TAG, swch));
    }

    /**
     * Send a timer save/add request to the service
     *
     * @param timer The string representation of the purpose of the task. See server code for more info.
     */
    public void saveTimer(String timer){
        startService(ClientRequest.saveTimerIntent(getApplicationContext(), mReceiver, TimerFragment.TAG, timer));
    }

    /**
     * Send a timer remove request to the service
     *
     * @param timer The string representation of the purpose of the task. See server code for more info.
     */
    public void removeTimer(String timer){
        startService(ClientRequest.removeTimerIntent(getApplicationContext(), mReceiver, TimerFragment.TAG, timer));
    }

    public void onTimerListChange(TreeSet<Integer/*Switch ID*/> switchTree){
        SwitchListFragment sf = mAdapterViewPager.getSwitchFragment();
        if(sf != null){ // Shouldn't happen...
            sf.onTimerListChange(switchTree);
        }
    }

    /**
     * Receives frequent updates from the server.
     *
     * The information is passed to the fragments..
     */
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startService(ClientRequest.getListIntent(getApplicationContext(), mReceiver, TAG));
            mHandler.postDelayed(mRunnable, 10000);
        }
    };

    /**
     * Feeding information from the server to the fragments.
     * Fragments is only getting the information they need.
     *
     * @param message a raw data message on the servers structure
     */
    private void feedFragments(String message){
        if (message == null){
            Toast.makeText(getApplicationContext(), R.string.no_response_from_server, Toast.LENGTH_LONG).show();
            return;
        }
        if (message.isEmpty() || message.equals("-1")){
            return;
        }
        ServerResponseParser parser = new ServerResponseParser();
        TimerFragment tf = mAdapterViewPager.getTimerFragment();
        SwitchListFragment sf = mAdapterViewPager.getSwitchFragment();
        if(sf != null) {
            sf.updateListAdapter(parser.getSwitchStatus(message));
            if(tf != null) {
                tf.serverSync(parser.getTimers(message));
            }
        }

    }

    /**
     * Called when request handled by the ClientIntentService have done a given
     * task.
     *
     * ClientIntentService's task is to handle all communications with the server.
     *
     * @param resultCode 0 means that task was done successfully
     *
     * @param resultData contains the raw response message from the server and the client's
     * TAG that ordered the request (One of the fragments or this activity).
     * */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // TODO Auto-generated method stub
        String client = resultData.getString(ClientIntentService.recClient);
        if(client != null && !client.isEmpty() ){
            if (client.equals(TAG)){ // It's this activity's order
                //Toast.makeText(this, client + ": " + resultData.getString(ClientIntentService.recResponce), Toast.LENGTH_SHORT).show();
                Log.d(TAG, resultData.getString(ClientIntentService.recResponce));
                feedFragments(resultData.getString(ClientIntentService.recResponce));
            }else if (client.equals(SwitchListFragment.TAG)){
                Toast.makeText(this, client + ": " + resultData.getString(ClientIntentService.recResponce), Toast.LENGTH_SHORT).show();
                SwitchListFragment sf = mAdapterViewPager.getSwitchFragment();
                if(sf != null) {
                    if (resultCode == 0)
                        sf.onRequestFinished(true);
                    else
                        sf.onRequestFinished(false);
                }
            }else if (client.equals(TimerFragment.TAG)){
                Toast.makeText(this, client + ": " + resultData.getString(ClientIntentService.recResponce), Toast.LENGTH_SHORT).show();
                TimerFragment tf = mAdapterViewPager.getTimerFragment();
                if(tf != null) {

                }
            }
        }

    }

}