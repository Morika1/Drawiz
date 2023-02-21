package com.example.drawiz.activities;

import static com.example.drawiz.activities.GameActivity.KEY_ROOM;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.drawiz.R;
import com.example.drawiz.assets.MyImageUtils;
import com.example.drawiz.logic.Room;
import com.example.drawiz.logic.UserInfo;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResultActivity extends AppCompatActivity {

   private AppCompatImageView result_IMG_background;
   private MaterialTextView  result_LBL_artist;
   private MaterialTextView result_LBL_guesser;
   private LottieAnimationView result_IC_lottiecoins;
   private ExtendedFloatingActionButton result_BTN_back;
   private UserInfo theUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent prevIntent = getIntent();
        Room room = (Room) prevIntent.getExtras().getSerializable(KEY_ROOM);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getUid().equals(room.getRoomId()))
            theUser = room.getArtist();

        else
            theUser = room.getGuesser();

        findViews();
        initViews();
        MyImageUtils.getInstance().loadUri("https://www.leatheredgepaint.com/hs-fs/hubfs/social-suggested-images/Custom_Color_Service.jpg?width=1440&name=Custom_Color_Service.jpg", result_IMG_background);

        result_LBL_artist.setText("Artist: " +room.getArtist().getUserName() + " Drawings: "+ room.getArtist().getNumOfDrawings() + " Coins: " + room.getArtist().getNumOfCoins());
        result_LBL_guesser.setText("Guesser: " +room.getGuesser().getUserName() + " Guesses: "+ room.getGuesser().getNumOfGuesses() + " Coins: " + room.getGuesser().getNumOfCoins());

        result_IC_lottiecoins.resumeAnimation();

    }

    private void initViews() {

        result_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
                intent.putExtra(HomeActivity.KEY_USER, theUser);
                startActivity(intent);
                finish();


            }
        });
    }

    private void findViews() {
        result_IMG_background = findViewById(R.id.result_IMG_background);
        result_LBL_artist = findViewById(R.id.result_LBL_artist);
        result_LBL_guesser = findViewById(R.id.result_LBL_guesser);
        result_IC_lottiecoins = findViewById(R.id.result_IC_lottiecoins);
        result_BTN_back = findViewById(R.id.result_BTN_back);
    }


}