//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

public class DataForSendToPrinterTSC {
    private static String charsetName = "gbk";

    public DataForSendToPrinterTSC() {
    }

    public static void setCharsetName(String charset) {
        charsetName = charset;
    }

    public static byte[] sizeBymm(double m, double n) {
        String str = "SIZE " + m + " mm," + n + " mm\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] sizeByinch(double m, double n) {
        String str = "SIZE " + m + "," + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] sizeBydot(int m, int n) {
        String str = "SIZE " + m + " dot," + n + " dot\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] gapByinch(double m, double n) {
        String str = "GAP " + m + "," + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] gapBymm(double m, double n) {
        String str = "GAP " + m + " mm," + n + " mm\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] gapBydot(int m, int n) {
        String str = "GAP " + m + " dot," + n + " dot\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] gapDetect(int x, int y) {
        String str = "GAPDETECT " + x + "," + y + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] gapDetect() {
        String str = "GAPDETECT\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] blineDetect(int x, int y) {
        String str = "BLINEDETECT " + x + "," + y + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] autoDetect(int x, int y) {
        String str = "AUTODETECT " + x + "," + y + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] blineByinch(double m, double n) {
        String str = "BLINE " + m + "," + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] blineBymm(double m, double n) {
        String str = "BLINE " + m + " mm," + n + " mm\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] blineBydot(int m, int n) {
        String str = "BLINE " + m + " dot," + n + " dot\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] offSetByinch(double m) {
        String str = "OFFSET " + m + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] offSetBymm(double m) {
        String str = "OFFSET " + m + " mm\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] offSetBydot(int m) {
        String str = "OFFSET " + m + " dot\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] speed(double n) {
        String str = "SPEED " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] density(int n) {
        String str = "DENSITY " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] direction(int n) {
        String str = "DIRECTION " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] reference(int x, int y) {
        String str = "REFERENCE " + x + ", " + y + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] shift(int n) {
        String str = "SHIFT " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] country(String n) {
        String str = "COUNTRY " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] codePage(String n) {
        String str = "CODEPAGE " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] cls() {
        String str = "CLS\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] feed(int n) {
        String str = "FEED " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] backFeed(int n) {
        String str = "BACKFEED " + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] formFeed() {
        String str = "FORMFEED\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] home() {
        String str = "HOME\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] print(int m, int n) {
        String str = "PRINT " + m + "," + n + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] print(int m) {
        String str = "PRINT " + m + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] sound(int level, int interval) {
        String str = "SOUND " + level + "," + interval + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] cut() {
        String str = "CUT\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] limitFeedByinch(double n) {
        String str = "LIMITFEED n\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] limitFeedBymm(double n) {
        String str = "LIMITFEED n mm\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] limitFeedBydot(int n) {
        String str = "LIMITFEED n dot\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] selfTest() {
        String str = "SELFTEST\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] selfTest(String page) {
        String str = "SELFTEST " + page + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] eoj() {
        String str = "EOJ\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] delay(int ms) {
        String str = "DELAY " + ms + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] disPlay(String s) {
        String str = "DISPLAY " + s + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] initialPrinter() {
        String str = "INITIALPRINTER\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] bar(int x, int y, int width, int heigth) {
        String str = "BAR " + x + "," + y + "," + width + "," + heigth + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] barCode(int x, int y, String codeType, int heigth, int human, int rotation, int narrow, int wide, String content) {
        String str = "BARCODE " + x + "," + y + ",\"" + codeType + "\"," + heigth + "," + human + "," + rotation + "," + narrow + "," + wide + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] bitmap(int x, int y, int mode, Bitmap bitmap, BitmapToByteData.BmpType bmpType) {
        int width = (bitmap.getWidth() + 7) / 8;
        int heigth = bitmap.getHeight();
        String str = "BITMAP " + x + "," + y + "," + width + "," + heigth + "," + mode + ",";
        String end = "\n";
        byte[] ended = strTobytes(end);
        byte[] head = strTobytes(str);
        byte[] data = BitmapToByteData.downLoadBmpToSendTSCData(bitmap, bmpType);
        data = byteMerger(head, data);
        data = byteMerger(data, ended);
        return data;
    }

    public static byte[] box(int x, int y, int x_end, int y_end, int thickness) {
        String str = "BOX " + x + "," + y + "," + x_end + "," + y_end + "," + thickness + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] ellipse(int x, int y, int width, int height, int thickness) {
        String str = "ELLIPSE " + x + "," + y + "," + width + "," + height + "," + thickness + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] codeBlockFMode(int x, int y, int rotation, int row_height, int module_width, String content) {
        String str = "CODABLOCK " + x + "," + y + "," + rotation + "," + row_height + "," + module_width + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] dmatrix(int x, int y, int width, int height, int xm, int row, int col, String content) {
        String str = "DMATRIX " + x + "," + y + "," + width + "," + height + "," + xm + "," + row + "," + col + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] erase(int x, int y, int width, int height) {
        String str = "ERASE " + x + "," + y + "," + width + "," + height + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] pdf417(int x, int y, int width, int height, int rotate, String option, String content) {
        String str = "PDF417 " + x + "," + y + "," + width + "," + height + "," + rotate + "," + option + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] putBmp(int x, int y, String filename, int bpp, int contrast) {
        String str = "PUTBMP " + x + "," + y + ",\"" + filename + "\", " + bpp + ", " + contrast + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] putBmp(int x, int y, String filename) {
        String str = "PUTBMP " + x + "," + y + ",\"" + filename + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] putpcx(int x, int y, String filename) {
        String str = "PUTPCX " + x + "," + y + ",\"" + filename + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] qrCode(int x, int y, String eccLevel, int cellWidth, String mode, int rotation, String model, String mask, String content) {
        String str = "QRCODE " + x + "," + y + "," + eccLevel + "," + cellWidth + "," + mode + "," + rotation + "," + model + "," + mask + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] qrCode(int x, int y, String eccLevel, int cellWidth, String mode, int rotation, String content) {
        String str = "QRCODE " + x + "," + y + "," + eccLevel + "," + cellWidth + "," + mode + "," + rotation + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] reverse(int x, int y, int width, int height) {
        String str = "REVERSE " + x + "," + y + "," + width + "," + height + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] text(int x, int y, String font, int rotation, int x_multiplication, int y_multiplication, String content) {
        String str = "TEXT " + x + "," + y + ",\"" + font + "\"," + rotation + "," + x_multiplication + "," + y_multiplication + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] block(int x, int y, int width, int height, String font, int rotation, int x_multiplication, int y_multiplication, int space, int alignment, String content) {
        String str = "BLOCK " + x + "," + y + "," + width + "," + height + ",\"" + font + "\"," + rotation + "," + x_multiplication + "," + y_multiplication + "," + space + "," + alignment + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] block(int x, int y, int width, int height, String font, int rotation, int x_multiplication, int y_multiplication, String content) {
        String str = "BLOCK " + x + "," + y + "," + width + "," + height + ",\"" + font + "\"," + rotation + "," + x_multiplication + "," + y_multiplication + ",\"" + content + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] checkPrinterStateByPort9100() {
        byte[] data = new byte[]{29, 97, 31};
        return data;
    }

    public static byte[] checkPrinterStateByPort4000() {
        byte[] data = new byte[]{27, 118, 0};
        return data;
    }

    public static byte[] downLoad(String filename) {
        String str = "DOWNLOAD \"" + filename + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] downLoad(String filename, int size, String content) {
        String str = "DOWNLOAD \"" + filename + "\"," + size + "," + content + "\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] downLoad(String filename, String filepath) {
        byte[] data = null;

        try {
            File e = new File(filepath);
            FileInputStream fIn = new FileInputStream(e);
            int size = fIn.available();
            String str = "DOWNLOAD \"" + filename + "\"," + size + ",";
            data = strTobytes(str);
            byte[] b = new byte[size];

            for(boolean c = true; fIn.read(b) != -1; data = byteMerger(data, b)) {
                ;
            }

            fIn.close();
            String end = "\n";
            byte[] endata = strTobytes(end);
            data = byteMerger(data, endata);
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return data;
    }

    public static byte[] downLoad(String filename, Bitmap bitmap) {
        byte[] data = BitmapToByteData.downLoadBmpToSendTSCdownloadcommand(bitmap);
        int size = data.length;
        String str = "DOWNLOAD \"" + filename + "\"," + size + ",";
        byte[] head = strTobytes(str);
        data = byteMerger(head, data);
        byte[] end = strTobytes("\n");
        data = byteMerger(data, end);
        return data;
    }

    public static byte[] eop() {
        String str = "EOP\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] files() {
        String str = "FILES\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] kill(String filename) {
        String str = "KILL \"" + filename + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] move() {
        String str = "MOVE\n";
        byte[] data = strTobytes(str);
        return data;
    }

    public static byte[] run(String filename) {
        String str = "RUN \"" + filename + "\"\n";
        byte[] data = strTobytes(str);
        return data;
    }

    private static byte[] strTobytes(String str) {
        Object b = null;
        byte[] data = null;

        try {
            byte[] b1 = str.getBytes("utf-8");
            if(charsetName == null | charsetName == "") {
                charsetName = "gbk";
            }

            data = (new String(b1, "utf-8")).getBytes(charsetName);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return data;
    }

    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
