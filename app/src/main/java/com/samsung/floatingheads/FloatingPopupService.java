package com.samsung.floatingheads;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatingPopupService extends Service {
    private View mFloatingView;
    private WindowManager mWindowManager;
    private static final String TAG = "FloatingPopupService";

    public FloatingPopupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        // Inflate the floating view created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_note_widget , null);

        // Provide a parameter instantiator for window to populate my view
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the window parameters using instatiator
        params.gravity = Gravity.TOP | Gravity.LEFT;
        //Initially view will be added to top-left corner

        //Bind the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        // Bind all Elements from XML to Java
        View default_view = mFloatingView.findViewById(R.id.complete_bubble);

        TextView objectName = (TextView) default_view.findViewById(R.id.object_name_text);

        //Dynamically Setting properties for objectName
        objectName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        objectName.setSingleLine(true);
        objectName.setSelected(true);
        objectName.setMarqueeRepeatLimit(-1);
        // Retrieve and set objectName
        // TODO: objectName.setText(Object Name from Camera)

        ImageView closeButton = (ImageView) default_view.findViewById(R.id.click_to_close);

        ImageView objectCircle = (ImageView) default_view.findViewById(R.id.object_name_circle);

        final TextView noteText = (TextView) default_view.findViewById(R.id.note_name_text);
        // Dynamically set NoteText Property
        noteText.setVisibility(View.GONE);
        // Retrieve and set note Description
        //TODO : noteText.setText (Note Text from SmartNotes)

        //OnClickListener for closeButton
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf(); //Self Destruct Service
            }
        });

        //OnClickListener for ObjectCircle
        objectCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onTouch: I have reached onTouch");
                // Logic to expand or contract Chat Bubble
                if (noteText.getVisibility() == View.GONE){
                    noteText.setVisibility(View.VISIBLE);
                }
                else{
                    noteText.setVisibility(View.GONE);
                }
            }
        });

        //OnClickListener for Text Description (Go to Notes App, Empty for Now)
        noteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Either Create New Note [If no note found]
                //  OR Open Existing Note in Note Editor Activity with key filled as Object name
            }
        });

        //Drag Logic for Object Circle on Touch
        objectCircle.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: I have reached onTouch");
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // Initial Position of view
                        initialX = params.x;
                        initialY = params.y;

                        // Your Touch Coordinates Location
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Get New Coordinates of window
                        params.x = initialX + (int)(motionEvent.getRawX()-initialTouchX);
                        params.y = initialY + (int)(motionEvent.getRawY()-initialTouchY);

                        // Update window layout
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (motionEvent.getRawX() - initialTouchX);
                        int Ydiff = (int) (motionEvent.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10){
                            if (noteText.getVisibility() == View.VISIBLE){
                                noteText.setVisibility(View.GONE);
                            }
                            else{
                                noteText.setVisibility(View.VISIBLE);
                            }
                        }
                }
                return false;
            }
        });
    }

    // On Destroy of service please do the following
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}