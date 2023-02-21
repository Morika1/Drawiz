package com.example.drawiz.logic;

import java.io.Serializable;

public class UserInfo implements Serializable {

    public enum MODE{
        APP,
        ARTIST,
        GUESSER
    }

    private String userId; // UUID
    private String userName;
    private int numOfDrawings;
    private int numOfGuesses;
    private int numOfCoins;
    private MODE mode = MODE.APP;

    public UserInfo(){}


    public String getUserId() {
        return userId;
    }

    public UserInfo setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public int getNumOfDrawings() {
        return numOfDrawings;
    }

    public UserInfo setNumOfDrawings() {
        this.numOfDrawings++;
        return this;
    }

    public int getNumOfGuesses() {
        return numOfGuesses;
    }

    public UserInfo setNumOfGuesses() {
        this.numOfGuesses ++;
        return this;
    }

    public int getNumOfCoins() {
        return numOfCoins;
    }

    public UserInfo setNumOfCoins(int bonus) {
        this.numOfCoins+= bonus;
        return this;
    }

    public MODE getMode() {
        return mode;
    }

    public UserInfo setMode(MODE mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", numOfDrawings=" + numOfDrawings +
                ", numOfGuesses=" + numOfGuesses +
                ", numOfCoins=" + numOfCoins +
                ", mode=" + mode +
                '}';
    }
}
