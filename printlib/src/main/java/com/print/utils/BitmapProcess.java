//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapProcess {
    public BitmapProcess() {
    }

    public static Bitmap compressBmpByPrinterWidth(Bitmap bitmap, BitmapProcess.PrinterWidth printerWidth) {
        Bitmap resizedBitmap = null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        boolean w = true;
        short w1;
        switch (printerWidth) {
            case Pos80:
                w1 = 576;
                break;
            case Pos76:
                w1 = 508;
                break;
            case Pos58:
                w1 = 384;
                break;
            default:
                w1 = 576;
        }

        if (width <= w1) {
            return bitmap;
        } else {
            int newHeight = height * w1 / width;
            float scaleWidth = (float) w1 / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        }
    }

    public static Bitmap compressBmpByYourWidth(Bitmap bitmap, int w) {
        Bitmap resizedBitmap = null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= w) {
            return bitmap;
        } else {
            int newHeight = height * w / width;
            float scaleWidth = (float) w / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        }
    }

    public static Bitmap rotateBmp(Bitmap bitmap, BitmapProcess.RotateType rotateType) {
        Matrix matrix = new Matrix();
        float degrees = 0.0F;
        switch (rotateType) {
            case Rotate90:
                degrees = 90.0F;
                break;
            case Rotate180:
                degrees = 180.0F;
                break;
            case Rotate270:
                degrees = 270.0F;
        }

        matrix.postRotate(degrees);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap2;
    }

    static enum PrinterWidth {
        Pos80,
        Pos76,
        Pos58;

        private PrinterWidth() {
        }
    }

    static enum RotateType {
        Rotate90,
        Rotate180,
        Rotate270;

        private RotateType() {
        }
    }
}
