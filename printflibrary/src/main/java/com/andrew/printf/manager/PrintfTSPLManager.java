package com.andrew.printf.manager;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;

public class PrintfTSPLManager {

    private Context context;
    private BluetoothManager bluetoothManager;

    static class PrintfLabelManagerHolder {
        private static PrintfTSPLManager instance = new PrintfTSPLManager();
    }

    public static PrintfTSPLManager getInstance(final Context context) {
        if (PrintfTSPLManager.PrintfLabelManagerHolder.instance.context == null) {
            PrintfTSPLManager.PrintfLabelManagerHolder.instance.context
                    = context.getApplicationContext();
            PrintfLabelManagerHolder.instance.bluetoothManager = BluetoothManager.getInstance(context);
        }
        return PrintfTSPLManager.PrintfLabelManagerHolder.instance;
    }

    private PrintfTSPLManager() {
    }

    /**
     * 发送数据
     * -1:数据发送失败 蓝牙未连接
     * 1:数据发送成功
     * -2:数据发送失败 抛出异常 失败
     */
    private int sendBytes(byte[] bytes) {
        int write = bluetoothManager.write(bytes);
        return write;
    }

    /**
     * 打印图片
     */
    public void printBitmap(int x, int y, Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        StringBuilder BITMAP = new StringBuilder()
                .append("BITMAP ")
                .append(x)
                .append(",")
                .append(y)
                .append(",")
                .append(width / 8)
                .append(",")
                .append(height)
                .append(",1,");
        sendBytes(BITMAP.toString().getBytes());
        byte[] bitmapData = convertToBMW(bitmap, 128);
        sendBytes(bitmapData);
        byte[] crlf = {0x0d, 0x0a};
        sendBytes(crlf);
    }

    /**
     * 开始打印
     *
     * @param number ： 张数
     */
    public void beginPrintf(int number) {
        if (number < 1) {
            number = 1;
        }
        String PRINT = "PRINT " + 1 + "," + number + "\r\n";
        sendBytes(PRINT.getBytes());
    }

    /**
     * 初始化画布
     *
     * @param width  标签的宽度
     * @param height 标签的高度
     */
    public void initCanvas(int width, int height) {
        byte[] data = new StringBuilder().append("SIZE ").append(width + " mm").append(",")
                .append(height + " mm").append("\r\n").toString().getBytes();
        sendBytes(data);
    }

    /**
     * 清除画布
     */
    public void clearCanvas() {
        byte[] bytes = "CLS\r\n".getBytes();
        sendBytes(bytes);
    }

    /**
     * 设置打印方向
     * 1：正
     * 0：默认
     *
     * @param direction
     */
    public void setDirection(int direction) {
        byte[] bytes = ("DIRECTION " + direction + "\r\n").getBytes();
        sendBytes(bytes);
    }

    /**
     * 打印文字
     *
     * @param x          文字x坐标
     * @param y          文字y坐标
     * @param fontWidth  字体宽度 1-10
     * @param fontHeight 字体高度1-10
     * @param content    文本内容
     */
    public void printText(int x, int y, int fontWidth, int fontHeight, String content) {
        byte[] bytes = new byte[0];
        try {
            bytes = ("TEXT " + x + "," + y + ",\"TSS24.BF2\",0," + fontWidth + "," + fontHeight +
                    ",\"" + content + "\"\r\n").getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendBytes(bytes);
    }

    /**
     * 打印条形码
     *
     * @param x                条形码x坐标
     * @param y                条形码y坐标
     * @param codeType         条码类型
     *                         CODE128: "128",
     *                         UPCA: "UPCA",
     *                         EAN13: "EAN13",
     *                         EAN8: "EAN8",
     *                         CODE39: "39",
     *                         CODABAR: "CODA",
     * @param height           条码高度
     * @param narrow            窄条码比例因子，可用于调整条码宽度
     * @param barcodeTextAlign 条码文字位置
     *                         NONE: 0,
     *                         LEFT: 1,
     *                         CENTER: 2,
     *                         RIGHT: 3,
     * @param rotation         旋转角度
     *                         ROTATION0: 0,
     *                         ROTATION90: 90,
     *                         ROTATION180: 180,
     *                         ROTATION270: 270
     * @param content          条码内容
     */
    public void printBarCode(int x, int y, String codeType, int height, int narrow, int barcodeTextAlign, int rotation, String content) {
        byte[] bytes = new byte[0];
        try {
            bytes = ("BARCODE " + x + "," + y + ",\"" + codeType + "\"," + height + "," + barcodeTextAlign + "," +
                    rotation +
                    "," + narrow + ",2,\"" + content + "\"\n").getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendBytes(bytes);
    }


    /**
     * @param x        二维码x坐标
     * @param y        二维码y坐标
     * @param level    LEVEL: {
     *                 L: "L",
     *                 M: "M",
     *                 Q: "Q",
     *                 H: "H"
     *                 },
     * @param width    宽
     * @param rotation 旋转
     *                 ROTATION0: 0,
     *                 ROTATION90: 90,
     *                 ROTATION180: 180,
     *                 ROTATION270: 270
     * @param content  二维码内容
     */
    public void printQrCode(int x, int y, String level, int width, int rotation, String content) {
        byte[] bytes = new byte[0];
        try {
            bytes = ("QRCODE " + x + "," + y + "," + level + "," + width + ",M," + rotation + ",M1,S1,\"" + content + "\"\n").getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendBytes(bytes);
    }


    /**
     * 图片二值化
     *
     * @param bmp
     * @return
     */
    public static byte[] convertToBMW(Bitmap bmp, int concentration) {
        if (concentration <= 0 || concentration >= 255) {
            concentration = 128;
        }
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        byte[] bytes = new byte[(width) / 8 * height];
        int[] p = new int[8];
        int n = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 8; j++) {
                for (int z = 0; z < 8; z++) {
                    int grey = bmp.getPixel(j * 8 + z, i);
                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    int gray = (int) (0.29900 * red + 0.58700 * green + 0.11400 * blue); // 灰度转化公式
                    if (gray <= concentration) {
                        gray = 0;//黑色
                    } else {
                        gray = 1;//白色

                    }
                    p[z] = gray;
                }
                byte value = (byte) (p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7]);
                bytes[width / 8 * i + j] = value;
            }
        }
        return bytes;
    }


}
