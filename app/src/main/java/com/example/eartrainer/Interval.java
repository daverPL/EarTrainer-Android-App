package com.example.eartrainer;

public class Interval extends NamedSequence {

    private int distance;

    Interval(String f, String s, int d){
        super(f,s);
        distance = d;
    }

    public int getDistance() {
        return distance;
    }
}
