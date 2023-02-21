package com.example.drawiz.logic;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordManager {

    public final static int NUM_WORDS =3;
    private ArrayList<Word> wordBank = new ArrayList<>();

    private Word[] options;


    public WordManager(){
        addWords();
        options = initWordsSet();

    }

    private void addWords() {
        wordBank.add(new Word().setTheWord("cat").setBonus(4));
        wordBank.add(new Word().setTheWord("dog").setBonus(4));
        wordBank.add(new Word().setTheWord("fish").setBonus(1));
        wordBank.add(new Word().setTheWord("chair").setBonus(2));
        wordBank.add(new Word().setTheWord("horse").setBonus(7));
        wordBank.add(new Word().setTheWord("kitchen").setBonus(10));
        wordBank.add(new Word().setTheWord("sofa").setBonus(3));
        wordBank.add(new Word().setTheWord("coat").setBonus(6));
        wordBank.add(new Word().setTheWord("tree").setBonus(2));
        wordBank.add(new Word().setTheWord("eye").setBonus(2));
        wordBank.add(new Word().setTheWord("phone").setBonus(5));
        wordBank.add(new Word().setTheWord("bottle").setBonus(5));
        wordBank.add(new Word().setTheWord("flower").setBonus(1));
        wordBank.add(new Word().setTheWord("butterfly").setBonus(2));
        wordBank.add(new Word().setTheWord("bathroom").setBonus(10));
    }


    private Word[] initWordsSet(){

        ArrayList<Word> bank = wordBank;
        List<Word> choices = new ArrayList<>();

        for(int i=0; i<NUM_WORDS; i++){
            int random = new Random().nextInt(bank.size()-1);
            choices.add(bank.get(random));
            bank.remove(random);
        }

        return choices.toArray(new Word[NUM_WORDS]);
    }

    public Word[] getOptions() {
        return options;
    }

    public void setOptions(Word[] options) {
        this.options = options;
    }
}
