package com.example.eartrainer;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import net.mabboud.android_tone_player.OneTimeBuzzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Quiz extends AppCompatActivity {
    int numberOfQuestions;
    boolean upDirection, downDirection;
    TextView result, answer;
    Button replayButton;
    ArrayList<Integer> intervalsPositions = new ArrayList<>();
    ArrayList<Integer> chordsPositions = new ArrayList<>();
    Interval [] intervals;
    Chord [] chords;
    double [] frequencies = {880.000, 830.609, 783.991, 739.989, 698.456, 659.255, 622.254, 587.330, 554.365, 523.251, 493.883, 466.164,
            440.000, 415.305, 391.995, 369.994, 349.228, 329.628, 311.127, 293.665, 277.183, 261.626, 246.942, 233.082, 220.000};
    DateFormat formatter;
    public HashMap <String, Integer> CorrectAnswers;
    public HashMap <String, Integer> WrongAnswers;
    public HashMap <Date, HashMap <String, Integer>> correctByDay;
    public HashMap <Date, HashMap <String, Integer>> wrongByDay;
    public  ArrayList <Integer> previousNotes;
    NamedSequence ans;
    int correctAnswers = 0;
    int allAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        formatter = new SimpleDateFormat("dd/MM/yyyy");
        loadStats();
        System.out.println(CorrectAnswers);
        System.out.println(WrongAnswers);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GridLayout ll = (GridLayout) findViewById(R.id.ll);
        result = (TextView) findViewById(R.id.points);
        result.setText("0/0");
        answer = (TextView) findViewById(R.id.answer);
        replayButton = (Button) findViewById(R.id.replayButton);
        intervals = new Interval[14];
        setIntervals();
        chords = new Chord[8];
        setChords();
        Intent intent = getIntent();
        intervalsPositions = intent.getIntegerArrayListExtra("selectedIntervals");
        chordsPositions = intent.getIntegerArrayListExtra("selectedChords");
        numberOfQuestions = intent.getIntExtra("numberOfQuestionsS", numberOfQuestions);
        upDirection = intent.getBooleanExtra("upDirection", upDirection);
        downDirection = intent.getBooleanExtra("downDirection", downDirection);

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                ans = getOne();
            }
        });
        thread.start();

        for(int i = 0; i < intervalsPositions.size(); i++) {

            final Button btn = new Button(this);
            btn.setId(i);
            btn.setAllCaps(false);
            btn.setText(intervals[intervalsPositions.get(i)].getShortName());

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(btn.getText() == ans.getShortName()) {

                        correctAnswers++;
                        answer.setTextColor(Color.GREEN);

                        if (CorrectAnswers.containsKey(ans.getFullName())) {
                            Integer n = CorrectAnswers.get(ans.getFullName());
                            CorrectAnswers.remove(ans.getFullName());
                            CorrectAnswers.put(ans.getFullName(), n+1);
                        } else {
                            CorrectAnswers.put(ans.getFullName(), 1);
                        }
                    }
                    else {

                        answer.setTextColor(Color.RED);

                        if (WrongAnswers.containsKey(ans.getFullName())) {
                            Integer n = WrongAnswers.get(ans.getFullName());
                            WrongAnswers.remove(ans.getFullName());
                            WrongAnswers.put(ans.getFullName(), n+1);
                        } else {
                            WrongAnswers.put(ans.getFullName(), 1);
                        }
                    }

                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);

                    if(allAnswers == numberOfQuestions) {

                        try {
                            saveStats();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(Quiz.this).create();
                        alertDialog.setTitle("End of training");
                        alertDialog.setMessage("Your score: " + correctAnswers + "/" + allAnswers);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                    else {

                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                ans = getOne();
                            }
                        });

                        thread.start();
                    }
                }
            });

            ll.addView(btn);
        }

        for(int i = 0; i < chordsPositions.size(); i++) {

            final Button btn = new Button(this);
            btn.setId(intervalsPositions.size()+i);
            btn.setAllCaps(false);
            btn.setText(chords[chordsPositions.get(i)].getShortName());

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(btn.getText() == ans.getShortName()) {

                        correctAnswers++;
                        answer.setTextColor(Color.GREEN);

                        if (CorrectAnswers.containsKey(ans.getFullName())) {
                            Integer n = CorrectAnswers.get(ans.getFullName());
                            CorrectAnswers.remove(ans.getFullName());
                            CorrectAnswers.put(ans.getFullName(), n+1);
                        } else {
                            CorrectAnswers.put(ans.getFullName(), 1);
                        }
                    }
                    else {

                        answer.setTextColor(Color.RED);

                        if (WrongAnswers.containsKey(ans.getFullName())) {
                            Integer n = WrongAnswers.get(ans.getFullName());
                            WrongAnswers.remove(ans.getFullName());
                            WrongAnswers.put(ans.getFullName(), n+1);
                        } else {
                            WrongAnswers.put(ans.getFullName(), 1);
                        }
                    }

                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);

                    if(allAnswers == numberOfQuestions) {

                        try {
                            saveStats();
                        } catch (IOException e) {
                            //
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(Quiz.this).create();
                        alertDialog.setTitle("End of training");
                        alertDialog.setMessage("Your score: " + correctAnswers + "/" + allAnswers);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                    else {

                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                ans = getOne();
                            }
                        });
                        thread.start();
                    }
                }
            });
            ll.addView(btn);
        }

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(previousNotes);
            }
        });

    }

    private void setIntervals() {
        intervals[0] = new Interval("Perfect unison", "P1", 0);
        intervals[1] = new Interval("Minor second", "m2", 1);
        intervals[2] = new Interval("Major second", "M2", 2);
        intervals[3] = new Interval("Minor third", "m3", 3);
        intervals[4] = new Interval("Major third", "M3", 4);
        intervals[5] = new Interval("Perfect fourth", "P4", 5);
        intervals[6] = new Interval("Augmented fourth", "A4", 6);
        intervals[7] = new Interval("Diminished fifth", "d5", 6);
        intervals[8] = new Interval("Perfect fifth", "P5", 7);
        intervals[9] = new Interval("Minor sixth", "m6", 8);
        intervals[10] = new Interval("Major sixth", "M6", 9);
        intervals[11] = new Interval("Minor seventh", "m7", 10);
        intervals[12] = new Interval("Major seventh", "M7", 11);
        intervals[13] = new Interval("Perfect octave", "P8", 12);
    }

    private void setChords() {
        chords[0] = new Chord("Major Tonica", "C:MT", intervals[4], intervals[3]);
        chords[1] = new Chord("Major First", "C:M1", intervals[3], intervals[5]);
        chords[2] = new Chord("Major Second", "C:M2", intervals[5], intervals[4]);
        chords[3] = new Chord("Minor Tonica", "C:mT", intervals[3], intervals[4]);
        chords[4] = new Chord("Minor First", "C:m1", intervals[4], intervals[5]);
        chords[5] = new Chord("Minor Second", "C:m2", intervals[5], intervals[3]);
        chords[6] = new Chord("Diminished", "C:d", intervals[3], intervals[3]);
        chords[7] = new Chord("Augmented", "C:A", intervals[4], intervals[4]);
    }

    NamedSequence getOne() {

        Random rand = new Random();

        int random = rand.nextInt(intervalsPositions.size() + chordsPositions.size());

        if ((chordsPositions.size() == 0 && intervalsPositions.size() > 0) || random < intervalsPositions.size()) {

            int elem = rand.nextInt(intervalsPositions.size());
            ArrayList <Integer> notes = new ArrayList<>();
            int tone = rand.nextInt(8);
            notes.add(tone);
            notes.add(intervals[intervalsPositions.get(elem)].getDistance() + tone);
            previousNotes = notes;
            play(notes);
            return intervals[intervalsPositions.get(elem)];

        } else {

            int elem = rand.nextInt(chordsPositions.size());
            ArrayList <Integer> notes = new ArrayList<>();
            int tone = rand.nextInt(8);
            notes.add(tone);
            notes.add(chords[chordsPositions.get(elem)].intervals.get(0).getDistance() + notes.get(0));
            notes.add(chords[chordsPositions.get(elem)].intervals.get(1).getDistance() + notes.get(1));
            previousNotes = notes;
            play(notes);
            return chords[chordsPositions.get(elem)];
        }
    }

    void play(final ArrayList<Integer> lastNotes) {

        final int firstSound, secondSound, thirdSound;
        boolean up;
        if(upDirection && downDirection) {
            Random rand = new Random();
            int random = rand.nextInt(2);
            if(random == 0) {
                up = true;
            } else {
                up = false;
            }
        } else if(upDirection) {
            up = true;
        } else {
            up = false;
        }

        if(up) {
            firstSound = lastNotes.size()-1;
            secondSound = lastNotes.size()-2;
            thirdSound = lastNotes.size()-3;
        } else {
            firstSound = 0;
            secondSound = 1;
            thirdSound = 2;
        }


        OneTimeBuzzer buzzer = new OneTimeBuzzer();
        buzzer.setDuration(0.5);
        buzzer.setToneFreqInHz(frequencies[lastNotes.get(firstSound)]);
        buzzer.play();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                OneTimeBuzzer buzzer2 = new OneTimeBuzzer();
                buzzer2.setDuration(0.5);
                buzzer2.setToneFreqInHz(frequencies[lastNotes.get(secondSound)]);
                buzzer2.play();
            }
        },  500);

        if(lastNotes.size()>2) {
                    Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
            OneTimeBuzzer buzzer3 = new OneTimeBuzzer();
            buzzer3.setDuration(0.5);
            buzzer3.setToneFreqInHz(frequencies[lastNotes.get(thirdSound)]);
            buzzer3.play();
                        }
                    },  1000);
        }
    }

    void loadStats(){

        try{
            FileInputStream fis = openFileInput("EarTrainerStats");
            ObjectInputStream ois = new ObjectInputStream(fis);
            correctByDay = (HashMap <Date, HashMap <String, Integer>>) ois.readObject();
            wrongByDay = (HashMap <Date, HashMap <String, Integer>>) ois.readObject();
        }catch (FileNotFoundException f){
            correctByDay = new HashMap <Date, HashMap <String, Integer>>();
            wrongByDay = new HashMap <Date, HashMap <String, Integer>>();
        }catch(Exception e){}

        Date today = new Date();
        Date currentDate = null;
        try {
            currentDate = formatter.parse(formatter.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!correctByDay.containsKey(currentDate)) {
            correctByDay.put(currentDate, new HashMap <String, Integer>());
        }
        if(!wrongByDay.containsKey(currentDate)) {
            wrongByDay.put(currentDate, new HashMap <String, Integer>());
        }

        CorrectAnswers = correctByDay.get(currentDate);
        WrongAnswers = wrongByDay.get(currentDate);
    }

    void saveStats() throws IOException {

        reload();
        FileOutputStream fos = openFileOutput("EarTrainerStats", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(correctByDay);
        oos.writeObject(wrongByDay);
        oos.close();
    }

    void reload(){

        Date today = new Date();
        Date currentDate = null;
        try {
            currentDate = formatter.parse(formatter.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(correctByDay.containsKey(currentDate)) {
            correctByDay.remove(currentDate);
            correctByDay.put(currentDate, CorrectAnswers);
        }

        if(wrongByDay.containsKey(currentDate)) {
            wrongByDay.remove(currentDate);
            wrongByDay.put(currentDate, WrongAnswers);
        }
    }
}
