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
    //  Lobby lobby;
    UserInfo user;

    ValueEventListener roomsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                //  HashMap<String,HashMap<String, Object>> map= (HashMap<String,HashMap<String, Object>>)snapshot.getValue();
                for(DataSnapshot roomSnapshot: snapshot.getChildren() ) {
                   // if(roomSnapshot.exists()){
                        HashMap<String, Object> roomData = (HashMap<String, Object>)roomSnapshot.getValue();
    //                    for(int i=0; i<roomData.size(); i++){
    //                        Log.d("Value",(String) roomData.get("artist"));
    //                    }
                    if (roomData.containsKey("artist")){
                        Room aRoom = new Room().
                                setRoomId((String)roomSnapshot.getKey()).
                                setArtist(new Gson().fromJson((String) roomData.get("artist").toString(), UserInfo.class)).
                                // setGuesser(new Gson().fromJson((String) roomData.get("guesser").toString(), UserInfo.class)).
                                        setWord(new Gson().fromJson((String) roomData.get("word").toString(), Word.class));
                              //  setTheGame(new Gson().fromJson((String) roomData.get("game").toString(), Game.class));

                        if (!aRoom.getRoomId().equals(user.getUserId()))
                            allRooms.add(aRoom);

                    }
                  //  }

                    }
               // Log.d("allrooms size", allRooms.size()+"\n" );

                // lobby.setRooms(allRooms);
            }
//                    lobby.setRooms((HashMap<String,Room>) snapshot.getValue());
//
            //adapter_room = new Adapter_Room(LobbyActivity.this, lobby.getRoomsList());
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

            removeListener(ref.child("rooms"),roomsEventListener); //TODO pass ref and listener
            user.setMode(UserInfo.MODE.GUESSER);
            room.setGuesser(user);
            saveGuesserToDB(room);
            Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
            intent.putExtra(GameActivity.KEY_ROOM,room );
            startActivity(intent);
            finish();


        }
    };

    private void removeListener(DatabaseReference ref, ValueEventListener listener) { //TODO update method to get ref and listener to remove
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref= db.getReference("lobby");

        ref.removeEventListener(listener);
    }


    private void saveGuesserToDB(Room room) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");
       // HashMap<String, Object> guesserjson = new HashMap<>();
      //  guesserjson.put("guesser", new Gson().toJson(room.getGuesser()));
        ref.child("rooms").
                child(room.getRoomId()).
                child("guesser").
                setValue(new Gson().toJson(room.getGuesser()));
                //updateChildren(guesserjson);




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
                       // openGame(ref.child("rooms").child(room.getRoomId()).child("guesser"), room);
                        Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                        intent.putExtra(GameActivity.KEY_ROOM, room);
                      //  removeListener(ref.child("rooms").child(room.getRoomId()).child("guesser"), guesserEventListener); // TODO add ref and listener to signiture
                      //  ref.child("rooms").child(room.getRoomId()).child("guesser").removeEventListener();
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
//        ref.child("rooms").child(room.getRoomId()).child("guesser").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.exists()){
//                    if(!snapshot.getValue().equals("empty")){
//                        Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
//                        intent.putExtra(GameActivity.KEY_ROOM, room);
//                        ref.child("rooms").child(room.getRoomId()).child("guesser").removeEventListener();
//                        startActivity(intent);
//                        finish();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                //handle error
//
//            }
//        });


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

        //  lobby = new Lobby();
        words_fragment = new Words_Fragment();
        words_fragment.setCallBack_wordProtocol(callBack_wordProtocol);
        //     words_fragment.setCallBack_readyProtocol(callBack_readyProtocol);

//        allRooms = new ArrayList<Room>();
        readRoomsFromDB();
//        Lobby lobby = new Lobby().setRooms(allRooms);
//
//        adapter_room = new Adapter_Room(this, lobby.getRoomsList());
//        lobby_LST_rooms.setLayoutManager(new LinearLayoutManager(this));
//        lobby_LST_rooms.setAdapter(adapter_room);

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
        // lobby.getRooms().put(room.getRoomNumber(),room);
       // String roomJson = new Gson().toJson(room);
        Gson json= new Gson();
        HashMap<String, Object> roomJson = new HashMap<>();
        roomJson.put("roomId", room.getRoomId());
        roomJson.put("artist", json.toJson(room.getArtist()));
        roomJson.put("guesser", "empty");
        roomJson.put("word", json.toJson(room.getWord()));
       // roomJson.put("game", json.toJson(room.getTheGame()));
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");
        ref.child("rooms").child(room.getRoomId()).setValue(roomJson);
    }


    private void readRoomsFromDB(){
        //allRooms = new ArrayList<Room>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("lobby");

        ref.child("rooms").addValueEventListener(roomsEventListener);
//        ref.child("rooms").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                  //  HashMap<String,HashMap<String, Object>> map= (HashMap<String,HashMap<String, Object>>)snapshot.getValue();
//                    for(DataSnapshot roomSnapshot: snapshot.getChildren() ) {
//                        HashMap<String, Object> roomData = (HashMap<String, Object>)roomSnapshot.getValue();
//                        for(int i=0; i<roomData.size(); i++){
//                            Log.d("Value",(String) roomData.get("artist"));
//                        }
//                        Room aRoom = new Room().
//                                setRoomId((String)roomSnapshot.getKey()).
//                                setArtist(new Gson().fromJson((String) roomData.get("artist").toString(), UserInfo.class)).
//                               // setGuesser(new Gson().fromJson((String) roomData.get("guesser").toString(), UserInfo.class)).
//                                setWord(new Gson().fromJson((String) roomData.get("word").toString(), Word.class)).
//                                setTheGame(new Gson().fromJson((String) roomData.get("game").toString(), Game.class));
//
//                        if (!aRoom.getRoomId().equals(user.getUserId()))
//                            allRooms.add(aRoom);
//                    }
//                    Log.d("allrooms size", allRooms.size()+"\n" );
//
//                    // lobby.setRooms(allRooms);
//                }
////                    lobby.setRooms((HashMap<String,Room>) snapshot.getValue());
////
//                //adapter_room = new Adapter_Room(LobbyActivity.this, lobby.getRoomsList());
//                adapter_room = new Adapter_Room(LobbyActivity.this, allRooms);
//                adapter_room.setCallBack_openPageProtocol(callBack_openPageProtocol);
//                lobby_LST_rooms.setLayoutManager(new LinearLayoutManager(LobbyActivity.this));
//                lobby_LST_rooms.setAdapter(adapter_room);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // handle error
//            }
//        });
    }

    private void findViews() {
        lobby_LST_rooms = findViewById(R.id.lobby_LST_rooms);
        lobby_IMG_background = findViewById(R.id.lobby_IMG_background);
        lobby_BTN_creategame = findViewById(R.id.lobby_BTN_creategame);
        lobby_BTN_back = findViewById(R.id.lobby_BTN_back);
    }

//    private AppCompatImageView lobby_IMG_background;
//    private MaterialButton lobby_BTN_creategame;
//    private RecyclerView lobby_LST_rooms;
//    private Words_Fragment words_fragment;
//    private ArrayList<Room> allRooms;
//    Adapter_Room adapter_room;
//    Lobby lobby;
//    UserInfo user;
//
//
//    CallBack_openPageProtocol callBack_openPageProtocol = new CallBack_openPageProtocol() {
//        @Override
//        public void openGame(Room room) { // user joining game as guesser (user2)
//            user.setMode(UserInfo.MODE.GUESSER);
//            room.setGuesser(user);
//            saveGuesserToFireBase(room);
//            Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
//            intent.putExtra(GameActivity.KEY_ROOM,room );
//            startActivity(intent);
//            finish();
//
//
//        }
//    };
//
//
//
//    private void saveGuesserToFireBase(Room room) {
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref= db.getReference("lobby");
//        ref.child("rooms").child(""+room.getRoomNumber()).child("guesser").setValue(room.getGuesser());
//
//    }
//
//    CallBack_wordProtocol callBack_wordProtocol = new CallBack_wordProtocol() {
//        @Override
//        public void addWord(Word word) {
//            Room room = new Room().
//                    setRoomNumber(Integer.parseInt(lobby.getRoomNumber())).
//                    setArtist(user).setWord(word);
//
//            saveRoomToDB(room);
//            words_fragment.turnWords(); // turn off
//            lobby_BTN_creategame.setVisibility(View.INVISIBLE);
//            words_fragment.startWaiting(); // start waiting lottie animation
//            readGuesserFromDB(room);
//
//
//
//
//
//        }
//    };
//
//    private void readGuesserFromDB(Room room) {
//
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref= db.getReference("lobby");
//        ref.child("rooms").child(room.getRoomNumber()+"").child("guesser").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.exists()){
//                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                //handle error
//
//            }
//        });
//
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lobby);
//
//        findViews();
//        initViews();
//        MyImageUtils.getInstance().loadUri("https://www.leatheredgepaint.com/hs-fs/hubfs/social-suggested-images/Custom_Color_Service.jpg?width=1440&name=Custom_Color_Service.jpg", lobby_IMG_background);
//
//        Intent prevIntent = getIntent();
//        user =((UserInfo) prevIntent.getExtras().getSerializable(HomeActivity.KEY_USER));
//
//        lobby = new Lobby();
//        words_fragment = new Words_Fragment();
//        words_fragment.setCallBack_wordProtocol(callBack_wordProtocol);
//   //     words_fragment.setCallBack_readyProtocol(callBack_readyProtocol);
//
////        allRooms = new ArrayList<Room>();
//        readRoomsFromDB();
////        Lobby lobby = new Lobby().setRooms(allRooms);
////
////        adapter_room = new Adapter_Room(this, lobby.getRoomsList());
////        lobby_LST_rooms.setLayoutManager(new LinearLayoutManager(this));
////        lobby_LST_rooms.setAdapter(adapter_room);
//
//        getSupportFragmentManager().beginTransaction().add(R.id.lobby_LAY_words, words_fragment).commit();
//    }
//
//    private void initViews() {
//        lobby_BTN_creategame.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               user.setMode(UserInfo.MODE.ARTIST);
//               words_fragment.turnWords(); // turn on words
//            }
//        });
//
//    }
//
//
//    private void saveRoomToDB(Room room){
//        lobby.getRooms().put(room.getRoomNumber(),room);
//
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref= db.getReference("lobby");
//        ref.child("rooms").child(room.getRoomNumber()).setValue(room);
//    }
//
//
//    private void readRoomsFromDB(){
//        //allRooms = new ArrayList<Room>();
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref= db.getReference("lobby");
//        ref.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    allRooms = (ArrayList<Room>) snapshot.getValue();
//                    Log.d("allrooms size", allRooms.size()+"" );
//                   // lobby.setRooms(allRooms);
//
//
//                }
////                    lobby.setRooms((HashMap<String,Room>) snapshot.getValue());
////
//                //adapter_room = new Adapter_Room(LobbyActivity.this, lobby.getRoomsList());
//                adapter_room = new Adapter_Room(LobbyActivity.this, allRooms);
//                adapter_room.setCallBack_openPageProtocol(callBack_openPageProtocol);
//                lobby_LST_rooms.setLayoutManager(new LinearLayoutManager(LobbyActivity.this));
//                lobby_LST_rooms.setAdapter(adapter_room);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // handle error
//            }
//        });
//    }
//
//    private void findViews() {
//        lobby_LST_rooms = findViewById(R.id.lobby_LST_rooms);
//        lobby_IMG_background = findViewById(R.id.lobby_IMG_background);
//        lobby_BTN_creategame = findViewById(R.id.lobby_BTN_creategame);
//    }
}