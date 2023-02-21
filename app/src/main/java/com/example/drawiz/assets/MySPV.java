package com.example.drawiz.assets;

import android.content.Context;
import android.content.SharedPreferences;

public class MySPV {

    private static final String DB_FILE= "DB_FILE";
    private static MySPV mySPV = null;
    private SharedPreferences sharedPreferences;

    private MySPV(Context context){
        sharedPreferences = context.getSharedPreferences(DB_FILE, context.MODE_PRIVATE);
    }


    public static MySPV getIntance() {
        return mySPV;
    }

    public static void init(Context context){
        if( mySPV == null)
            mySPV = new MySPV(context);
    }

    public void putInt(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public int getInt(String key, int def){
        return sharedPreferences.getInt(key,def);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getString(String key, String def){
        return sharedPreferences.getString(key,def);
    }


}
