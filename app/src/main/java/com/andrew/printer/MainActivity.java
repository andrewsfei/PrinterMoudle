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

import com.andrew.printf.manager.BluetoothManager;
import com.andrew.printf.recycleview.BluetoothActivity;
import com.andrew.printf.utils.ToastUtils;

/***
 * 蓝牙打印
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Context context;

    Button btnMainConnectBlue, btnMainTestLabel,btnMainTestESCMLabel,btnMainTestUSBLabel;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_main);
        context = this;

        btnMainConnectBlue = findViewById(R.id.btn_main_connect_blue);
        btnMainTestLabel = findViewById(R.id.btn_main_test_label);
        btnMainTestESCMLabel = findViewById(R.id.btn_main_test_escm_label);
        btnMainTestUSBLabel = findViewById(R.id.btn_main_test_usb_label);

        //蓝牙连接结果回调
        BluetoothManager.getInstance(context)
                .addConnectResultCallBack(new BluetoothManager.ConnectResultCallBack() {
                    @Override
                    public void success(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接成功");
                        btnMainConnectBlue.setText("已连接：" + device.getName());
                        ToastUtils.ToastText(context, "蓝牙连接成功");
                    }

                    @Override
                    public void close(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接关闭");
                        btnMainConnectBlue.setText("连接蓝牙");
                        ToastUtils.ToastText(context, "蓝牙连接关闭");
                    }

                    @Override
                    public void fail(BluetoothDevice device) {
                        Log.e(TAG, "蓝牙连接失败");
                        btnMainConnectBlue.setText("连接蓝牙");
                        ToastUtils.ToastText(context, "蓝牙连接失败");
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

        //进入标签打印demo----TSPL
        btnMainTestLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothManager.getInstance(MainActivity.this).isConnect()) {
                    ToastUtils.ToastText(context, "请先连接蓝牙");
                    startActivity(new Intent(context, BluetoothActivity.class));
                    return;
                }
                Intent intent = new Intent(context, LabelActivity.class);
                startActivity(intent);
            }
        });

        //进入标签打印demo----ESCM
        btnMainTestESCMLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(context,ReceiptActivity.class);
                startActivity(intent);
            }
        });

        //进入标签打印demo----USB
        btnMainTestUSBLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,USBPrinterActivity.class);
                startActivity(intent);
            }
        });


        //默认连接上一次连接的蓝牙
        BluetoothManager.getInstance(this).connectLastBluetooth();
    }


}
