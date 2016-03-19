package de.dev_bros.workoutplaner.Plan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;


import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import de.dev_bros.workoutplaner.ParseApplication;
import de.dev_bros.workoutplaner.R;

public class Plan extends AppCompatActivity implements PlanAdapter.OnControlCallbackListener_plan{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<ParseObject> plans;
    private Toolbar toolbar;
    private Paint p = new Paint();
    private int editPosition = 0;
    private boolean saveBack = false;
    private static boolean saveCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);


        getAllPins();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setupRecyclerView();
    }



    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_plan);



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)




        ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
        query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
        query.fromLocalDatastore();
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    plans = objects;
                    mAdapter = new PlanAdapter(plans);
                    mRecyclerView.setAdapter(mAdapter);


                    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                            int position = viewHolder.getAdapterPosition();

                            if (swipeDir == ItemTouchHelper.LEFT) {
                                final ParseObject planToDelte = plans.get(position);
                                plans.remove(position);
                                mAdapter.notifyItemRemoved(position);
                                mAdapter.notifyItemRangeChanged(position, plans.size());


                                planToDelte.deleteEventually(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){

                                        }
                                    }
                                });
                                planToDelte.unpinInBackground();
                            } else {
                                ParseObject planToEdit = plans.get(position);

                            }
                        }

                        @Override
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                            Bitmap icon;
                            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                                View itemView = viewHolder.itemView;
                                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                                float width = height / 3;

                                if (dX > 0) {
                                    p.setColor(Color.parseColor("#388E3C"));
                                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                                    c.drawRect(background, p);
                                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.draw);
                                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                                    c.drawBitmap(icon, null, icon_dest, p);
                                } else {
                                    p.setColor(Color.parseColor("#D32F2F"));
                                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                                    c.drawRect(background, p);
                                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                                    c.drawBitmap(icon, null, icon_dest, p);
                                }
                            }
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    };

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    itemTouchHelper.attachToRecyclerView(mRecyclerView);


                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
        query.fromLocalDatastore();
        query.whereEqualTo("User", ParseUser.getCurrentUser().getUsername());
        query.orderByAscending("createAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mAdapter.notifyItemRangeChanged(editPosition, plans.size());
                } else {
                    e.printStackTrace();
                }
            }
        });


        super.onRestart();
    }
    @Override
    public void onControlClickItem(ParseObject object, int mode, int position) {
        if(mode == PlanAdapter.PLAN){
            Log.i("App","Plan pressed");

        }

        if(mode == PlanAdapter.FAVORIT_false){
            try {
                object.unpin(ParseApplication.CHANGES);
                object.put("Favorit", false);
                object.pin(ParseApplication.CHANGES);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        if(mode == PlanAdapter.FAVORIT_true){
            try {
                object.unpin(ParseApplication.CHANGES);
                object.put("Favorit", true);
                object.pin(ParseApplication.CHANGES);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        getAllPins();
    }


    public void onClickBtnAdd(View view){
        saveCreate = true;
        startActivity(new Intent(this,CreatePlan.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            saveBack = true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {

        if(saveBack == false && saveCreate == false) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
            query.fromLocalDatastore();
            query.fromPin(ParseApplication.CHANGES);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> plans, ParseException e) {
                    for (ParseObject plan : plans){
                        plan.saveEventually();
                        plan.unpinInBackground(ParseApplication.CHANGES);
                    }
                }
            });
        }
        super.onStop();
    }

    //Zum Kontrollieren
    private void getAllPins() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
        query.fromLocalDatastore();
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("App","Generell:");
                    for (ParseObject object : objects) {
                        Log.i("App", "Object: " + object.getObjectId() + " User: " + object.get("User") + " Favorit: " + object.get("Favorit"));
                    }
                }
            }
        });

        query = ParseQuery.getQuery("Plans");
        query.fromLocalDatastore();
        query.orderByAscending("createdAt");
        query.fromPin(ParseApplication.CHANGES);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.i("App","Changes:");
                    for (ParseObject object : objects) {
                        Log.i("App", "Object: " + object.getObjectId() + " User: " + object.get("User") + " Favorit: " + object.get("Favorit"));
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if(saveCreate == true){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Plans");
            query.fromLocalDatastore();
            query.orderByAscending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() != 0) {
                        plans = objects;
                        mAdapter = new PlanAdapter(plans);
                        mRecyclerView.setAdapter(mAdapter);
                        saveCreate = false;
                    }
                }
            });
        }

        super.onResume();
    }
}
