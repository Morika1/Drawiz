package com.example.drawiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.drawiz.R;
import com.example.drawiz.logic.CallBack_wordProtocol;
import com.example.drawiz.logic.Word;
import com.example.drawiz.logic.WordManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class Words_Fragment extends Fragment {

    private LinearLayoutCompat[] words_LAY_allwords;
    private  LinearLayoutCompat words_LAY_wordslay;
    private LottieAnimationView words_IC_waiting;
    WordManager wordManager;

    private CallBack_wordProtocol callBack_wordProtocol;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_words, container, false);


        findViews(view);
        initViews();
        wordManager = new WordManager();

        updateUI();

        return view;
    }

    private void updateUI() {

        for(int i=0; i< WordManager.NUM_WORDS; i++){
            MaterialButton name = (MaterialButton) words_LAY_allwords[i].getChildAt(0);
            MaterialTextView numcoins = (MaterialTextView) words_LAY_allwords[i].getChildAt(2);
            name.setText(wordManager.getOptions()[i].getTheWord());
            numcoins.setText(wordManager.getOptions()[i].getBonus()+"");
        }
    }

    public void startWaiting(){
        words_IC_waiting.setVisibility(View.VISIBLE);
        words_IC_waiting.resumeAnimation();

    }

    public void turnWords(){
        if(words_LAY_wordslay.getVisibility() == View.INVISIBLE)
            words_LAY_wordslay.setVisibility(View.VISIBLE);

        else
            words_LAY_wordslay.setVisibility(View.INVISIBLE);
    }

    public void setCallBack_wordProtocol(CallBack_wordProtocol callBack_wordProtocol) {
        this.callBack_wordProtocol = callBack_wordProtocol;
    }


    private void initViews() {

        for(int i=0; i< words_LAY_allwords.length; i++){
            int finalI = i;
            words_LAY_allwords[i].getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked(wordManager.getOptions()[finalI]);
                }
            });
        }

    }

    private void clicked(Word word) {
        callBack_wordProtocol.addWord(word);
    }


    private void findViews(View view) {
        words_LAY_allwords = new LinearLayoutCompat[]{
                view.findViewById(R.id.words_LAY_word1),
                view.findViewById(R.id.words_LAY_word2),
                view.findViewById(R.id.words_LAY_word3)};

        words_LAY_wordslay = view.findViewById(R.id.words_LAY_wordslay);
        words_IC_waiting = view.findViewById(R.id.words_IC_waiting);
    }

}
