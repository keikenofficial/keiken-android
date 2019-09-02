package com.keiken.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

public class EditProfileActivity extends AppCompatActivity {

    private LinearLayout backgroundFrame;
    private BackdropFrontLayerBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();



        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);


        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        backgroundFrame = findViewById(R.id.background_frame_x);



        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int) (80 * (displayMetrics.densityDpi / 160f));
                    int bottomBarPx = (int) (56 * (displayMetrics.densityDpi / 160f));

                    int peekHeight = displayMetrics.heightPixels - viewHeight - toolbarPx - bottomBarPx;


                    sheetBehavior.setPeekHeight(peekHeight);
                    //int bottomPx = (int)( 70 * (displayMetrics.densityDpi / 160f));
                    //sheetBehaviorReviews.setPeekHeight(bottomPx);

                    int marginPx = (int) (20 * (displayMetrics.densityDpi / 160f));

                }
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle(mAuth.getCurrentUser().getDisplayName());
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



    }
}
