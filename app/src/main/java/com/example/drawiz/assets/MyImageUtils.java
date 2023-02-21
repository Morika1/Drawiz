package com.example.drawiz.assets;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.drawiz.R;


public class MyImageUtils {

    private static MyImageUtils instance;
    private static Context appContext;


    public static MyImageUtils getInstance(){
        return instance;
    }

    private MyImageUtils(Context context){
        appContext = context;
    }

    public static MyImageUtils initImageHelper(Context context){
        if(instance == null)
            instance = new MyImageUtils(context);

        return instance;
    }

    public void loadUri(String link, ImageView imageView){
        Glide
                .with(appContext)
                .load(link)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView);


    }

    public void loadPic(int drawable, ImageView imageView){
        Glide
                .with(appContext)
                .load(drawable)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView);



    }


}
