package com.example.drawiz.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.drawiz.R;
import com.example.drawiz.assets.MyImageUtils;
import com.example.drawiz.fragments.Menu_Fragment;
import com.example.drawiz.logic.CallBack_optionProtocol;
import com.example.drawiz.logic.UserInfo;
import com.google.android.material.textview.MaterialTextView;

public class HomeActivity extends AppCompatActivity {

    public static final String KEY_USER = "KEY_USER";
    private UserInfo user;
    private Menu_Fragment menu_fragment;
    private AppCompatImageView home_IMG_background;
    private MaterialTextView home_LBL_hellouser;
    private MaterialTextView home_LBL_numcoins;


    CallBack_optionProtocol callBack_optionProtocol = new CallBack_optionProtocol() {
        @Override
        public void openPage() {
            openPageFromMenu();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        findViews();
        MyImageUtils.getInstance().loadUri("https://www.leatheredgepaint.com/hs-fs/hubfs/social-suggested-images/Custom_Color_Service.jpg?width=1440&name=Custom_Color_Service.jpg", home_IMG_background); //TODO choose jpg and add to link
        menu_fragment = new Menu_Fragment();
        menu_fragment.setCallBack_optionProtocol(callBack_optionProtocol);

        Intent prevIntent = getIntent();
        if(prevIntent.getExtras()!= null){
            user = (UserInfo) prevIntent.getExtras().getSerializable(KEY_USER);

            home_LBL_hellouser.setText("Hello " + user.getUserName());
            home_LBL_numcoins.setText(user.getNumOfCoins() + "");

        }

        getSupportFragmentManager().beginTransaction().add(R.id.home_LAY_menufragment, menu_fragment).commit();
    }

    private void findViews() {

        home_IMG_background = findViewById(R.id.home_IMG_background);
        home_LBL_hellouser= findViewById(R.id.home_LBL_hellouser);
        home_LBL_numcoins= findViewById(R.id.home_LBL_numcoins);
    }

    private void openPageFromMenu() {
        Intent intent;
        intent =  new Intent(this, LobbyActivity.class);
        intent.putExtra(KEY_USER, user);
        startActivity(intent);
        finish();

    }
}