package com.example.drawiz.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

    private List<String> guesses;
    private List<Segment> segments;

    private Segment segment;

    public Game(){
        guesses = new ArrayList<String>();
        segments = new ArrayList<Segment>();
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public void setGuesses(List<String> guesses) {
        this.guesses = guesses;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }
}
