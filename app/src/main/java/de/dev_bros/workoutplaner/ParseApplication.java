package de.dev_bros.workoutplaner;

import android.app.Application;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Marc on 06.03.2016.
 */
public class ParseApplication extends Application {

    public static final String CHANGES = "MYCHANGES";


    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseUser.enableAutomaticUser();


        ParseObject.unpinAllInBackground( new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                }else{
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Icons");
                    try {
                        List<ParseObject> parseObjects = query.find();

                        for(ParseObject object : parseObjects){
                            object.pin();
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}
