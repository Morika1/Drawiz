package com.example.drawiz.logic;

import java.io.Serializable;

public class Segment implements Serializable {

    private int color;
    private int brushSize;
    private String pathString;


    public  Segment(){

    }

    public int getColor() {
        return color;
    }

    public Segment setColor(int color) {
        this.color = color;
        return this;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Segment setBrushSize(int brushSize) {
        this.brushSize = brushSize;
        return this;
    }

    public String getPathString() {
        return pathString;
    }

    public Segment setPathString(String pathString) {
        this.pathString = pathString;
        return this;
    }
}


