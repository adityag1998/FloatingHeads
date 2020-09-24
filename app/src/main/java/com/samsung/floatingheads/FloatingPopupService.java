package com.samsung.floatingheads;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    public void onCreate() {
        super.onCreate();
        // Inflate the floating view created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget , null);

        // Provide a parameter instantiate for window to view to populate
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;   //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        // Bind my NoteBubble (default_view : behaviour of NOTE DESCRIPTION = OFF)
        // [Bubble + Object Name Text + Small Close Button + Note Desc(OFF by default)]
        // ALso bind its various sub elements
        View default_view = mFloatingView.findViewById(R.id.complete_bubble);

        TextView objectName = (TextView) default_view.findViewById(R.id.object_name_text);
        objectName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        objectName.setSingleLine(true);
        objectName.setSelected(true);
        objectName.setMarqueeRepeatLimit(-1);

        ImageView closeButton = (ImageView) default_view.findViewById(R.id.click_to_close);

        ImageView objectCircle = (ImageView) default_view.findViewById(R.id.object_name_circle);

        final TextView noteText = (TextView) default_view.findViewById(R.id.note_name_text);

        // Retrieve and set objectName
        //TODO: objectName.setText(Object Name from Camera)

        // Retrieve and set note Description
        //TODO : noteText.setText (Asynchronus Task from DB Fetch Query)

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
                // TODO: Either Create New Note or Open Existing
            }
        });

        //Drag Logic for Complete Bubble on Touch
        default_view.setOnTouchListener(new LinearLayout.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }


}
