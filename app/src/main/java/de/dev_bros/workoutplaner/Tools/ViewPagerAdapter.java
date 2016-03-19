package de.dev_bros.workoutplaner.Tools;

/**
 * Created by Marc on 04.03.2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.dev_bros.workoutplaner.MainTabs.MainTab;
import de.dev_bros.workoutplaner.MainTabs.SettingTab;

/**
 * Created by Admin on 11-12-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new MainTab();
        }else if(position == 1){
            return new SettingTab();
        }else{
            return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Home";
            case 1:
                return "Settings";
        }
        return "Default Text";
    }

    @Override
    public int getCount() {
        return 2;           // As there are only 3 Tabs
    }

}