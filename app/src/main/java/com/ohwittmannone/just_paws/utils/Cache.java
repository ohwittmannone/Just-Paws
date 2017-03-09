package com.ohwittmannone.just_paws.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Courtney on 2017-02-25.
 */

public class Cache {
    private static Cache sCache;
    private static Context sContext;

    //sharedpreferences
    public static final String USER_RELATED = "user";
    public static final String LOGIN_STATE = "login";

    private Cache(){}

    public static Cache getInstance(Context context){
        if(sCache == null){
            sCache = new Cache();
        }
        sContext = context;
        return sCache;
    }

    public void saveLoginState(boolean login){
        SharedPreferences settings = sContext.getSharedPreferences(USER_RELATED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(LOGIN_STATE, login);
        editor.commit();
    }

    public boolean getLoginState(){
        SharedPreferences settings = sContext.getSharedPreferences(USER_RELATED, Context.MODE_PRIVATE);
        boolean state = settings.getBoolean(LOGIN_STATE, false);
        return state;
    }

    public void logout(){
        saveLoginState(false);

    }
}
