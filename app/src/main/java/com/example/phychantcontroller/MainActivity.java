package com.example.phychantcontroller;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phychantcontroller.utils.DrawUtils;
import com.example.phychantcontroller.utils.ScreenUtils;
import com.example.phychantcontroller.utils.ToastUtils;
import com.example.phychantcontroller.websocket.WebSocketManager;

public class MainActivity extends BaseActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    private RelativeLayout mRlFullScreen;
    private TextView mTvXCoord;
    private TextView mTvYCoord;
    private TextView mTvHeight;
    private TextView mTvWidth;

    private Paint mPaint;
    private SurfaceView mOverlap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRlFullScreen = (RelativeLayout) findViewById(R.id.rl_full_screen);
        mTvXCoord = (TextView) findViewById(R.id.tv_x_coord);
        mTvYCoord = (TextView) findViewById(R.id.tv_y_coord);
        mTvHeight = (TextView) findViewById(R.id.tv_height);
        mTvWidth = (TextView) findViewById(R.id.tv_width);
        mOverlap = (SurfaceView) findViewById(R.id.overlap_surface_view);
        init();

        if(!WebSocketManager.getInstance().isServerStarted()) {
            if (WebSocketManager.getInstance().start()) {
                ToastUtils.showShortSafe("Web socket started...");
            } else {
                ToastUtils.showShortSafe("Web socket error...");
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        mOverlap.setZOrderOnTop(true);
        mOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5);

        mTvHeight.setText(String.valueOf(ScreenUtils.getScreenRealHeight(this)));
        mTvWidth.setText(String.valueOf(ScreenUtils.getScreenRealWidth(this)));

        mRlFullScreen.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "touched down: (" + x + ", " + y + ")");
                mTvXCoord.setText(String.valueOf(x));
                mTvYCoord.setText(String.valueOf(y));
                drawCrossLine(x, y);
                sendCoordinate(new Coordinate(x, y));
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "moving: (" + x + ", " + y + ")");
                mTvXCoord.setText(String.valueOf(x));
                mTvYCoord.setText(String.valueOf(y));
                drawCrossLine(x, y);
                sendCoordinate(new Coordinate(x, y));
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "touched up: (" + x + ", " + y + ")");
                mTvXCoord.setText(String.valueOf(x));
                mTvYCoord.setText(String.valueOf(y));
                drawCrossLine(x, y);
                sendCoordinate(new Coordinate(x, y));
                break;
        }
        return true;
    }

    private void drawCrossLine(int x, int y) {
        Canvas canvas = mOverlap.getHolder().lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.save();
        DrawUtils.drawCrossLine(canvas, mPaint, x, y);
        canvas.restore();
        mOverlap.getHolder().unlockCanvasAndPost(canvas);
    }

    private void sendCoordinate(Coordinate coordinate) {
        if (WebSocketManager.getInstance().isServerStarted()) {
            WebSocketManager.getInstance().sendCoordinate(coordinate);
        }
    }

    @Override
    public void onDestroy() {
        if(WebSocketManager.getInstance().isServerStarted()) {
            if (WebSocketManager.getInstance().stop()) {
                ToastUtils.showShortSafe("Web socket stopped...");
            } else {
                ToastUtils.showShortSafe("Web socket error...");
            }
        }
        super.onDestroy();
    }
}