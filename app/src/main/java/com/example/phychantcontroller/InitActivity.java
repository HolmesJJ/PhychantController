package com.example.phychantcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.phychantcontroller.utils.ContextUtils;
import com.example.phychantcontroller.utils.NetUtils;
import com.example.phychantcontroller.utils.PermissionsUtils;

import pub.devrel.easypermissions.AfterPermissionGranted;

public class InitActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnStart;
    private Button mBtnGetIP;
    private TextView mTvIP;

    private static final int REC_PERMISSION = 100;
    String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CHANGE_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        ContextUtils.init(this);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnGetIP = (Button) findViewById(R.id.btn_get_ip);
        mTvIP = (TextView) findViewById(R.id.tv_ip);
        mBtnStart.setOnClickListener(this);
        mBtnGetIP.setOnClickListener(this);
        requestPermission();
    }

    @AfterPermissionGranted(REC_PERMISSION)
    private void requestPermission() {
        mBtnStart.setEnabled(false);
        PermissionsUtils.doSomeThingWithPermission(this, () -> {
            mBtnStart.setEnabled(true);
        }, PERMISSIONS, REC_PERMISSION, R.string.rationale_init);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_start:
                Intent myIntent = new Intent(this, MainActivity.class);
                this.startActivity(myIntent);
                break;
            case R.id.btn_get_ip:
                mTvIP.setText(String.format("%s:50050", NetUtils.getHostIP()));
                break;
            default:
                break;
        }
    }
}