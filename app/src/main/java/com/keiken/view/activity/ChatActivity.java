package com.keiken.view.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ChatActivity extends AppCompatActivity {


    private BackdropFrontLayerBehavior sheetBehavior;
    private int peekHeight = 0;
    private MaterialButton menuButton;
    private ImageView upArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);


        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded





        menuButton = findViewById( R.id.menu_button);
        upArrow = findViewById(R.id.up_arrow);

        contentLayout.setBehavior(sheetBehavior);
        contentLayout.setButton(menuButton);
        contentLayout.setDrawable((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.cross_to_points));

        contentLayout.setImageView(upArrow);
        contentLayout.setDrawable2((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.black_to_white_up_arrow));




        contentLayout.setButton(menuButton);
        contentLayout.setDrawable((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.cross_to_points));

        contentLayout.setImageView(upArrow);
        contentLayout.setDrawable2((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.black_to_white_up_arrow));







        final DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        final LinearLayout backgroundFrame = findViewById(R.id.background_frame);
        final int bFrameHeight = backgroundFrame.getBottom();

        //sheetBehavior.setPeekHeight(displayMetrics.heightPixels-bFrameHeight);


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
                    //int bottomPx = (int)( 70 * (displayMetrics.densityDpi / 160f));
                    //sheetBehaviorReviews.setPeekHeight(bottomPx);

                    int marginPx = (int)( 20 * (displayMetrics.densityDpi / 160f));

                }
            });
        }





        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Federico Andrea Tondini");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        //  recursiveLoopChildren(false, contentLayout);
                        menuButton.setIcon(getResources().getDrawable(R.drawable.points_to_cross));
                        AnimatedVectorDrawable ic = (AnimatedVectorDrawable) menuButton.getIcon();
                        ic.start();

                        upArrow.setImageDrawable((getResources().getDrawable(R.drawable.white_to_black_up_arrow)));
                        AnimatedVectorDrawable ic2 = (AnimatedVectorDrawable) upArrow.getDrawable();
                        ic2.start();

                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {

                        menuButton.setIcon(getResources().getDrawable(R.drawable.cross_to_points));
                        AnimatedVectorDrawable ic = (AnimatedVectorDrawable) menuButton.getIcon();
                        ic.start();

                        upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
                        AnimatedVectorDrawable ic2 = (AnimatedVectorDrawable) upArrow.getDrawable();
                        ic2.start();

                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        // recursiveLoopChildren(true, contentLayout);

                    }

                }



        });










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

         if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            //action not popBackStack

            menuButton.setIcon(getResources().getDrawable(R.drawable.cross_to_points));
            AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)menuButton.getIcon();
            ic.start();

            upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
            AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
            ic2.start();

            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        } else {
            super.onBackPressed();
        }
    }




}
