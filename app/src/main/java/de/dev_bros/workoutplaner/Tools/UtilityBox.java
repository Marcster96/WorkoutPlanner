package de.dev_bros.workoutplaner.Tools;

import android.os.Build;

/**
 * Created by Marc on 06.03.2016.
 */
public class UtilityBox {

    public static boolean isEmty(String value){
        if("".equals(value) || value == null){
            return true;
        }
        return false;
    }


    public static String getUniqueString(){
        String timeStamp = String.valueOf(System.currentTimeMillis()) + "_"+ Build.MODEL;
        return timeStamp;
    }
}
