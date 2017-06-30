package com.example.eartrainer;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Stats extends AppCompatActivity {

    TextView answers;
    public HashMap <Date, HashMap <String, Integer>> correctByDay;
    public HashMap <Date, HashMap <String, Integer>> wrongByDay;
    public HashMap <String, Integer> CorrectAnswers;
    public HashMap <String, Integer> WrongAnswers;
    DateFormat formatter;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_stats);

        formatter = new SimpleDateFormat("dd/MM/yyyy");
        loadStats();

        answers = (TextView) findViewById(R.id.answers);
        answers.setMovementMethod(new ScrollingMovementMethod());
        final ActionBar actionBar = getSupportActionBar();

        final CompactCalendarView compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        Date today = new Date();
        Date currentDate = null;

        try {
            currentDate = formatter.parse(formatter.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        actionBar.setTitle("Kalendarz: " + dateFormatMonth.format(currentDate));
        checkDate(currentDate);

        for (Date key : correctByDay.keySet()) {
            Event event = new Event(Color.GREEN, key.getTime(), "EarTrainer Stats");
            compactCalendar.addEvent(event, true);
        }

        for (Date key : wrongByDay.keySet()) {
            if(!correctByDay.containsKey(key)) {
                Event event = new Event(Color.GREEN, key.getTime(), "EarTrainer Stats");
                compactCalendar.addEvent(event, true);
            }
        }

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                checkDate(date);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle("Kalendarz: " + dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    void checkDate(Date dateClicked) {

        if(correctByDay.containsKey(dateClicked) || wrongByDay.containsKey(dateClicked)) {

            CorrectAnswers = correctByDay.get(dateClicked);
            WrongAnswers = wrongByDay.get(dateClicked);

            String correctSummary = "";
            if(CorrectAnswers.size() > 0) {
                correctSummary = correctSummary + "Name: Number of good answers\n";
                for (String key : CorrectAnswers.keySet()) {
                    correctSummary = correctSummary + key + ": " + CorrectAnswers.get(key) + "\n";
                }
                correctSummary = correctSummary + "\n\n";
            }

            if(WrongAnswers.size() > 0) {
                correctSummary = correctSummary + "Name: Number of wrong answers\n";
                for (String key : WrongAnswers.keySet()) {
                    correctSummary = correctSummary + key + ": " + WrongAnswers.get(key) + "\n";
                }
                correctSummary = correctSummary + "\n";
            }

            answers.setText(correctSummary);
        }
        else {
            answers.setText(" ");
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
    }

}
