package com.example.drawiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawiz.R;
import com.example.drawiz.assets.Adapter_Room;
import com.example.drawiz.assets.MyImageUtils;
import com.example.drawiz.fragments.Words_Fragment;
import com.example.drawiz.logic.CallBack_openPageProtocol;
import com.example.drawiz.logic.CallBack_wordProtocol;
import com.example.drawiz.logic.Room;
import com.example.drawiz.logic.UserInfo;
import com.example.drawiz.logic.Word;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class LobbyActivity extends AppCompatActivity {

    private AppCompatImageView lobby_IMG_background;
    private ExtendedFloatingActionButton lobby_BTN_creategame;
    private ExtendedFloatingActionButton lobby_BTN_back;
    private RecyclerView lobby_LST_rooms;
    private Words_Fragment words_fragment;
    private ArrayList<Room> allRooms = new ArrayList<Room>();
    Adapter_Room adapter_room;
    UserInfo user;

    ValueEventListener roomsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                for(DataSnapshot roomSnapshot: snapshot.getChildren() ) {
                   // if(roomSnapshot.exists()){
                        HashMap<String, Object> roomData = (HashMap<String, Object>)roomSnapshot.getValue();

                    if (roomData.containsKey("artist")){
                        Room aRoom = new Room().
                                setRoomId((String)roomSnapshot.getKey()).
                                setArtist(new Gson().fromJson((String) roomData.get("artist").toString(), UserInfo.class)).
                                        setWord(new Gson().fromJson((String) roomData.get("word").toString(), Word.class));

                        if (!aRoom.getRoomId().equals(user.getUserId()))
                            allRooms.add(aRoom);

                    }

                }
              
            }

            adapter_room = new Adapter_Room(LobbyActivity.this, allRooms);
            adapter_room.setCallBack_openPageProtocol(callBack_openPageProtocol);
            lobby_LST_rooms.setLayoutManager(new LinearLayoutManager(LobbyActivity.this));
            lobby_LST_rooms.setAdapter(adapter_room);


        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


            CallBack_openPageProtocol callBack_openPageProtocol = new CallBack_openPageProtocol() {
        @Override
        public void openGame(Room room) { // user joining game as guesser (user2)
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref= db.getReference("lobby");

            removeListener(ref.child("rooms"),roomsEventListener); 
            user.setMode(UserInfo.MODE.GUESSER);
            room.setGuesser(user);
            saveGuesserToDB(room);
            Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
            intent.putExtra(GameActivity.KEY_ROOM,room );
            startActivity(intent);
            finish();


        }
    };

    private void removeListener(DatabaseReference ref, ValueEventListener listener) { 


        ref.removeEventListener(listener);
    }


    private void saveGuesserToDB(Room room) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");

        ref.child("rooms").
                child(room.getRoomId()).
                child("guesser").
                setValue(new Gson().toJson(room.getGuesser()));
  


    }

    CallBack_wordProtocol callBack_wordProtocol = new CallBack_wordProtocol() {
        @Override
        public void addWord(Word word) {
            Room room = new Room().
                    setRoomId(user.getUserId()).
                    setArtist(user).setWord(word);

            saveRoomToDB(room);
            words_fragment.turnWords(); // turn off
            lobby_BTN_creategame.setVisibility(View.INVISIBLE);
            words_fragment.startWaiting(); // start waiting lottie animation
            readGuesserFromDB(room);

        }
    };

    private void readGuesserFromDB(Room room) {
        ValueEventListener guesserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref= db.getReference("lobby");
                if(snapshot.exists()){
                    if(!snapshot.getValue().equals("empty")){
                        Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                        intent.putExtra(GameActivity.KEY_ROOM, room);
                        startActivity(intent);
                        finish();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");
        ref.child("rooms").child(room.getRoomId()).child("guesser").addValueEventListener(guesserEventListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        findViews();
        initViews();
        MyImageUtils.getInstance().loadUri("https://www.leatheredgepaint.com/hs-fs/hubfs/social-suggested-images/Custom_Color_Service.jpg?width=1440&name=Custom_Color_Service.jpg", lobby_IMG_background);

        Intent prevIntent = getIntent();
        user =((UserInfo) prevIntent.getExtras().getSerializable(HomeActivity.KEY_USER));

    
        words_fragment = new Words_Fragment();
        words_fragment.setCallBack_wordProtocol(callBack_wordProtocol);
    
        readRoomsFromDB();

        getSupportFragmentManager().beginTransaction().add(R.id.lobby_LAY_words, words_fragment).commit();
    }

    private void initViews() {
        lobby_BTN_creategame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setMode(UserInfo.MODE.ARTIST);
                words_fragment.turnWords(); // turn on words
            }
        });

        lobby_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, HomeActivity.class);
                intent.putExtra(HomeActivity.KEY_USER, user);
                startActivity(intent);
                finish();

            }
        });

    }


    private void saveRoomToDB(Room room){
 
        Gson json= new Gson();
        HashMap<String, Object> roomJson = new HashMap<>();
        roomJson.put("roomId", room.getRoomId());
        roomJson.put("artist", json.toJson(room.getArtist()));
        roomJson.put("guesser", "empty");
        roomJson.put("word", json.toJson(room.getWord()));
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");
        ref.child("rooms").child(room.getRoomId()).setValue(roomJson);
    }


    private void readRoomsFromDB(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");

        ref.child("rooms").addValueEventListener(roomsEventListener);
    }

    private void findViews() {
        lobby_LST_rooms = findViewById(R.id.lobby_LST_rooms);
        lobby_IMG_background = findViewById(R.id.lobby_IMG_background);
        lobby_BTN_creategame = findViewById(R.id.lobby_BTN_creategame);
        lobby_BTN_back = findViewById(R.id.lobby_BTN_back);
    }
}
