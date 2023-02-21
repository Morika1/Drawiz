package com.example.drawiz.db;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.drawiz.logic.CallBack_openResultProtocol;
import com.example.drawiz.logic.Piece;
import com.example.drawiz.logic.Room;
import com.example.drawiz.logic.Segment;
import com.example.drawiz.logic.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

public class DrawingDBManager {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference("lobby");
    private CallBack_openResultProtocol callBack_openResultProtocol;

    public DrawingDBManager() {
    }

    public void setCallBack_openResultProtocol(CallBack_openResultProtocol callBack_openResultProtocol) {
        this.callBack_openResultProtocol = callBack_openResultProtocol;
    }

    public void guesserUpdatesStatus(Room room, ValueEventListener listener) {
        ref.child("rooms").child(room.getRoomId()).child("status").setValue(true);
//                start= false;
//            }
        //read segment from db
        ref.child("rooms").child(room.getRoomId()).child("segment").addValueEventListener(listener);

    }


    public void artistReadsStatus(Piece piece, Room room) {
        ref.child("rooms").child(room.getRoomId()).child("status").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getValue(boolean.class) == true){
                    //change boolean to false in database
                    Log.d("read status", " boolan is true");
                    ref.child("rooms").child(room.getRoomId()).child("status").setValue(false);
                    Log.d("read status", " boolan was changed to false");
                    Segment segment = convertPieceToSegment(piece);


                    ref.child("rooms").child(room.getRoomId()).child("segment").setValue(segment);
                    Log.d("read status", "after writing segment to db");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Segment convertPieceToSegment(Piece piece) {
        return piece == null ? null: new Segment().
                setColor(piece.getColor()).
                setBrushSize(piece.getBrush_width()).
                setPathString(convertPath(piece.getPath()));
    }

    private String convertPath(Path path) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        StringBuilder stringBuilder = new StringBuilder();

        for(float i=0; i< pathMeasure.getLength(); i++){
            float[] coordinates= new float[2];
            pathMeasure.getPosTan(i, coordinates, null);
            if(i==0)
                stringBuilder.append("M").append(coordinates[0]).append(" ").append(coordinates[1]).append(" ");
            else
                stringBuilder.append("L").append(coordinates[0]).append(" ").append(coordinates[1]).append(" ");

        }

        return stringBuilder.toString();
    }

    public void artistReadsRoom(Room room) {
        ref.child("rooms").child(room.getRoomId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> roomData = (HashMap<String, Object>)snapshot.getValue();
                room. setRoomId((String)snapshot.getKey()).
                        setArtist(new Gson().fromJson((String) roomData.get("artist").toString(), UserInfo.class)).
                        setGuesser(new Gson().fromJson((String) roomData.get("guesser").toString(), UserInfo.class));

                ref.child("rooms").child(room.getRoomId()).removeEventListener(this);
                ref.child("rooms").child("status").removeValue();
                ref.child("rooms").child("segment").removeValue();
                ref.child("rooms").child(room.getRoomId()).removeValue();
                callBack_openResultProtocol.openResult(room);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
