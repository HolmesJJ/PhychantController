package com.example.phychantcontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.phychantcontroller.utils.DrawUtils;
import com.example.phychantcontroller.utils.ToastUtils;
import com.example.phychantcontroller.websocket.WebSocketManager;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int SPEECH_INPUT = 1;
    private static final int EYE_CONTROL_ID = 1;
    private static final int GREETING_ID = 2;
    private static final int QNA_ID = 3;
    private static final int MUSCLE_CONTROL_ID = 4;

    private Button btnGreeting;
    private Button btnHow;
    private Button btnWhat;
    private Button btnTTS;
    private Button btnSend;
    private Button btnLeft;
    private Button btnRight;
    private EditText mEtText;
    private RelativeLayout mRlFullScreen;
    private TextView mTvXCoord;
    private TextView mTvYCoord;
    private TextView mTvHeight;
    private TextView mTvWidth;

    private Paint mPaint;
    private SurfaceView mOverlap;

    private int touchHeight = 0;
    private int touchWidth = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGreeting = (Button) findViewById(R.id.btn_greeting);
        btnHow = (Button) findViewById(R.id.btn_how);
        btnWhat = (Button) findViewById(R.id.btn_what);
        btnTTS = (Button) findViewById(R.id.btn_tts);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnRight = (Button) findViewById(R.id.btn_right);
        mEtText = (EditText) findViewById(R.id.et_text);
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
        btnGreeting.setOnClickListener(this);
        btnHow.setOnClickListener(this);
        btnWhat.setOnClickListener(this);
        btnTTS.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        mOverlap.setZOrderOnTop(true);
        mOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5);

        mRlFullScreen.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRlFullScreen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                touchHeight = mRlFullScreen.getHeight();
                touchWidth = mRlFullScreen.getWidth();
                mTvHeight.setText(String.valueOf(mRlFullScreen.getHeight()));
                mTvWidth.setText(String.valueOf(mRlFullScreen.getWidth()));
                ToastUtils.showShortSafe("Initialized successfully...");
            }
        });
        mRlFullScreen.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int id = view.getId();
        switch (id) {
            case R.id.rl_full_screen:
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
                break;
            default:
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
        JSONObject messageJSON = new JSONObject();
        JSONObject contentJSON = new JSONObject();
        try {
            messageJSON.put("id", EYE_CONTROL_ID);
            contentJSON.put("x", coordinate.getX());
            contentJSON.put("y", coordinate.getY());
            contentJSON.put("height", touchHeight);
            contentJSON.put("width", touchWidth);
            messageJSON.put("coordinate", contentJSON);
            if (WebSocketManager.getInstance().isServerStarted()) {
                WebSocketManager.getInstance().sendMessage(messageJSON.toString());
            }
        } catch (Exception e) {
            Log.i(TAG, "Send Exception: " + e);
        }
    }

    private void sendGreeting() {
        JSONObject messageJSON = new JSONObject();
        try {
            messageJSON.put("id", GREETING_ID);
            if (WebSocketManager.getInstance().isServerStarted()) {
                WebSocketManager.getInstance().sendMessage(messageJSON.toString());
            }
        } catch (Exception e) {
            Log.i(TAG, "Send Exception: " + e);
        }
    }

    private void sendQNA(String qna) {
        JSONObject messageJSON = new JSONObject();
        JSONObject contentJSON = new JSONObject();
        try {
            messageJSON.put("id", QNA_ID);
            contentJSON.put("qna", qna);
            messageJSON.put("content", contentJSON);
            if (WebSocketManager.getInstance().isServerStarted()) {
                WebSocketManager.getInstance().sendMessage(messageJSON.toString());
            }
        } catch (Exception e) {
            Log.i(TAG, "Send Exception: " + e);
        }
    }

    private void sendMuscle(boolean isLeft) {
        JSONObject messageJSON = new JSONObject();
        JSONObject contentJSON = new JSONObject();
        try {
            messageJSON.put("id", MUSCLE_CONTROL_ID);
            if (isLeft) {
                contentJSON.put("muscle", true);
            } else {
                contentJSON.put("muscle", false);
            }
            messageJSON.put("content", contentJSON);
            if (WebSocketManager.getInstance().isServerStarted()) {
                WebSocketManager.getInstance().sendMessage(messageJSON.toString());
            }
        } catch (Exception e) {
            Log.i(TAG, "Send Exception: " + e);
        }
    }

    private void speak() {
        Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_us");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something...");
        try {
            startActivityForResult(mSpeechRecognizerIntent, SPEECH_INPUT);
        } catch (Exception e) {
            ToastUtils.showShortSafe(e.getMessage());
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_greeting:
                sendGreeting();
                ToastUtils.showShortSafe("Greeting");
                break;
            case R.id.btn_how:
                sendQNA("Hi, how are you");
                ToastUtils.showShortSafe("Hi, how are you");
                break;
            case R.id.btn_what:
                sendQNA("Hi, what are you doing");
                ToastUtils.showShortSafe("Hi, what are you doing");
                break;
            case R.id.btn_tts:
                ToastUtils.showShortSafe("Speaking...");
                speak();
                break;
            case R.id.btn_send:
                if (mEtText.getText() != null) {
                    sendQNA(mEtText.getText().toString());
                    ToastUtils.showShortSafe(mEtText.getText().toString());
                    mEtText.setText("");
                }
                break;
            case R.id.btn_left:
                sendMuscle(true);
                ToastUtils.showShortSafe("Left");
                break;
            case R.id.btn_right:
                sendMuscle(false);
                ToastUtils.showShortSafe("Right");
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && results.size() > 0) {
                        sendQNA(results.get(0));
                        ToastUtils.showShortSafe(results.get(0));
                    }
                }
                break;
            default:
                break;
        }
    }
}