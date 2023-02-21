package com.example.drawiz.db;

import androidx.annotation.NonNull;

import com.example.drawiz.assets.MySignal;
import com.example.drawiz.logic.CallBack_finishProtocol;
import com.example.drawiz.logic.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GuessingDBManager {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference("lobby");
    private CallBack_finishProtocol callBack_finishProtocol;


    public GuessingDBManager(){}

    public void setCallBack_finishProtocol(CallBack_finishProtocol callBack_finishProtocol) {
        this.callBack_finishProtocol = callBack_finishProtocol;
    }

    public void addSuccessToDB(Room room) {
        ref.child("rooms").child(room.getRoomId()).child("success").setValue(true);

    }

    public void readSuccessFromDB(Room room) {
        ref.child("rooms").child(room.getRoomId()).child("success").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ref.child("rooms").child(room.getRoomId()).child("success").removeEventListener(this);
                    callBack_finishProtocol.finishGame(room);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void readGuessFromDB(Room room) {
        ref.child("rooms").child(room.getRoomId()).child("guess").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                   // room.setTheGame(new Gson().fromJson(snapshot.getValue().toString(), Game.class));
                    MySignal.getInstance().toast(snapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void addGuessToDB(String guess, Room room) {
        ref.child("rooms").child(room.getRoomId()).child("guess").setValue(guess);

    }
}
