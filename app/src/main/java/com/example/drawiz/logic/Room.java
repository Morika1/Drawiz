package com.example.drawiz.logic;

import java.io.Serializable;

public class Room implements Serializable {

    private String roomId;
    private UserInfo artist;
    private UserInfo guesser;// user UUID;
    private Word word;

    private Game theGame;

    public Room(){
        theGame = new Game();

    }

    public String getRoomId() {
        return roomId;
    }

    public Room setRoomId( String artistId) {
        this.roomId = artistId;
        return this;
    }

    public Word getWord() {
        return word;
    }

    public Room setWord(Word word) {
        this.word = word;
        return this;
    }

    public Game getTheGame() {
        return theGame;
    }

    public Room setTheGame(Game theGame) {
        this.theGame = theGame;
        return this;
    }

    public UserInfo getArtist() {
        return artist;
    }

    public Room setArtist(UserInfo artist) {
        this.artist = artist;
        return this;
    }

    public UserInfo getGuesser() {
        return guesser;
    }

    public Room setGuesser(UserInfo guesser) {

        this.guesser = guesser;
        return this;
    }

    public boolean isCorrectGuess(String guess) {
        return guess.toLowerCase().equals(word.getTheWord());
    }
}
