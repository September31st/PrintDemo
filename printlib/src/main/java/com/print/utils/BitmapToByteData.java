//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class BitmapToByteData {
    public BitmapToByteData() {
    }

    public static byte[] rasterBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType, BitmapToByteData.AlignType alignType, int pagewidth) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > 1016) {
            width = 1016;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdata(pixels, width, height);
        int n = (width + 7) / 8;
        byte xL = (byte) (n % 256);
        byte xH = (byte) (n / 256);
        int x = (height + 23) / 24;
        ArrayList list = new ArrayList();
        byte[] head = new byte[]{29, 118, 48, (byte) m, xL, xH, 24, 0};
        int mL = 0;
        int mH = 0;
        if (width >= pagewidth) {
            alignType = BitmapToByteData.AlignType.Left;
        }

        switch (alignType) {
            case Left:
                mL = 0;
                mH = 0;
                break;
            case Center:
                mL = (pagewidth - width) / 2 % 256;
                mH = (pagewidth - width) / 2 / 256;
                break;
            case Right:
                mL = (pagewidth - width) % 256;
                mH = (pagewidth - width) / 256;
        }

        byte[] aligndata = DataForSendToPrinterPos80.setAbsolutePrintPosition(mL, mH);

        for (int byteData = 0; byteData < x; ++byteData) {
            byte[] newdata;
            if (byteData == x - 1) {
                if (height % 24 == 0) {
                    head[6] = 24;
                    newdata = new byte[n * 24];
                    System.arraycopy(data, 24 * byteData * n, newdata, 0, 24 * n);
                } else {
                    head[6] = (byte) (height % 24);
                    newdata = new byte[height % 24 * n];
                    System.arraycopy(data, 24 * byteData * n, newdata, 0, height % 24 * n);
                }
            } else {
                newdata = new byte[n * 24];
                System.arraycopy(data, 24 * byteData * n, newdata, 0, 24 * n);
            }

            byte i;
            int var22;
            int var23;
            byte[] var24;
            if (alignType != BitmapToByteData.AlignType.Left) {
                var24 = aligndata;
                var23 = aligndata.length;

                for (var22 = 0; var22 < var23; ++var22) {
                    i = var24[var22];
                    list.add(Byte.valueOf(i));
                }
            }

            var24 = head;
            var23 = head.length;

            for (var22 = 0; var22 < var23; ++var22) {
                i = var24[var22];
                list.add(Byte.valueOf(i));
            }

            var24 = newdata;
            var23 = newdata.length;

            for (var22 = 0; var22 < var23; ++var22) {
                i = var24[var22];
                list.add(Byte.valueOf(i));
            }
        }

        byte[] var25 = new byte[list.size()];

        for (int var26 = 0; var26 < var25.length; ++var26) {
            var25[var26] = ((Byte) list.get(var26)).byteValue();
        }

        return var25;
    }

    public static byte[] rasterBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > 1016) {
            width = 1016;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdata(pixels, width, height);
        int n = (width + 7) / 8;
        byte xL = (byte) (n % 256);
        byte xH = (byte) (n / 256);
        int x = (height + 23) / 24;
        ArrayList list = new ArrayList();
        byte[] head = new byte[]{29, 118, 48, (byte) m, xL, xH, 24, 0};

        for (int byteData = 0; byteData < x; ++byteData) {
            byte[] newdata;
            if (byteData == x - 1) {
                if (height % 24 == 0) {
                    head[6] = 24;
                    newdata = new byte[n * 24];
                    System.arraycopy(data, 24 * byteData * n, newdata, 0, 24 * n);
                } else {
                    head[6] = (byte) (height % 24);
                    newdata = new byte[height % 24 * n];
                    System.arraycopy(data, 24 * byteData * n, newdata, 0, height % 24 * n);
                }
            } else {
                newdata = new byte[n * 24];
                System.arraycopy(data, 24 * byteData * n, newdata, 0, 24 * n);
            }

            byte[] var19 = head;
            int var18 = head.length;

            byte i;
            int var17;
            for (var17 = 0; var17 < var18; ++var17) {
                i = var19[var17];
                list.add(Byte.valueOf(i));
            }

            var19 = newdata;
            var18 = newdata.length;

            for (var17 = 0; var17 < var18; ++var17) {
                i = var19[var17];
                list.add(Byte.valueOf(i));
            }
        }

        byte[] var20 = new byte[list.size()];

        for (int var21 = 0; var21 < var20.length; ++var21) {
            var20[var21] = ((Byte) list.get(var21)).byteValue();
        }

        return var20;
    }

    public static byte[] flashBmpToSendData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = convertBmp(mBitmap);
        bitmap = toGrayscale(bitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        if (n <= 1023 && h <= 288 && n != 0 && h != 0) {
            if (n * h >= 1023) {
                return new byte[0];
            } else {
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                byte[] data = getbmpdata(pixels, width, height);
                byte xL = (byte) (n % 256);
                byte xH = (byte) (n / 256);
                byte yL = (byte) (h % 256);
                byte yH = (byte) (h / 256);
                byte[] head = new byte[]{xL, xH, yL, yH};
                data = byteMerger(head, data);
                return data;
            }
        } else {
            return new byte[0];
        }
    }

    public static byte[] downLoadBmpToSendTSCdownloadcommand(Bitmap mBitmap) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pixels = new int[width * height];
        mBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] send = new byte[width * height];

        for (int data = 0; data < pixels.length; ++data) {
            send[data] = (byte) pixels[data];
        }

        byte[] var7 = getbmpdataTsc(pixels, width, height);
        return var7;
    }

    public static byte[] downLoadBmpToSendTSCData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        if (n <= 255 && h <= 48 && n != 0 && h != 0) {
            if (n * h > 912) {
                return new byte[0];
            } else {
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                byte[] data = getbmpdataTsc(pixels, width, height);
                return data;
            }
        } else {
            return new byte[0];
        }
    }

    public static byte[] downLoadBmpToSendData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = convertBmp(mBitmap);
        bitmap = toGrayscale(bitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        if (n <= 255 && h <= 48 && n != 0 && h != 0) {
            if (n * h > 912) {
                return new byte[0];
            } else {
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                byte[] data = getbmpdata(pixels, width, height);
                byte[] head = new byte[]{(byte) n, (byte) h};
                data = byteMerger(head, data);
                return data;
            }
        } else {
            return new byte[0];
        }
    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    private static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        double redSum = 0.0D;
        double total = (double) (width * height);

        int m;
        int mBitmap;
        int j;
        int grey;
        for (m = 0; m < height; ++m) {
            for (mBitmap = 0; mBitmap < width; ++mBitmap) {
                j = pixels[width * m + mBitmap];
                grey = (j & 16711680) >> 16;
                redSum += (double) grey;
            }
        }

        m = (int) (redSum / total);

        for (mBitmap = 0; mBitmap < height; ++mBitmap) {
            for (j = 0; j < width; ++j) {
                grey = pixels[width * mBitmap + j];
                int alpha1 = -16777216;
                int red = (grey & 16711680) >> 16;
                int green = (grey & '\uff00') >> 8;
                int blue = grey & 255;
                short var17;
                short var18;
                short var19;
                if (red >= m) {
                    var19 = 255;
                    var18 = 255;
                    var17 = 255;
                } else {
                    var19 = 0;
                    var18 = 0;
                    var17 = 0;
                }

                grey = alpha1 | var17 << 16 | var18 << 8 | var19;
                pixels[width * mBitmap + j] = grey;
            }
        }

        Bitmap var16 = Bitmap.createBitmap(width, height, Config.RGB_565);
        var16.setPixels(pixels, 0, width, 0, 0, width, height);
        return var16;
    }

    private static Bitmap convertGreyImgByFloyd(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray = new int[height * width];

        int e;
        int mBitmap;
        int j;
        int g;
        for (e = 0; e < height; ++e) {
            for (mBitmap = 0; mBitmap < width; ++mBitmap) {
                j = pixels[width * e + mBitmap];
                g = (j & 16711680) >> 16;
                gray[width * e + mBitmap] = g;
            }
        }

        boolean var9 = false;

        for (mBitmap = 0; mBitmap < height; ++mBitmap) {
            for (j = 0; j < width; ++j) {
                g = gray[width * mBitmap + j];
                if (g >= 128) {
                    pixels[width * mBitmap + j] = -1;
                    e = g - 255;
                } else {
                    pixels[width * mBitmap + j] = -16777216;
                    e = g - 0;
                }

                if (j < width - 1 && mBitmap < height - 1) {
                    gray[width * mBitmap + j + 1] += 3 * e / 8;
                    gray[width * (mBitmap + 1) + j] += 3 * e / 8;
                    gray[width * (mBitmap + 1) + j + 1] += e / 4;
                } else if (j == width - 1 && mBitmap < height - 1) {
                    gray[width * (mBitmap + 1) + j] += 3 * e / 8;
                } else if (j < width - 1 && mBitmap == height - 1) {
                    gray[width * mBitmap + j + 1] += e / 4;
                }
            }
        }

        Bitmap var10 = Bitmap.createBitmap(width, height, Config.RGB_565);
        var10.setPixels(pixels, 0, width, 0, 0, width, height);
        return var10;
    }

    private static byte[] bagetbmpdata(int[] b, int w, int m) {
        int nH = w / 256;
        int nL = w % 256;
        byte[] head = new byte[]{27, 42, (byte) m, (byte) nL, (byte) nH};
        byte[] end = new byte[]{27, 74, 16};
        byte mask = 1;
        byte[] perdata = new byte[w];

        int data;
        for (data = 0; data < w; ++data) {
            for (int y = 0; y < 8; ++y) {
                if ((b[y * w + data] & 16711680) >> 16 != 0) {
                    perdata[data] |= (byte) (mask << 7 - y);
                }
            }
        }

        for (data = 0; data < perdata.length; ++data) {
            perdata[data] = (byte) (~perdata[data]);
        }

        byte[] var11 = byteMerger(head, perdata);
        var11 = byteMerger(var11, end);
        return var11;
    }

    public static byte[] baBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Dithering:
                bitmap = convertGreyImg(bitmap);
                break;
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > 1016) {
            width = 1016;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int n = (height + 7) / 8;
        ArrayList list = new ArrayList();

        for (int newdata = 0; newdata < n; ++newdata) {
            int[] i = new int[width * 8];

            for (int data = 0; data < i.length; ++data) {
                if (data + 8 * newdata * width < pixels.length - 1) {
                    i[data] = pixels[data + 8 * newdata * width];
                } else {
                    i[data] = -1;
                }
            }

            byte[] var18 = bagetbmpdata(i, width, m);
            byte[] var15 = var18;
            int var14 = var18.length;

            for (int var13 = 0; var13 < var14; ++var13) {
                byte b = var15[var13];
                list.add(Byte.valueOf(b));
            }
        }

        byte[] var16 = new byte[list.size()];

        for (int var17 = 0; var17 < var16.length; ++var17) {
            var16[var17] = ((Byte) list.get(var17)).byteValue();
        }

        return var16;
    }

    private static byte[] getbmpdata(int[] b, int w, int h) {
        int n = (w + 7) / 8;
        byte[] data = new byte[n * h];
        byte mask = 1;

        int i;
        for (i = 0; i < h; ++i) {
            for (int x = 0; x < n * 8; ++x) {
                if (x < w) {
                    if ((b[i * w + x] & 16711680) >> 16 != 0) {
                        data[i * n + x / 8] |= (byte) (mask << 7 - x % 8);
                    }
                } else if (x >= w) {
                    data[i * n + x / 8] |= (byte) (mask << 7 - x % 8);
                }
            }
        }

        for (i = 0; i < data.length; ++i) {
            data[i] = (byte) (~data[i]);
        }

        return data;
    }

    private static byte[] getbmpdataTsc(int[] b, int w, int h) {
        int n = (w + 7) / 8;
        byte[] data = new byte[n * h];
        byte mask = 1;

        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < n * 8; ++x) {
                if (x < w) {
                    if ((b[y * w + x] & 16711680) >> 16 != 0) {
                        data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                    }
                } else if (x >= w) {
                    data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                }
            }
        }

        return data;
    }

    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private static Bitmap convertBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap convertBmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas cv = new Canvas(convertBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(-1.0F, 1.0F);
        matrix.postRotate(-90.0F);
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
        cv.drawBitmap(newBmp, new Rect(0, 0, newBmp.getWidth(), newBmp.getHeight()), new Rect(0, 0, w, h), (Paint) null);
        return convertBmp;
    }

    public static enum AlignType {
        Left,
        Center,
        Right;

        private AlignType() {
        }
    }

    public static enum BmpType {
        Dithering,
        Threshold;

        private BmpType() {
        }
    }
}
