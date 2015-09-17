package se.aleer.smarthome;

import android.app.ActionBar;
import android.app.TabActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TabHost;

public class RemoteControl extends AppCompatActivity {

    private static final String TAG = "RemoteControl";

    private Fragment contentFragment;
    SwitchListFragment switchListFragment;
    SettingFragment settingFragment;
    FragmentTabHost mTabHost;
    SwitchManagerPopup mAddSwitchPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        //switchListFragment = new SwitchListFragment(RemoteControl.this, findViewById(R.id.listview));
        FragmentManager fragmentManager = getSupportFragmentManager();

        /**
         * This is called when orientation is changed.
         */
        if (savedInstanceState != null) {
            /*if (savedInstanceState.containsKey("content")) {
                String content = savedInstanceState.getString("content");
                if (content.equals(SettingFragment.ARG_ITEM_ID)) {
                    if (fragmentManager.findFragmentByTag(SettingFragment.ARG_ITEM_ID) != null) {
                        //setFragmentTitle(R.string.app_name);
                        contentFragment = fragmentManager
                                .findFragmentByTag(SettingFragment.ARG_ITEM_ID);
                    }
                }
            }*/
            if (fragmentManager.findFragmentByTag(SwitchListFragment.ARG_ITEM_ID) != null) {
                switchListFragment = (SwitchListFragment) fragmentManager
                        .findFragmentByTag(SwitchListFragment.ARG_ITEM_ID);
                contentFragment = switchListFragment;
            }
        } else {
            switchListFragment = new SwitchListFragment();
            switchListFragment.setHasOptionsMenu(false);
            switchContent(switchListFragment, SwitchListFragment.ARG_ITEM_ID);
        }

        /** Adding tabs src: */
        //mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        //mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        //mTabHost.addTab(mTabHost.newTabSpec("Switches").setIndicator("Switches"), SwitchListFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("Settings").setIndicator("Tab 2"), Settings.class, null);

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
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_add_switch:
                if (mAddSwitchPopup == null) {
                    mAddSwitchPopup = new SwitchManagerPopup(this, switchListFragment);
                }
                mAddSwitchPopup.initiatePopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }



    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate());

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            //Only FavoriteListFragment is added to the back stack.
            //if (!(fragment instanceof SettingFragment)) {
              //  transaction.addToBackStack(tag);
            //}
            transaction.commit();
            contentFragment = fragment;
        }
    }

    /*
 * We call super.onBackPressed(); when the stack entry count is > 0. if it
 * is instanceof ProductListFragment or if the stack entry count is == 0, then
 * we finish the activity.
 * In other words, from ProductListFragment on back press it quits the app.
 */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else if (contentFragment instanceof SwitchListFragment
                || fm.getBackStackEntryCount() == 0) {
            finish();
        }
    }

}