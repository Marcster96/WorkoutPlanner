package de.dev_bros.workoutplaner.MainTabs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dev_bros.workoutplaner.R;

/**
 * Created by Marc on 04.03.2016.
 */
public class SettingTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout =inflater.inflate(R.layout.settings_tab, container, false);

        return layout;
    }
}
