package com.example.drawiz.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drawiz.logic.CallBack_finishProtocol;
import com.example.drawiz.logic.CallBack_openResultProtocol;
import com.example.drawiz.db.GuessingDBManager;
import com.example.drawiz.R;
import com.example.drawiz.assets.CustomDrawView;
import com.example.drawiz.assets.MySignal;
import com.example.drawiz.logic.CallBack_readStatusProtocol;
import com.example.drawiz.logic.CallBack_updateStatusProtocol;
import com.example.drawiz.logic.Piece;
import com.example.drawiz.logic.Room;
import com.example.drawiz.logic.UserInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;





public class GameActivity extends AppCompatActivity {

    CallBack_updateStatusProtocol callBack_updateStatusProtocol = new CallBack_updateStatusProtocol() {
        @Override
        public void updateStatus() {
            game_LAY_drawview.updateStatus(room);

        }
    };

    CallBack_readStatusProtocol callBack_readStatusProtocol = new CallBack_readStatusProtocol() {
        @Override
        public void readStatus(Piece piece) {
            game_LAY_drawview.readStatus(piece, room);
        }

    };

    CallBack_finishProtocol callBack_finishProtocol = new CallBack_finishProtocol() {
        @Override
        public void finishGame(Room room) {
            game_LAY_drawview.readRoom(room);
        }
    };

    CallBack_openResultProtocol callBack_openResultProtocol = new CallBack_openResultProtocol() {
        @Override
        public void openResult(Room room) {


            Intent intent = new Intent(GameActivity.this, ResultActivity.class);
            intent.putExtra(GameActivity.KEY_ROOM, room);
            startActivity(intent);
            finish();

        }
    };

    public final static String KEY_ROOM = "KEY_ROOM";
    private boolean start = true;
    private ArrayList<Piece> pieces = new ArrayList<Piece>();
    GuessingDBManager guessingDBManager;

    private MaterialButton game_BTN_undo;
    private MaterialButton game_BTN_colors;
    private MaterialButton game_BTN_brush;
    private RangeSlider game_SLD_brushsize;
    private CustomDrawView game_LAY_drawview;
    private TextInputEditText game_TXT_guess;
    private MaterialButton game_BTN_sendguess;

    Room room;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        guessingDBManager = new GuessingDBManager();
        Intent prevIntent = getIntent();
        room = (Room) prevIntent.getExtras().getSerializable(KEY_ROOM);

        findViews();
        initViews();

        updateUI();
        if (isArtist()) {
            game_LAY_drawview.setMode(room.getArtist().getMode());
            game_LAY_drawview.setCallBack_readStatusProtocol(callBack_readStatusProtocol);
            guessingDBManager.setCallBack_finishProtocol(callBack_finishProtocol);
            game_LAY_drawview.setCallBack(callBack_openResultProtocol);
            guessingDBManager.readGuessFromDB(room);
            guessingDBManager.readSuccessFromDB(room);

        } else {
            game_LAY_drawview.setMode(room.getGuesser().getMode());
            game_LAY_drawview.setCallBack_updateStatusProtocol(callBack_updateStatusProtocol);
        }
    }

    private void initViews() {

        game_LAY_drawview.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        game_LAY_drawview.
                                getViewTreeObserver().
                                removeOnGlobalLayoutListener(this);
                        game_LAY_drawview.initBitmap(game_LAY_drawview.getMeasuredHeight(), game_LAY_drawview.getMeasuredWidth());
                    }
                });

        if (isArtist()) {

            game_SLD_brushsize.setValueFrom(0.0f);
            game_SLD_brushsize.setValueTo(100.0f);

            game_BTN_undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    game_LAY_drawview.undo();
                }
            });

            game_BTN_colors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ColorPicker picker = new ColorPicker(GameActivity.this);
                    picker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                                @Override
                                public void setOnFastChooseColorListener(int position, int color) {
                                    // user changes color from palette
                                    game_LAY_drawview.setColor(color);
                                }

                                @Override
                                public void onCancel() {
                                    // user close dialog without choosing any color
                                    picker.dismissDialog();
                                }
                            }).setColumns(3) // num of colors in dialog
                            .setDefaultColorButton(Color.parseColor("#000000"))
                            .show();


                }
            });

            game_BTN_brush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClose())
                        game_SLD_brushsize.setVisibility(View.VISIBLE);

                    else
                        game_SLD_brushsize.setVisibility(View.INVISIBLE);
                }
            });

            game_SLD_brushsize.addOnChangeListener(new RangeSlider.OnChangeListener() {
                @Override
                public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                    game_LAY_drawview.setCurrent_brushWidth((int) value);
                }
            });


        } else {
            game_BTN_sendguess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer();
                }
            });
        }


    }

    boolean isArtist() {
        return currentUser.getUid().equals(room.getRoomId());
    }

    private void updateUI() {
        if (isArtist()) {
            game_BTN_undo.setVisibility(View.VISIBLE);
            game_BTN_colors.setVisibility(View.VISIBLE);
            game_BTN_brush.setVisibility(View.VISIBLE);
            game_SLD_brushsize.setVisibility(View.VISIBLE);

        } else {
            game_TXT_guess.setVisibility(View.VISIBLE);
            game_BTN_sendguess.setVisibility(View.VISIBLE);

        }

    }

    private void checkAnswer() {
        if (!room.isCorrectGuess(game_TXT_guess.getText() + "")) {
            MySignal.getInstance().toast("Try again!");
            guessingDBManager.addGuessToDB(game_TXT_guess.getText()+"", room);



        } else {
            MySignal.getInstance().toast("Good job!");

            room.getGuesser().setNumOfCoins(room.getWord().getBonus()).setNumOfGuesses();
            room.getArtist().setNumOfCoins(room.getWord().getBonus()).setNumOfDrawings();
            saveChangesToDB();
            guessingDBManager.addSuccessToDB(room);

            showReult();
        }
    }

    private void saveChangesToDB() {
            updateUserToDB("artist", room.getArtist());

            updateUserToDB("guesser", room.getGuesser());
    }

    private void updateUserToDB(String key, UserInfo user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("lobby");
        ref.child("rooms").child(room.getRoomId()).child(key).setValue(new Gson().toJson(user));

    }

    private void showReult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(KEY_ROOM, room);
        startActivity(intent);
        finish();

    }

    private boolean isClose() {
        return game_SLD_brushsize.getVisibility() == View.INVISIBLE;
    }

    private void findViews() {
        game_LAY_drawview = findViewById(R.id.game_LAY_drawview);
        // if user is artist
        if (isArtist()) {
            game_BTN_undo = findViewById(R.id.game_BTN_undo);
            game_BTN_colors = findViewById(R.id.game_BTN_colors);
            game_BTN_brush = findViewById(R.id.game_BTN_brush);
            game_SLD_brushsize = findViewById(R.id.game_SLD_brushsize);
        } else {
            game_TXT_guess = findViewById(R.id.game_TXT_guess);
            game_BTN_sendguess = findViewById(R.id.game_BTN_sendguess);
        }

    }

}


