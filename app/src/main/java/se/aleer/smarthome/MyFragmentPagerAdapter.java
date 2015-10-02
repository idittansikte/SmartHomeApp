package se.aleer.smarthome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Overview", "Timers" };
    private Context context;
    //public String SWITCH_FRAG_TAG;
    //public String TIMER_FRAG_TAG;
    private SwitchListFragment mSwitchFrag;
    private TimerFragment mTimerFrag;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context=context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d("PagerAdapter", "getItem() = 0");
                return SwitchListFragment.newInstance(0, tabTitles[position]);
            case 1:
                Log.d("PagerAdapter", "getItem() = 1");
                return TimerFragment.newInstance(1, tabTitles[position]);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition)
    {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter
        switch (position) {
            case 0:
                mSwitchFrag = (SwitchListFragment) createdFragment;
                break;
            case 1:
                mTimerFrag = (TimerFragment) createdFragment;
                break;
        }
        // ... save the tags somewhere so you can reference them later
        return createdFragment;
    }

    public SwitchListFragment getSwitchFragment(){
        return mSwitchFrag;
    }
    public TimerFragment getTimerFragment(){
        return mTimerFrag;
    }
}
