package de.dev_bros.workoutplaner.MainTabs;

import android.app.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.dev_bros.workoutplaner.R;

/**
 * Created by Marc on 04.03.2016.
 */
public class MainTab extends Fragment implements View.OnClickListener {

    private int mHeight;
    private int mWidth;
    private OnControlButtonClickedListener mCallback;

    private RelativeLayout[] relativeLayouts = new RelativeLayout[4];


    public static final int WORKOUT_FRAME = 0;
    public static final int PLAN_FRAME = 1;
    public static final int YOU_FRAME = 2;
    public static final int SUCCESS_FRAME = 3;

    @Override
    public void onClick(View v) {

        //Check All Grid Childs
        for(RelativeLayout rel : relativeLayouts){

            if(rel == v){
                mCallback.OnControlButtonClicked(Integer.parseInt(String.valueOf(rel.getTag())));
                return;
            }



            for(int i = 0; i < rel.getChildCount();i++){

                if(rel.getChildAt(i).getId() == v.getId() ){
                    String tag = String.valueOf(rel.getTag());
                    mCallback.OnControlButtonClicked(Integer.parseInt(tag));
                    return;
                }

            }
        }
    }

    public interface OnControlButtonClickedListener{
        public void OnControlButtonClicked(int tag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.main_tab, container, false);


        formatierung(view);

        return view;
    }

    private void formatierung(View view) {
        final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativLayout_MainTab);
        final GridLayout gridLayout = (GridLayout) view.findViewById(R.id.gridLayout_MainTab);
        ViewTreeObserver observer =relativeLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mHeight = relativeLayout.getHeight();
                mWidth= relativeLayout.getWidth();
                relativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                Log.i("Count", String.valueOf(gridLayout.getChildCount()));
                for(int i = 0; i < gridLayout.getChildCount();i++){
                    RelativeLayout frame = (RelativeLayout) gridLayout.getChildAt(i);
                    frame.setOnClickListener(MainTab.this);
                    for(int j = 0; j < frame.getChildCount();j++){
                        frame.getChildAt(j).setOnClickListener(MainTab.this);
                    }
                    relativeLayouts[i] = frame;
                    frame.getLayoutParams().height = mHeight / 4;
                    frame.getLayoutParams().width = mWidth / 3;
                }

            }
        });

        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "sleep.ttf");

        TextView lblTitle = (TextView) view.findViewById(R.id.lblTitle_MainTab);
        lblTitle.setTypeface(myTypeface);
        lblTitle.setText(getString(R.string.title_mainTab1) + "\n\t\t" + getString(R.string.title_mainTab2));
        lblTitle.setTextSize(50);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (OnControlButtonClickedListener) activity;
        }catch (ClassCastException e){
            e.toString();
        }
    }
}
