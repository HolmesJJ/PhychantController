package com.example.phychantcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    private RelativeLayout mRlFullScreen;
    private TextView mTvXCoord;
    private TextView mTvYCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRlFullScreen = (RelativeLayout) findViewById(R.id.rl_full_screen);
        mTvXCoord = (TextView) findViewById(R.id.tv_x_coord);
        mTvYCoord = (TextView) findViewById(R.id.tv_y_coord);
        mRlFullScreen.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "touched down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "moving: (" + x + ", " + y + ")");
                mTvXCoord.setText(String.valueOf(x));
                mTvYCoord.setText(String.valueOf(y));
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "touched up");
                break;
        }

        return true;
    }
}