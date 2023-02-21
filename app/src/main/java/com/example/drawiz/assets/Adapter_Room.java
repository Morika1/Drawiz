package com.example.drawiz.assets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawiz.R;
import com.example.drawiz.logic.CallBack_openPageProtocol;
import com.example.drawiz.logic.Room;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Adapter_Room extends RecyclerView.Adapter<Adapter_Room.RoomViewHolder> {

    private Context context;
    private ArrayList<Room> rooms;
    private CallBack_openPageProtocol callBack_openPageProtocol;

    public Adapter_Room(Context context, ArrayList<Room> rooms){
        this.context = context;
        this.rooms = rooms;
    }


    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_game, parent, false);
        RoomViewHolder roomViewHolder = new RoomViewHolder(view);
        return roomViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.listgame_LBL_artistname.setText(room.getArtist().getUserName());
        holder.listgame_BTN_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGamePage(room);
            }
        });

    }

    public void setCallBack_openPageProtocol(CallBack_openPageProtocol callBack_openPageProtocol) {
        this.callBack_openPageProtocol = callBack_openPageProtocol;
    }

    private void openGamePage(Room room) {
        callBack_openPageProtocol.openGame(room);
    }

    @Override
    public int getItemCount() {
        return rooms == null || rooms.size() ==0? 0: rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton listgame_BTN_join;
        private MaterialTextView listgame_LBL_artistname;


        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            findViews(itemView);

        }


        private void findViews(View itemView) {

            listgame_BTN_join = itemView.findViewById(R.id.listgame_BTN_join);
            listgame_LBL_artistname= itemView.findViewById(R.id.listgame_LBL_artistname);
        }
    }




}
