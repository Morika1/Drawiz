package com.example.drawiz.logic;

import android.graphics.Path;

public class Piece {

    private int color;
    private int brush_width;
    private Path path;

    public Piece(){

    }

    public int getColor() {
        return color;
    }

    public Piece setColor(int color) {
        this.color = color;
        return this;

    }

    public int getBrush_width() {
        return brush_width;
    }

    public Piece setBrush_width(int brush_width) {
        this.brush_width = brush_width;
        return this;
    }

    public Path getPath() {
        return path;
    }

    public Piece setPath(Path path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                ", brush_width=" + brush_width +
                '}';
    }
}
