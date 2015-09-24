package se.aleer.smarthome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Overview", "Timers" };
    private Context context;

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
                return SwitchListFragment.newInstance(0, tabTitles[position]);
            case 1:
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
}
