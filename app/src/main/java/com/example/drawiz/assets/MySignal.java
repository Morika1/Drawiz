package com.example.drawiz.assets;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.drawiz.R;

public class MySignal {


    private static MySignal mySignal= null;
    private Context context;

    private MySignal(Context context){
        this.context = context;

    }

    public static void init(Context context){
        if(mySignal == null)
            mySignal = new MySignal(context);
    }


    public static MySignal getInstance(){
        return mySignal;
    }

    public void toast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // TODO remove if no usage at the end
    public void vibrate(){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

    }

    public void useCoins(){
        CoinSound coinSound = new CoinSound();
        coinSound.execute();
    }



    public class CoinSound extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MediaPlayer player = MediaPlayer.create(context, R.raw.msc_coin);
            player.setVolume(1.0f, 1.0f);
            player.start();

            return null;
        }

    }

}
