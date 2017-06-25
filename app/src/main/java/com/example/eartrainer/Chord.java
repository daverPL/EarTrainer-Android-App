package com.example.eartrainer;

import java.util.ArrayList;

public class Chord extends NamedSequence{
    ArrayList<Interval> intervals;

    Chord(String f, String s, ArrayList<Interval> i){
        super(f,s);
        intervals = i;
    }

    Chord(String f, String s, Interval i1, Interval i2){
        super(f,s);
        intervals = new ArrayList<>();
        intervals.add(i1);
        intervals.add(i2);
    }

}
