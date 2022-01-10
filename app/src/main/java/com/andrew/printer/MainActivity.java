package com.andrew.printer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrew.printf.manager.BluetoothManager;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Context context;

    Button btnMainConnectBlue, btnMainTestLabel;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_main);
        context = this;

        btnMainConnectBlue = findViewById(R.id.btn_main_connect_blue);
        btnMainTestLabel = findViewById(R.id.btn_main_test_label);
        //蓝牙连接结果回调
        BluetoothManager.getInstance(context)
                .addConnectResultCallBack(new BluetoothManager.ConnectResultCallBack() {
                    @Override
                    public void success(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接成功");
                        btnMainConnectBlue.setText("已连接：" + device.getName());
                        Toast.makeText(context, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void close(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接关闭");
                        btnMainConnectBlue.setText("连接蓝牙");
                        Toast.makeText(context, "蓝牙连接关闭", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void fail(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接失败");
                        btnMainConnectBlue.setText("连接蓝牙");
                        Toast.makeText(context, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                    }
                });

        //进入蓝牙列表（搜索蓝牙，连接蓝牙）
        btnMainConnectBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BluetoothActivity.class);
                startActivity(intent);
            }
        });

        //进入标签打印demo
        btnMainTestLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothManager.getInstance(MainActivity.this).isConnect()) {
                    Toast.makeText(context, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, BluetoothActivity.class));
                    return;
                }
                Intent intent = new Intent(context, LabelActivity.class);
                startActivity(intent);
            }
        });

        //默认连接上一次连接的蓝牙
        BluetoothManager.getInstance(this).connectLastBluetooth();
    }

}
