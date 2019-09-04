package com.keiken.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateExperienceActivity extends AppCompatActivity {

    private BackdropFrontLayerBehavior sheetBehavior;
    private int peekHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experience);


        NumberPicker pickerPosti = findViewById(R.id.posti_disponibili);


        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);

        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded




        contentLayout.setBehavior(sheetBehavior);



        final DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        final LinearLayout backgroundFrame = findViewById(R.id.background_frame);


        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int)( 80 * (displayMetrics.densityDpi / 160f));

                    peekHeight = displayMetrics.heightPixels-viewHeight-toolbarPx;

                    sheetBehavior.setPeekHeight(peekHeight);

                }
            });
        }






        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Crea esperienza");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





        //posti disponibili

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        pickerPosti.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        pickerPosti.setMaxValue(99);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        pickerPosti.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        pickerPosti.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
            }
        });






        //date

        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.thumb_primary_color));
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
            }
        });

        List<Calendar> selectedDates = calendarView.getSelectedDates();





    }


   /* public void recursiveLoopChildren(boolean enable, ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);

            child.setEnabled(enable);

            if (child instanceof ViewGroup) {
                recursiveLoopChildren(enable, (ViewGroup) child);
            }
        }
    }   */


    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }




}

