package com.example.asus.sao;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate,View.OnClickListener {
    private static final String TAG = TestScanActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;
    private TextView open_flashlight;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        initView();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
//获取相应的二维码扫描结果的方法
    @Override
    public void onScanQRCodeSuccess(final String result) {
        Log.i(TAG, "result:" + result);

        if (TextUtils.isEmpty(result)) {
            Toast.makeText(TestScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestScanActivity.this);
            builder.setTitle("可能存在风险，是否连接?");
            builder.setMessage(result);
            builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(result.contains("http://")){
                        Intent intent=new Intent(TestScanActivity.this,MainActivity.class);
                        intent.putExtra("url",result);
                        startActivity(intent);
                    }else {
                        Toast.makeText(TestScanActivity.this, "不是网址", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();

        }

        vibrate();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }


    private void initView() {
        open_flashlight = (TextView) findViewById(R.id.open_flashlight);
        open_flashlight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_flashlight:
                if(open_flashlight.getText().toString().equals("开灯")){
                    open_flashlight.setText("关灯");
                    mQRCodeView.openFlashlight();

                }else{
                    open_flashlight.setText("开灯");
                    mQRCodeView.closeFlashlight();
                }
                break;
        }
    }

}