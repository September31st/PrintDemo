//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.print.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.print.asynncTask.PosAsyncTask;
import com.print.enums.PortType;
import com.print.enums.StatusCode;
import com.print.posprinterface.BackgroundInit;
import com.print.posprinterface.IMyBinder;
import com.print.posprinterface.ProcessData;
import com.print.posprinterface.UiExecute;
import com.print.utils.PosPrinterDev;
import com.print.utils.RoundQueue;

import java.lang.ref.SoftReference;
import java.util.List;

public class PosprinterService extends Service {
    private PosPrinterDev xPrinterDev;
    private PosPrinterDev.ReturnMessage mMsg;
    private boolean isConnected = false;
    private RoundQueue<byte[]> que;
    private IBinder myBinder = new MyBinder(this);

    public PosprinterService() {
    }

    private RoundQueue<byte[]> getinstaceRoundQueue() {
        if (que == null) {
            que = new RoundQueue(500);
        }

        return que;
    }

    public void onCreate() {
        super.onCreate();
        que = getinstaceRoundQueue();
        Log.i("TAG", "onCreate");
    }

    public IBinder onBind(Intent intent) {
        Log.i("TAG", "onBind");
        return myBinder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        if (xPrinterDev != null) {
            xPrinterDev.Close();
        }
    }

    public static class MyBinder extends Binder implements IMyBinder {
        SoftReference<PosprinterService> reference;

        public MyBinder(PosprinterService service) {
            reference = new SoftReference<>(service);
        }

        private boolean doinBackground(PosPrinterDev dev) {
            if (reference.get() == null) {
                return false;
            }
            reference.get().xPrinterDev = dev;
            reference.get().mMsg = reference.get().xPrinterDev.Open();
            if (reference.get().mMsg.GetErrorCode().equals(StatusCode.OpenPortSuccess)) {
                reference.get().isConnected = true;
                return true;
            } else {
                return false;
            }
        }

        /**
         * 联网
         *
         * @param ethernetIP
         * @param ethernetPort
         * @param execute
         */
        public void connectNetPort(final String ethernetIP, final int ethernetPort, UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    return doinBackground(new PosPrinterDev(PortType.Ethernet, ethernetIP, ethernetPort));
                }
            });
            task.execute();
        }

        /**
         * 连蓝牙
         *
         * @param bluetoothID
         * @param execute
         */
        public void connectBtPort(final String bluetoothID, UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    return doinBackground(new PosPrinterDev(PortType.Bluetooth, bluetoothID));
                }
            });
            task.execute();
        }

        /**
         * 连usb
         *
         * @param context
         * @param usbPathName
         * @param execute
         */
        public void connectUsbPort(final Context context, final String usbPathName, UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    return doinBackground(new PosPrinterDev(PortType.USB, context, usbPathName));
                }
            });
            task.execute();
        }

        public void disconnectCurrentPort(UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    if (reference.get() == null || reference.get().xPrinterDev == null) {
                        return false;
                    }
                    reference.get().mMsg = reference.get().xPrinterDev.Close();
                    if (reference.get().mMsg.GetErrorCode().equals(StatusCode.ClosePortSuccess)) {
                        reference.get().isConnected = false;
                        if (reference.get().que != null) {
                            reference.get().que.clear();
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });
            task.execute();
        }

        public void write(final byte[] data, UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    if (reference.get() == null) {
                        return false;
                    }
                    if (data != null) {
                        reference.get().mMsg = reference.get().xPrinterDev.Write(data);
                        if (reference.get().mMsg.GetErrorCode().equals(StatusCode.WriteDataSuccess)) {
                            reference.get().isConnected = true;
                            return true;
                        }

                        reference.get().isConnected = false;
                    }

                    return false;
                }
            });
            task.execute();
        }

        public void writeDataByYouself(UiExecute execute, final ProcessData processData) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    if (reference.get() == null) {
                        return false;
                    }
                    List list = processData.processDataBeforeSend();
                    if (list != null) {
                        for (int i = 0; i < list.size(); ++i) {
                            reference.get().mMsg = reference.get().xPrinterDev.Write((byte[]) list.get(i));
                        }

                        if (reference.get().mMsg.GetErrorCode().equals(StatusCode.WriteDataSuccess)) {
                            reference.get().isConnected = true;
                            return true;
                        }

                        reference.get().isConnected = false;
                    }

                    return false;
                }
            });
            task.execute();
        }

        public void acceptdatafromprinter(UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    if (reference.get() == null) {
                        return false;
                    }
                    reference.get().que = reference.get().getinstaceRoundQueue();
                    byte[] buffer = new byte[4];
                    reference.get().que.clear();
                    Log.i("TAG", reference.get().xPrinterDev.Read(buffer).GetErrorCode().toString());

                    for (; reference.get().xPrinterDev.Read(buffer).GetErrorCode().equals(StatusCode.ReadDataSuccess); reference.get().que.addLast(buffer)) {
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                            return false;
                        }
                    }

                    reference.get().isConnected = false;
                    return false;
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public RoundQueue<byte[]> readBuffer() {
            new RoundQueue(500);
            RoundQueue queue = reference.get().que;
            return queue;
        }

        public void clearBuffer() {
            reference.get().que.clear();
        }

        public void checkLinkedState(UiExecute execute) {
            PosAsyncTask task = new PosAsyncTask(execute, new BackgroundInit() {
                public boolean doInBackground() {
                    if (reference.get() == null) {
                        return false;
                    }
                    while (reference.get().isConnected) {
                        reference.get().isConnected = reference.get().xPrinterDev.GetPortInfo().PortIsOpen();
                    }

                    return false;
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }
}
