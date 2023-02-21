package com.example.drawiz.logic;

import java.io.Serializable;

public class Word implements Serializable {


    private String theWord;
    private int bonus;



    public Word(){}


    public String getTheWord() {
        return theWord;
    }

    public Word setTheWord(String theWord) {
        this.theWord = theWord;
        return this;
    }

    public int getBonus() {
        return bonus;
    }

    public Word setBonus(int bonus) {
        this.bonus = bonus;
        return this;
    }
}
