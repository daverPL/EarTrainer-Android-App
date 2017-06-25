package com.example.eartrainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.mabboud.android_tone_player.ContinuousBuzzer;
import net.mabboud.android_tone_player.OneTimeBuzzer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Quiz extends AppCompatActivity {
    int numberOfQuestions;
    TextView result, answer;
    ArrayList<Integer> intervalsPositions = new ArrayList<>();
    ArrayList<Integer> chordsPositions = new ArrayList<>();
    Interval [] intervals;
    Chord [] chords;
    double [] frequencies = {880.000, 830.609, 783.991, 739.989, 698.456, 659.255, 622.254, 587.330, 554.365, 523.251, 493.883, 466.164,
            440.000, 415.305, 391.995, 369.994, 349.228, 329.628, 311.127, 293.665, 277.183, 261.626, 246.942, 233.082, 220.000};

    NamedSequence ans;
    int correctAnswers = 0;
    int allAnswers = 0;
    ArrayList <Integer> lastNotes = new ArrayList<>();

    // player:
    private final double duration = 0.5;
    private final int sampleRate = 8000;

    private final int numSamples = (int)(duration * sampleRate);
    private final double sample[] = new double[numSamples];
    private final byte generatedSnd[] = new byte[2 * numSamples];
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GridLayout ll = (GridLayout) findViewById(R.id.ll);
        result = (TextView) findViewById(R.id.points);
        result.setText("0/0");
        answer = (TextView) findViewById(R.id.answer);
        intervals = new Interval[14];
        setIntervals();
        chords = new Chord[8];
        setChords();

        Intent intent = getIntent();
        intervalsPositions = intent.getIntegerArrayListExtra("selectedIntervals");
        chordsPositions = intent.getIntegerArrayListExtra("selectedChords");
        numberOfQuestions = intent.getIntExtra("numberOfQuestionsS", numberOfQuestions);

        ans = getOne();

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                play();
            }
        });
        thread.start();
//        OneTimeBuzzer buzzer = new OneTimeBuzzer();
//        buzzer.setDuration(1);
//        buzzer.setVolume(50);
//        buzzer.setToneFreqInHz(440);
//        buzzer.play();

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
                    }
                    else {
                        answer.setTextColor(Color.RED);
                    }
                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);
                    if(allAnswers == numberOfQuestions) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Quiz.this).create();
                        alertDialog.setTitle("End of training");
                        alertDialog.setMessage("Your score: " + correctAnswers + "/" + allAnswers);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        //startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                    else {
                        ans = getOne();
                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                play();
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
                    }
                    else {
                        answer.setTextColor(Color.RED);
                    }
                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);

                    if(allAnswers == numberOfQuestions) {
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
                        ans = getOne();

                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                play();
                            }
                        });
                        thread.start();
                    }
                }
            });
            ll.addView(btn);
        }

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
            lastNotes.clear();
            int tone = rand.nextInt(12);
            lastNotes.add(tone);
            lastNotes.add(intervals[intervalsPositions.get(elem)].getDistance() + tone);
            return intervals[intervalsPositions.get(elem)];
        } else {
            int elem = rand.nextInt(chordsPositions.size());
            lastNotes.clear();
            int tone = rand.nextInt(12);
            lastNotes.add(tone);
            lastNotes.add(chords[chordsPositions.get(elem)].intervals.get(0).getDistance() + lastNotes.get(0));
            lastNotes.add(chords[chordsPositions.get(elem)].intervals.get(1).getDistance() + lastNotes.get(1));
            return chords[chordsPositions.get(elem)];
        }
    }

    void generateTone(double freqOfTone){
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        int idx = 0;
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767));

            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

    void play() {

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                generateTone(frequencies[lastNotes.get(lastNotes.size()-1)]);
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
        handler.post(new Runnable() {

            public void run() {
                generateTone(frequencies[lastNotes.get(lastNotes.size()-1)]);
                System.out.println("freq: " + frequencies[lastNotes.get(lastNotes.size()-1)]);
                playSound();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        generateTone(frequencies[lastNotes.get(lastNotes.size()-2)]);
                        playSound();
                    }
                },  550);
                if(lastNotes.size()>2) {
                    Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            generateTone(frequencies[lastNotes.get(lastNotes.size()-3)]);
                            playSound();
                        }
                    },  1100);
                }
            }
        });
    }
}
