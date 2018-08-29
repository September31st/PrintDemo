//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.posprinterface;

import android.content.Context;

import com.print.utils.RoundQueue;


public interface IMyBinder {
    void connectNetPort(String var1, int var2, UiExecute var3);

    void connectBtPort(String var1, UiExecute var2);

    void connectUsbPort(Context var1, String var2, UiExecute var3);

    void disconnectCurrentPort(UiExecute var1);

    void acceptdatafromprinter(UiExecute var1);

    RoundQueue<byte[]> readBuffer();

    void clearBuffer();

    void checkLinkedState(UiExecute var1);

    void write(byte[] var1, UiExecute var2);

    void writeDataByYouself(UiExecute var1, ProcessData var2);
}
