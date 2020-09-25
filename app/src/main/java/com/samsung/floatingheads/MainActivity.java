package com.samsung.floatingheads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // Request Service Code for Overlay Settings
    private static final int POPUP_DRAW_OVER_OTHER_APP_PERMISSION = 1001;

    private void initializeView() {
        findViewById(R.id.start_floating_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingPopupService.class));
                finish(); // Destroy Activity just activate it's service
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //I will ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            //Fire Intent to ask permission at runtime
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, POPUP_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        else{
            initializeView();
        }
    }


    // For Android P or greater, it is not mandatory for settings to return RESULT_OK on settings change
    // That's why explicit check is made for settings.canDrawOverlays additionally
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == POPUP_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK || Settings.canDrawOverlays(this)) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();
                finish(); // Destroy Activity without doing anything
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}