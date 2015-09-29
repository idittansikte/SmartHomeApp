package se.aleer.smarthome;

import android.app.ActionBar;
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

public class RemoteControl extends AppCompatActivity implements SwitchListFragment.OnEditSwitchListener {

    private static final String TAG = "RemoteControl";

    private Fragment contentFragment;
    SwitchListFragment switchListFragment;
    SettingFragment settingFragment;
    SwitchManagerPopup mAddSwitchPopup;
    FragmentPagerAdapter mAdapterViewPager;
    // If edit-switch mode selected in SwitchListFragment this is runs
    public void onEditSwitch(Switch swtch)
    {
        // #### Switch fragment from SwitchListFragment to SwitchManagerFragment ####
       /* SwitchManagerFragment managerFragment = (SwitchManagerFragment)
                getFragmentManager().findFragmentByTag(SwitchManagerFragment.ARG_ITEM_ID);

        if (managerFragment == null) {
            // Create fragment
            managerFragment = new SwitchManagerFragment();
        }
        // Set switch so that it can be edited
        managerFragment.setSwitch(swtch);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever it is in the fragment container with this one...
        transaction.replace(R.id.content_frame, managerFragment, SwitchManagerFragment.ARG_ITEM_ID);
        // Add transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    // If SwitchManagerFragment needs update switch in SwitchListFragment this is executed.
    public void onManagedSwitch(Switch swtch, boolean remove){
       /* // Remove SwitchManagerFragment from stack so its clean to the next time..
        getFragmentManager().popBackStack();
        // #### Switch fragment from SwitchListFragment to SwitchManagerFragment ####
        SwitchListFragment listFragment = (SwitchListFragment)
                getFragmentManager().findFragmentByTag(SwitchListFragment.ARG_ITEM_ID);

        if (listFragment == null) {
            // Create fragment
            listFragment = new SwitchListFragment();
        }
        // Set switch so that it can be edited
        listFragment.manageManagedSwitch(swtch, remove);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever it is in the fragment container with this one...
        transaction.replace(R.id.content_frame, listFragment, SwitchListFragment.ARG_ITEM_ID);
        // Add transaction to the back stack so the user can navigate back
        //transaction.addToBackStack(null);
        transaction.commit();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SwitchListFragment switchListFragment = new SwitchListFragment();
        fragmentTransaction.add(R.id.content_frame, switchListFragment, SwitchListFragment.ARG_ITEM_ID);
        fragmentTransaction.commit();
*/
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageMargin(20);
        //viewPager.setPageMarginDrawable(R.color.black);
        mAdapterViewPager = new MyFragmentPagerAdapter(getSupportFragmentManager(), RemoteControl.this);
        viewPager.setAdapter(mAdapterViewPager);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        // Force full width
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
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



    /*public void switchContent(Fragment fragment, String tag) {
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
    }*/

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

}