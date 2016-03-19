package de.dev_bros.workoutplaner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

import de.dev_bros.workoutplaner.MainTabs.MainTab;
import de.dev_bros.workoutplaner.Plan.Plan;
import de.dev_bros.workoutplaner.Tools.UtilityBox;
import de.dev_bros.workoutplaner.Tools.ViewPagerAdapter;
import de.dev_bros.workoutplaner.You.You;

public class MainActivity extends AppCompatActivity implements MainTab.OnControlButtonClickedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Typeface myTypeface; // Font

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupParseUser();
        setupTabLayout();

        savePlansInBackground();

    }

    private void savePlansInBackground() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
        query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("App", String.valueOf(objects.size()));
                    for (ParseObject object : objects) {
                        try {
                            object.pin("Plans");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupParseUser() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("MyApp", "Anonymous login failed.");
                } else {
                    Log.d("MyApp", "Anonymous user logged in.");
                }
            }
        });

    }

    private void setupTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        /*
        Creating Adapter and setting that adapter to the viewPager
        setSupportActionBar method takes the toolbar and sets it as
        the default action bar thus making the toolbar work like a normal
        action bar.
         */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        /*
        TabLayout.newTab() method creates a tab view, Now a Tab view is not the view
        which is below the tabs, its the tab itself.
         */

        final TabLayout.Tab home = tabLayout.newTab();
        final TabLayout.Tab settings = tabLayout.newTab();

        /*
        Setting Title text for our tabs respectively
         */

        home.setIcon(R.drawable.ic_action_home_white);
        settings.setIcon(R.drawable.ic_action_settings_gray);

        /*
        Adding the tab view to our tablayout at appropriate positions
        As I want home at first position I am passing home and 0 as argument to
        the tablayout and like wise for other tabs as well
         */
        tabLayout.addTab(home, 0);
        tabLayout.addTab(settings, 1);

        /*
        TabTextColor sets the color for the title of the tabs, passing a ColorStateList here makes
        tab change colors in different situations such as selected, active, inactive etc

        TabIndicatorColor sets the color for the indiactor below the tabs
         */

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.icons));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.accent));

        /*
        Adding a onPageChangeListener to the viewPager
        1st we add the PageChangeListener and pass a TabLayoutPageChangeListener so that Tabs Selection
        changes when a viewpager page changes.
         */


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        /*
                        setting Home as White and rest grey
                        and like wise for all other positions
                         */
                        home.setIcon(R.drawable.ic_action_home_white);
                        settings.setIcon(R.drawable.ic_action_settings_gray);
                        break;
                    case 1:
                        home.setIcon(R.drawable.ic_action_home_gray);
                        settings.setIcon(R.drawable.ic_action_settings_white);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void OnControlButtonClicked(int tag) {
        if(MainTab.WORKOUT_FRAME == tag){

        }

        if(MainTab.PLAN_FRAME == tag){
            startActivity(new Intent(getApplicationContext(), Plan.class));
        }

        if(MainTab.SUCCESS_FRAME== tag){

        }
        if(MainTab.YOU_FRAME== tag){

            startActivity(new Intent(getApplicationContext(), You.class));
        }
    }

    @Override
    protected void onStop() {


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
        query.fromLocalDatastore();
        query.fromPin(ParseApplication.CHANGES);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> plans, ParseException e) {
                for (ParseObject plan : plans) {
                    plan.saveEventually();
                    plan.unpinInBackground(ParseApplication.CHANGES);
                }
            }
        });

        super.onStop();
    }
}
