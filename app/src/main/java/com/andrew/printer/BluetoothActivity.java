package com.andrew.printer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.andrew.printf.manager.BluetoothManager;
import com.andrew.printf.model.BluetoothModel;
import com.andrew.printf.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印机设备，蓝牙搜索页面
 */
public class BluetoothActivity extends Activity {

    RecyclerView rvBluetoothShowList;

    Context context;

    private List<BluetoothModel> bluetoothModels = new ArrayList<>();

    private BluetoothRecyclerViewAdapter recyclerShowAdapter;

    /**
     * 扫描到蓝牙的回调（添加蓝牙设备到列表）
     */
    BluetoothManager.ScanBlueCallBack scanBlueCallBack = new BluetoothManager.ScanBlueCallBack() {
        @Override
        public void scanDevice(BluetoothModel bluetoothModel) {
            bluetoothModels.add(bluetoothModel);
            recyclerShowAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 连接蓝牙结果的回调
     */
    BluetoothManager.ConnectResultCallBack connectResultCallBack = new BluetoothManager.ConnectResultCallBack() {
        @Override
        public void success(BluetoothDevice device) {
            showToast("蓝牙连接成功");
            BluetoothActivity.this.finish();
        }

        @Override
        public void close(BluetoothDevice device) {
            showToast("蓝牙关闭");
        }

        @Override
        public void fail(BluetoothDevice device) {
            showToast("蓝牙连接失败");
        }
    };

    private void showToast(String msg) {
        Toast.makeText(BluetoothActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        context = this;

        rvBluetoothShowList = findViewById(R.id.rv_bluetooth_show_list);
        //添加Android自带的分割线
        rvBluetoothShowList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerShowAdapter = new BluetoothRecyclerViewAdapter(this, bluetoothModels);
        rvBluetoothShowList.setAdapter(recyclerShowAdapter);
        rvBluetoothShowList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //检查是否开启了定位权限（安卓6.0以上，搜索蓝牙需要定位权限）
        if (PermissionUtil.checkLocationPermission(this)) {
            //添加扫描到蓝牙回调
            BluetoothManager.getInstance(this).addScanBlueCallBack(scanBlueCallBack);
            //添加连接蓝牙结果回调
            BluetoothManager.getInstance(this).addConnectResultCallBack(connectResultCallBack);
            //开始搜索蓝牙
            int i = BluetoothManager.getInstance(this).beginSearch();
            //蓝牙适配器未打开
            if (i == 2) {
                BluetoothManager.getInstance(this)
                        .openBluetoothAdapter(BluetoothActivity.this, 101);
            }
        }
        //点击蓝牙列表中的item
        recyclerShowAdapter.setOnClickItemLister(new BluetoothRecyclerViewAdapter.OnClickItemLister() {
            @Override
            public void onClick(View view, int position) {
                String bluetoothMac = bluetoothModels.get(position).getBluetoothMac();
                //配对连接蓝牙（打印机设备默认配对码：0000）
                BluetoothManager.getInstance(BluetoothActivity.this).pairBluetooth(bluetoothMac);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //定位权限申请结果回调
        if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //添加扫描到蓝牙回调
                BluetoothManager.getInstance(this).addScanBlueCallBack(scanBlueCallBack);
                //添加连接蓝牙结果回调
                BluetoothManager.getInstance(this).addConnectResultCallBack(connectResultCallBack);
                int i = BluetoothManager.getInstance(this).beginSearch();
                if (i == 2) {
                    BluetoothManager.getInstance(this)
                            .openBluetoothAdapter(BluetoothActivity.this, 101);
                }
                //连接上一次的蓝牙
                BluetoothManager.getInstance(this).connectLastBluetooth();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开蓝牙适配器结果回调
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            BluetoothManager.getInstance(this).beginSearch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroyonDestroy===","进来。。。");
        BluetoothManager.getInstance(BluetoothActivity.this)
                .removeScanBlueCallBack(scanBlueCallBack);
        BluetoothManager.getInstance(BluetoothActivity.this)
                .removeConnectResultCallBack(connectResultCallBack);
        BluetoothManager.getInstance(BluetoothActivity.this)
                .stopSearch();
    }
}
