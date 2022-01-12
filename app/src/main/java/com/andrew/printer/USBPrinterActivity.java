package com.andrew.printer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrew.printf.manager.USBPrinterManager;


public class USBPrinterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_test);

        findViewById(R.id.btn_usb_printf_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                USBPrinterManager usbPrinter = USBPrinterManager.getInstance();
                usbPrinter.initPrinter(USBPrinterActivity.this);
                usbPrinter.bold(true);
                usbPrinter.setTextSize(3);
                usbPrinter.setAlign(1);
                usbPrinter.printTextNewLine("tableName");
                usbPrinter.printLine(1);
                usbPrinter.bold(false);
                usbPrinter.printTextNewLine("品号：" + "PA00000001");
                usbPrinter.printTextNewLine("特征：" + "出库单");
                usbPrinter.printBarCode("6936983800013");
                usbPrinter.printLine(5);
                usbPrinter.cutPager();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBPrinterManager.getInstance().close();
    }
}
