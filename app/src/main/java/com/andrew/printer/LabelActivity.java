package com.andrew.printer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;


import com.andrew.printf.manager.PrintfTSPLManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签打印demo页面
 */
public class LabelActivity extends Activity {
    List<String> list = new ArrayList<String>();
    String content = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        list.add("059#002#305#TI1901070017#2019-01-07#01001059#002#305#TI1901070017#2019-01-07#01001");
        list.add("888888");
        for (int i=0;i<list.size();i++){
            content = list.get(i);
        }

        for (String content:list) {
            content = content;
        }

        //打印文字（1mm=8px）
        findViewById(R.id.btn_label_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先获取一个打印TSPL指令的实例，用于打印内容
                PrintfTSPLManager instance = PrintfTSPLManager.getInstance(LabelActivity.this);
                //清除画板缓存，每打印一张新标签之前，都需要清除
                instance.clearCanvas();
                //定位标签纸大小（标签纸宽度和高度），单位mm
                instance.initCanvas(60, 30);
                //设置打印方向（0和1，一正一反）
                instance.setDirection(0);
                //打印文字，文字的坐标定位采用坐标轴，坐标在标签纸上的计算单位转换是，1mm=8px
                instance.printText(0, 0, 1, 1, "1234567890abcdefghijklmnopqrstuvwxyz");
                instance.printText(0, 32, 1, 1, "希望是附丽于存在的，有存在，便有希望.");
                //开始打印（参数是打印张数）
                instance.beginPrintf(1);
            }
        });

        //打印条形码（1mm=8px）
        findViewById(R.id.btn_label_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfTSPLManager instance = PrintfTSPLManager.getInstance(LabelActivity.this);
                instance.clearCanvas();
                instance.initCanvas(60, 30);
                instance.setDirection(0);
                //打印条形码
                instance.printBarCode(0, 0, "128", 48, 3, 2, 0, "123456");
                instance.beginPrintf(1);
            }
        });

        //打印二维码（1mm=8px）
        findViewById(R.id.btn_label_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfTSPLManager instance = PrintfTSPLManager.getInstance(LabelActivity.this);
                instance.clearCanvas();
                instance.initCanvas(40, 30);
                instance.setDirection(1);

                //打印二维码
                instance.printQrCode(50, 30, "L", 6, 0, "059#002#305#TI1901070017#2019-01-07#01001059#002#305#TI1901070017#2019-01-07#01001");
//                instance.printText(0, 190, 1, 1, "特征特征");
                instance.beginPrintf(1);
            }
        });

        //打印图片
        findViewById(R.id.btn_label_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfTSPLManager instance = PrintfTSPLManager.getInstance(LabelActivity.this);
                instance.clearCanvas();
                instance.initCanvas(40, 30);
                instance.setDirection(0);
                //bitmap的大小不能超过标签纸大小，根据1mm=8px来计算，如果超过，会导致bitmap打印不全，所以需要重置大小
                Bitmap bitmap = handleBitmap(40 * 8, 30 * 8, decodeResource(getResources(), R.mipmap.p_one_six));
                //打印图片
                instance.printBitmap(0, 0, bitmap);
                instance.beginPrintf(1);
            }
        });

        //打印案例
        findViewById(R.id.btn_label_test_example).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrintfTSPLManager instance = PrintfTSPLManager.getInstance(LabelActivity.this);
                instance.clearCanvas();
                instance.initCanvas(40, 30);
                instance.setDirection(1);//1为正向
/*                instance.printText(88, 0, 1, 2, "福建省食用农产品合格证（散户）");
                instance.printText(0, 50, 1, 1, "我承诺");
                instance.printText(0, 80, 1, 1, "√ 没有使用禁用农药兽药");
                instance.printBarCode(0, 114, "128", 48, 3, 2, 0, "123456");*/


                instance.printText(0, 20, 1, 1, "品号品号品号品号品号品号");
                instance.printText(0, 50, 1, 1, "特征特征");
                instance.printText(0, 80, 1, 1, "条码测试条码测试条码测试");
                instance.printBarCode(0, 114, "128", 10 * 8, 1, 1, 0, "059#002#305#TI1901070017#2019-01-07#01001");
                instance.beginPrintf(1);
            }
        });
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 处理图片的大小
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    private Bitmap handleBitmap(float width, float height, Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = width / w;
        float scaleH = height / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

}