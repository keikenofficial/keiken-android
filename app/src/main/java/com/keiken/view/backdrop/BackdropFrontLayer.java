package com.keiken.view.backdrop;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;

public class BackdropFrontLayer extends LinearLayout {

    public static int FULL_LAYER = 0;
    public static int NONE = 1;
   // public static int HEADER_ONLY = 2;


    private BackdropFrontLayerBehavior behavior;
    private MaterialButton button;
    private AnimatedVectorDrawable drawable, drawable2;
    private ImageView imageView;
    private int touchIntercept = FULL_LAYER;

    public BackdropFrontLayer(Context context) {
        super(context);
    }

    public BackdropFrontLayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackdropFrontLayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BackdropFrontLayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public void setBehavior(BackdropFrontLayerBehavior behavior){
        this.behavior = behavior;
    }

    public void setButton(MaterialButton button){
        this.button = button;
    }

    public void setDrawable(AnimatedVectorDrawable drawable){
        this.drawable = drawable;
    }

    public void setDrawable2(AnimatedVectorDrawable drawable2){
        this.drawable2 = drawable2;
    }

    public void setImageView(ImageView imageView){
        this.imageView = imageView;
    }

    public void setTouchIntercept(int touchIntercept){
        if (touchIntercept == NONE || touchIntercept == FULL_LAYER )
            this.touchIntercept = touchIntercept;
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
        if (touchIntercept == FULL_LAYER && behavior != null && button != null && drawable != null &&
                (behavior.getState()== BottomSheetBehavior.STATE_COLLAPSED ||behavior.getState()== BottomSheetBehavior.STATE_HIDDEN)) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    button.setIcon(drawable);
                    drawable.start();

                    if(imageView != null && drawable2 != null) {
                        imageView.setImageDrawable(drawable2);
                        drawable2.start();

                    }
                }
            });
            return true;
        }
        return false;
    }


}








