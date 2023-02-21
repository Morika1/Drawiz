package com.example.drawiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawiz.R;
import com.example.drawiz.logic.CallBack_optionProtocol;
import com.google.android.material.button.MaterialButton;

public class Menu_Fragment extends Fragment {

    private MaterialButton home_BTN_play ;
    private CallBack_optionProtocol callBack_optionProtocol;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);


        findViews(view);
        initViews();

        return view;
    }

    public void setCallBack_optionProtocol(CallBack_optionProtocol callBack_optionProtocol) {
        this.callBack_optionProtocol = callBack_optionProtocol;
    }

    private void initViews() {

        home_BTN_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked();
            }
        });
    }

    private void clicked() {
        callBack_optionProtocol.openPage();
    }

    private void findViews(@NonNull View view) {
        home_BTN_play=  view.findViewById(R.id.home_BTN_play);
    }


}
