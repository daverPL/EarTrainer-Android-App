package com.example.eartrainer;

/**
 * Created by dawid on 19.06.2017.
 */

public class NamedSequence {
    private String fullName, shortName;
    private int total, correct;
    public String getFullName(){
        return fullName;
    }
    public String getShortName(){
        return shortName;
    }

    NamedSequence(String f, String s){
        fullName = f;
        shortName = s;
        total = 0;
        correct = 0;
    }
}

