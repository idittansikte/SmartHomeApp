package se.aleer.smarthome;

import android.app.ActionBar;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteControl extends AppCompatActivity implements TimerFragment.OnGetSwitchListListener{

    private static final String TAG = "RemoteControl";

    private Fragment contentFragment;
    SwitchListFragment switchListFragment;
    SettingFragment settingFragment;
    SwitchManagerPopup mAddSwitchPopup;
    MyFragmentPagerAdapter mAdapterViewPager;
    FragmentManager mFm;
    ViewPager mViewPager;
    private final Handler mHandler = new Handler();
    // If edit-switch mode selected in SwitchListFragment this is runs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
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
        mRunnable.run();
        /** Adding tabs src: */
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
                Log.d(TAG, "Stack count: " + getFragmentManager().getBackStackEntryCount());
                getFragmentManager().popBackStack();
                Log.d(TAG, "Stack count: " + getFragmentManager().getBackStackEntryCount());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
    public void hideUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(false); }


    /*
 * We call super.onBackPressed(); when the stack entry count is > 0. if it
 * is instanceof ProductListFragment or if the stack entry count is == 0, then
 * we finish the activity.
 * In other words, from ProductListFragment on back press it quits the app.
 */
    @Override
    public void onBackPressed() {
        /*SwitchManagerFragment smf = (SwitchManagerFragment)getFragmentManager().findFragmentByTag(SwitchManagerFragment.ARG_ITEM_ID);
        if(smf != null && smf.isVisible()) {
            SwitchListFragment fragment = new SwitchListFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, SwitchListFragment.ARG_ITEM_ID);
            transaction.commit();
        }
        else {*/
            super.onBackPressed();
        //}

    }

    /*
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

    /*
     * Receives frequent updates from the server.
     *
     * The information is passed to the fragments..
     */
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Get and set status for all switches...
            StorageSetting ss = new StorageSetting(getApplicationContext());
            String port = ss.getString(StorageSetting.PREFS_SERVER_PORT);
            String url = ss.getString(StorageSetting.PREFS_SERVER_URL);
            if (port != null && !port.isEmpty() && url != null && !url.isEmpty() ) {
                mHandler.removeCallbacks(mRunnable);
                TCPAsyncTask getStatusArduino = new TCPAsyncTask(url, Integer.parseInt(port)) {
                    // TODO: Disable this when waiting for remove/add/switch...
                    @Override
                    protected void onPostExecute(String s) {
                        feedFragments(s);
                        mHandler.postDelayed(mRunnable, 10000);
                    }
                };
                getStatusArduino.execute("G");
            }
        }
    };

    /*
     * Feeding information from the server to the fragments.
     * Fragments is only getting the information they need.
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
            Log.d(TAG, "SwitchFragment ALIVE!");
            if(tf != null) {
                tf.serverSync(parser.getTimers(message));
                Log.d(TAG, "TIMER fragemnt ALIVE!");
            }
        }

    }

}