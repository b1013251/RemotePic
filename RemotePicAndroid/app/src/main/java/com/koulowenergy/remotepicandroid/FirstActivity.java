package com.koulowenergy.remotepicandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.content.ContentValues.TAG;

public class FirstActivity extends Activity implements View.OnClickListener {

    Button buttonToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Button
        buttonToMain = (Button)findViewById(R.id.buttonToMain);
        buttonToMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ( v != null) {
            Log.d(TAG, "Clicked");
            Intent intent = new Intent();
            intent.setClass(this.getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
