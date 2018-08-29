
package com.print.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.print.enums.PortType;
import com.print.enums.StatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class PosPrinterDev {
    private PortInfo mPortInfo = new PortInfo();
    private PrinterPort mPort = null;

    public PosPrinterDev(PortType portType, Context context) {
        mPortInfo.mPortType = portType;
        mPortInfo.mContext = context;
    }

    public PosPrinterDev(PortType portType, Context context, String usbPathName) {
        mPortInfo.mPortType = portType;
        mPortInfo.mContext = context;
        if (!usbPathName.equals("") && usbPathName != null) {
            mPortInfo.mUsbPathName = usbPathName;
        }
    }

    public PosPrinterDev(PortType portType, String bluetoothID) {
        mPortInfo.mPortType = portType;
        mPortInfo.mBluetoothID = bluetoothID;
    }

    public PosPrinterDev(PortType portType, String ethernetIP, int ethernetPort) {
        mPortInfo.mPortType = portType;
        mPortInfo.mEthernetIP = ethernetIP;
        mPortInfo.mEthernetPort = ethernetPort;
    }

    private void ResetPar() {
        if (mPortInfo != null) {
            mPortInfo = null;
        }

        mPortInfo = new PortInfo();
        if (mPort != null) {
            mPort.ClosePort();
            mPort = null;
        }

    }

    public ReturnMessage Open() {
        ReturnMessage retVar = null;
        switch (mPortInfo.mPortType) {
            case USB:
                if (mPortInfo.mUsbPathName.equals("")) {
                    retVar = Open(mPortInfo.mPortType, mPortInfo.mContext);
                } else {
                    retVar = Open(mPortInfo.mPortType, mPortInfo.mContext, mPortInfo.mUsbPathName);
                }
                break;
            case Bluetooth:
                retVar = Open(mPortInfo.mPortType, mPortInfo.mBluetoothID);
                break;
            case Ethernet:
                retVar = Open(mPortInfo.mPortType, mPortInfo.mEthernetIP, mPortInfo.mEthernetPort);
                break;
            default:
                retVar = new ReturnMessage();
        }

        return retVar;
    }

    private ReturnMessage Open(PortType portType, Context context) {
        ResetPar();
        if (portType != PortType.USB) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Port type wrong !\n");
        } else if (context == null) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Context is null !\n");
        } else {
            mPortInfo.mContext = context;
            mPortInfo.mPortType = PortType.USB;
            mPortInfo.mUsbPathName = "";
            mPort = new UsbPort(mPortInfo);
            return mPort.OpenPort();
        }
    }

    private ReturnMessage Open(PortType portType, Context context, String usbPathName) {
        ResetPar();
        if (portType != PortType.USB) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Port type wrong !\n");
        } else if (context == null) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Context is null !\n");
        } else if (usbPathName == null) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "usbPathName is null !\n");
        } else {
            mPortInfo.mContext = context;
            mPortInfo.mPortType = PortType.USB;
            mPortInfo.mUsbPathName = usbPathName;
            mPort = new UsbPort(mPortInfo);
            return mPort.OpenPort();
        }
    }

    private ReturnMessage Open(PortType portType, String bluetoothID) {
        ResetPar();
        if (portType != PortType.Bluetooth) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Port type wrong !\n");
        } else if (!BluetoothAdapter.checkBluetoothAddress(bluetoothID)) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "BluetoothID wrong !\n");
        } else {
            mPortInfo.mBluetoothID = bluetoothID;
            mPortInfo.mPortType = PortType.Bluetooth;
            mPort = new BluetoothPort(mPortInfo);
            return mPort.OpenPort();
        }
    }

    private ReturnMessage Open(PortType portType, String ethernetIP, int ethernetPort) {
        ResetPar();
        if (portType != PortType.Ethernet) {
            return new ReturnMessage(StatusCode.OpenPortFailed, "Port type wrong !\n", 0);
        } else {
            try {
                Inet4Address.getByName(ethernetIP);
            } catch (Exception var5) {
                return new ReturnMessage(StatusCode.OpenPortFailed, "Ethernet ip wrong !\n", 0);
            }

            if (ethernetPort <= 0) {
                return new ReturnMessage(StatusCode.OpenPortFailed, "Ethernet port wrong !\n", 0);
            } else {
                mPortInfo.mEthernetPort = ethernetPort;
                mPortInfo.mEthernetIP = ethernetIP;
                mPortInfo.mPortType = PortType.Ethernet;
                mPort = new EthernetPort(mPortInfo);
                NetConnectThread connectNet = new NetConnectThread(mPort);
                connectNet.start();

                while (connectNet.returnMessage == null) {
                    ;
                }

                return connectNet.returnMessage;
            }
        }
    }

    public ReturnMessage Write(int data) {
        data &= 255;
        return mPort.Write(data);
    }

    public ReturnMessage Write(byte[] data) {
        return mPort.Write(data);
    }

    public ReturnMessage Write(byte[] data, int offset, int count) {
        return mPort.Write(data, offset, count);
    }

    public ReturnMessage Read(byte[] buffer) {
        return Read(buffer, 0, buffer.length);
    }

    public ReturnMessage Read(byte[] buffer, int offset, int count) {
        ReturnMessage retvar = null;
        switch (mPort.mPortInfo.mPortType) {
            case Ethernet:
                NetReadThread netReadThread = new NetReadThread(mPort, buffer, offset, count);
                netReadThread.start();

                while (netReadThread.returnMessage == null) {
                    ;
                }

                retvar = netReadThread.returnMessage;
                break;
            default:
                retvar = mPort.Read(buffer, offset, count);
        }

        return retvar;
    }

    public int Read() {
        byte[] tem = new byte[1];
        return Read(tem, 0, 1).mStatusCode == StatusCode.ReadDataSuccess ? tem[0] : -1;
    }

    public synchronized ReturnMessage Close() {
        return mPort == null ? new ReturnMessage(StatusCode.ClosePortFailed, "Not opened port !", 0) : mPort.ClosePort();
    }

    public PortInfo GetPortInfo() {
        mPortInfo.mIsOpened = mPort.PortIsOpen();
        return mPortInfo;
    }

    private class BluetoothPort extends PrinterPort {
        private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private BluetoothAdapter mBtAdapter = null;
        private BluetoothDevice mBtDevice = null;
        private BluetoothSocket mBtSocket = null;
        private OutputStream mOutPut = null;
        private InputStream mInPut = null;

        public BluetoothPort(PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType == PortType.Bluetooth && BluetoothAdapter.checkBluetoothAddress(portInfo.mBluetoothID)) {
                mPortInfo.mParIsOK = true;
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            } else {
                mPortInfo.mParIsOK = false;
            }
        }

        @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN})
        ReturnMessage OpenPort() {
            if (!mPortInfo.mParIsOK) {
                return new ReturnMessage(StatusCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                try {
                    if (mBtAdapter == null) {
                        return new ReturnMessage(StatusCode.OpenPortFailed, "Not Bluetooth adapter !\n", 0);
                    }

                    if (!mBtAdapter.isEnabled()) {
                        return new ReturnMessage(StatusCode.OpenPortFailed, "Bluetooth adapter was closed !\n", 0);
                    }

                    mBtAdapter.cancelDiscovery();
                    mBtDevice = mBtAdapter.getRemoteDevice(mPortInfo.mBluetoothID);
                    mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                    mBtSocket.connect();
                    mOutPut = null;
                    mOutPut = mBtSocket.getOutputStream();
                    mInPut = null;
                    mInPut = mBtSocket.getInputStream();
                    mIsOpen = true;
                } catch (Exception var2) {
                    return new ReturnMessage(StatusCode.OpenPortFailed, var2.toString(), 0);
                }

                return new ReturnMessage(StatusCode.OpenPortSuccess, "Open bluetooth port success !\n", 0);
            }
        }

        ReturnMessage ClosePort() {
            try {
                if (mOutPut != null) {
                    mOutPut.flush();
                }

                if (mBtSocket != null) {
                    mBtSocket.close();
                }

                mIsOpen = false;
                mOutPut = null;
                mInPut = null;
            } catch (Exception var2) {
                return new ReturnMessage(StatusCode.ClosePortFailed, var2.toString(), 0);
            }

            return new ReturnMessage(StatusCode.ClosePortSuccess, "Close bluetooth port success !\n", 0);
        }

        ReturnMessage Write(int data) {
            if (mIsOpen && mBtSocket.isConnected() && mOutPut != null) {
                try {
                    mOutPut.write(data);
                } catch (Exception var3) {
                    ClosePort();
                    return new ReturnMessage(StatusCode.WriteDataFailed, var3.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send 1 byte .\n", 1);
            } else {
                return new ReturnMessage(StatusCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        ReturnMessage Write(byte[] data) {
            if (mIsOpen && mBtSocket.isConnected() && mOutPut != null) {
                try {
                    mOutPut.write(data);
                } catch (Exception var3) {
                    ClosePort();
                    return new ReturnMessage(StatusCode.WriteDataFailed, var3.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
            } else {
                return new ReturnMessage(StatusCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        ReturnMessage Write(byte[] data, int offset, int count) {
            if (mIsOpen && mBtSocket.isConnected() && mOutPut != null) {
                try {
                    mOutPut.write(data, offset, count);
                } catch (Exception var5) {
                    return new ReturnMessage(StatusCode.WriteDataFailed, var5.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
            } else {
                ClosePort();
                return new ReturnMessage(StatusCode.WriteDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        ReturnMessage Read(byte[] buffer, int offset, int count) {
            boolean readBytes = true;
            if (mIsOpen && mBtSocket.isConnected() && mInPut != null) {
                int readBytes1;
                try {
                    readBytes1 = mInPut.read(buffer, offset, count);
                } catch (Exception var6) {
                    return new ReturnMessage(StatusCode.ReadDataFailed, var6.toString(), 0);
                }

                return new ReturnMessage(StatusCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes1);
            } else {
                ClosePort();
                return new ReturnMessage(StatusCode.ReadDataFailed, "bluetooth port was close !\n", 0);
            }
        }

        ReturnMessage Read(byte[] buffer) {
            return Read(buffer, 0, buffer.length);
        }

        int Read() {
            if (mIsOpen && mBtSocket.isConnected() && mInPut != null) {
                try {
                    return mInPut.read();
                } catch (Exception var2) {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        boolean PortIsOpen() {
            byte[] b = new byte[4];
            ReturnMessage msg = Read(b);
            if (msg.GetReadByteCount() == -1) {
                mIsOpen = false;
            } else {
                mIsOpen = true;
            }

            return mIsOpen;
        }
    }


    private class EthernetPort extends PrinterPort {
        private InetAddress mInetAddr;
        private SocketAddress mSocketAddr;
        private Socket mNetSocket = new Socket();
        private OutputStream mOutput;
        private InputStream mInput;
        private Process p;

        public EthernetPort(PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType == PortType.Ethernet && portInfo.mEthernetPort > 0) {
                try {
                    mInetAddr = Inet4Address.getByName(portInfo.mEthernetIP);
                    mPortInfo.mParIsOK = true;
                } catch (Exception var4) {
                    mPortInfo.mParIsOK = false;
                }
            } else {
                mPortInfo.mParIsOK = false;
            }

        }

        ReturnMessage OpenPort() {
            if (!mPortInfo.mParIsOK) {
                return new ReturnMessage(StatusCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                try {
                    mSocketAddr = new InetSocketAddress(mInetAddr, mPortInfo.mEthernetPort);
                    mNetSocket.connect(mSocketAddr, 3000);
                    if (mOutput != null) {
                        mOutput = null;
                    }

                    mOutput = mNetSocket.getOutputStream();
                    if (mInput != null) {
                        mInput = null;
                    }

                    mInput = mNetSocket.getInputStream();
                    mIsOpen = true;
                } catch (Exception var2) {
                    return new ReturnMessage(StatusCode.OpenPortFailed, var2.toString(), 0);
                }

                return new ReturnMessage(StatusCode.OpenPortSuccess, "Open ethernet port success !\n", 0);
            }
        }

        ReturnMessage ClosePort() {
            try {
                if (mOutput != null) {
                    mOutput.flush();
                }

                if (mNetSocket != null) {
                    mNetSocket.close();
                }

                mIsOpen = false;
                mOutput = null;
                mInput = null;
            } catch (Exception var2) {
                return new ReturnMessage(StatusCode.ClosePortFailed, var2.toString(), 0);
            }

            return new ReturnMessage(StatusCode.ClosePortSuccess, "Close ethernet port success !\n", 0);
        }

        ReturnMessage Write(int data) {
            if (mIsOpen && mOutput != null && mNetSocket.isConnected()) {
                try {
                    mOutput.write(data);
                } catch (Exception var3) {
                    return new ReturnMessage(StatusCode.WriteDataFailed, var3.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send 1 byte .\n", 1);
            } else {
                return new ReturnMessage(StatusCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        ReturnMessage Write(byte[] data) {
            if (mIsOpen && mOutput != null && mNetSocket.isConnected()) {
                try {
                    mOutput.write(data);
                } catch (Exception var3) {
                    return new ReturnMessage(StatusCode.WriteDataFailed, var3.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
            } else {
                return new ReturnMessage(StatusCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        ReturnMessage Write(byte[] data, int offset, int count) {
            if (mIsOpen && mOutput != null && mNetSocket.isConnected()) {
                try {
                    mOutput.write(data, offset, count);
                } catch (Exception var5) {
                    return new ReturnMessage(StatusCode.WriteDataFailed, var5.toString(), 0);
                }

                return new ReturnMessage(StatusCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
            } else {
                return new ReturnMessage(StatusCode.WriteDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        ReturnMessage Read(byte[] buffer, int offset, int count) {
            boolean readBytes = true;
            if (mIsOpen && mInput != null && mNetSocket.isConnected()) {
                int readBytes1;
                try {
                    readBytes1 = mInput.read(buffer, offset, count);
                    if (readBytes1 == -1) {
                        return new ReturnMessage(StatusCode.ReadDataFailed, "Ethernet port was close !\n", 0);
                    }
                } catch (Exception var6) {
                    return new ReturnMessage(StatusCode.ReadDataFailed, var6.toString(), 0);
                }

                return new ReturnMessage(StatusCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes1);
            } else {
                return new ReturnMessage(StatusCode.ReadDataFailed, "Ethernet port was close !\n", 0);
            }
        }

        ReturnMessage Read(byte[] buffer) {
            return Read(buffer, 0, buffer.length);
        }

        int Read() {
            if (mIsOpen && mNetSocket.isConnected() && mInput != null) {
                try {
                    return mInput.read();
                } catch (Exception var2) {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        boolean PortIsOpen() {
            mIsOpen = pingHost(mPortInfo.mEthernetIP);
            if (!mIsOpen) {
                mIsOpen = pingHost(mPortInfo.mEthernetIP);
            }

            return mIsOpen;
        }

        private boolean pingHost(String str) {
            boolean result = false;
            BufferedReader bufferedReader = null;

            try {
                Thread.sleep(2000L);
                p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + str);
                InputStream e = p.getInputStream();
                InputStreamReader reader = new InputStreamReader(e);
                bufferedReader = new BufferedReader(reader);
                Object line = null;

                while (bufferedReader.readLine() != null) {
                    ;
                }

                int status = p.waitFor();
                if (status == 0) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException var18) {
                result = false;
            } catch (InterruptedException var19) {
                result = false;
            } finally {
                if (p != null) {
                    p.destroy();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException var17) {
                        var17.printStackTrace();
                    }
                }

            }

            return result;
        }
    }

    private class NetConnectThread extends Thread {
        public ReturnMessage returnMessage = null;
        private PrinterPort mTemPort = null;

        public NetConnectThread(PrinterPort port) {
            mTemPort = port;
        }

        public void run() {
            returnMessage = mTemPort.OpenPort();
        }
    }

    private class NetReadThread extends Thread {
        public ReturnMessage returnMessage = null;
        private PrinterPort mTemPort = null;
        public byte[] reBuffer = null;
        public int reOffset = 0;
        public int reCount = 0;

        public NetReadThread(PrinterPort port, byte[] buffer, int offset, int count) {
            mTemPort = port;
            reBuffer = buffer;
            reOffset = offset;
            reCount = count;
        }

        public void run() {
            returnMessage = mTemPort.Read(reBuffer, reOffset, reCount);
        }
    }

    public class PortInfo {
        private PortType mPortType;
        private String mUsbPathName;
        private int mUsbPid;
        private int mUsbVid;
        private int mEthernetPort;
        private String mEthernetIP;
        private String mBluetoothID;
        private boolean mParIsOK;
        private Context mContext;
        private boolean mIsOpened;

        public PortInfo() {
            mPortType = PortType.Unknown;
            mUsbPathName = "";
            mUsbPid = 0;
            mUsbVid = 0;
            mEthernetPort = 0;
            mEthernetIP = "";
            mBluetoothID = "";
            mParIsOK = false;
            mContext = null;
            mIsOpened = false;
        }

        public PortType GetPortType() {
            return mPortType;
        }

        public String GetPortName() {
            return mPortType.name();
        }

        public String GetUsbPathName() {
            return mUsbPathName;
        }

        public int GetUsbVid() {
            return mUsbVid;
        }

        public int GetUsbPid() {
            return mUsbPid;
        }

        public int GetEthernetPort() {
            return mEthernetPort;
        }

        public String GetEthernetIP() {
            return mEthernetIP;
        }

        public String GetBluetoothID() {
            return mBluetoothID;
        }

        public boolean PortIsOpen() {
            return mIsOpened;
        }
    }

    private abstract class PrinterPort {
        protected PortInfo mPortInfo = null;
        protected Queue<Byte> mRxdQueue = null;
        protected Queue<Byte> mTxdQueue = null;
        protected boolean mIsOpen = false;

        abstract ReturnMessage OpenPort();

        abstract ReturnMessage ClosePort();

        abstract ReturnMessage Write(int var1);

        abstract ReturnMessage Write(byte[] var1);

        abstract ReturnMessage Write(byte[] var1, int var2, int var3);

        abstract ReturnMessage Read(byte[] var1, int var2, int var3);

        abstract ReturnMessage Read(byte[] var1);

        abstract int Read();

        abstract boolean PortIsOpen();

        public PrinterPort(PortInfo portInfo) {
            mPortInfo = portInfo;
        }

        public int GetRxdCount() {
            return mRxdQueue != null ? mRxdQueue.size() : 0;
        }

        public int GetTxdCount() {
            return mTxdQueue != null ? mTxdQueue.size() : 0;
        }
    }

    /**
     * 返回的信息
     */
    public class ReturnMessage {
        private StatusCode mStatusCode;
        private String mErrorStrings;
        private int mReadBytes;
        private int mWriteBytes;

        ReturnMessage() {
            mReadBytes = -1;
            mWriteBytes = -1;
            mStatusCode = StatusCode.UnknownError;
            mErrorStrings = "Unknown error\n";
            mReadBytes = -1;
            mWriteBytes = -1;
        }

        private ReturnMessage(StatusCode ec, String es) {
            mReadBytes = -1;
            mWriteBytes = -1;
            mStatusCode = ec;
            mErrorStrings = es;
        }

        private ReturnMessage(StatusCode ec, String es, int count) {
            mReadBytes = -1;
            mWriteBytes = -1;
            mStatusCode = ec;
            mErrorStrings = es;
            switch (ec) {
                case WriteDataSuccess:
                    mWriteBytes = count;
                    break;
                case ReadDataSuccess:
                    mReadBytes = count;
            }

        }

        public StatusCode GetErrorCode() {
            return mStatusCode;
        }

        public String GetErrorStrings() {
            return mErrorStrings;
        }

        public int GetReadByteCount() {
            return mReadBytes;
        }

        public int GetWriteByteCount() {
            return mWriteBytes;
        }
    }

    /**
     * usb端口
     */
    private class UsbPort extends PrinterPort {
        private UsbManager mUsbManager = null;
        private UsbDevice mUsbDevice = null;
        private UsbInterface mUsbInterface = null;
        private UsbDeviceConnection mUsbDeviceConnection = null;
        private UsbEndpoint mUsbInEndpoint = null;
        private UsbEndpoint mUsbOutEndpoint = null;
        private PendingIntent mPermissionIntent = null;
        private String mUserUsbName = null;
        private String ACTION_USB_PERMISSION = "net.xprinter.xprintersdk.USB_PERMISSION";
        private boolean mPermissioning = true;
        private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    Log.d("onReceive id is", "" + Thread.currentThread().getId());
                    mPermissioning = false;
                    synchronized (this) {
                        UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                        if (intent.getBooleanExtra("permission", false) && device != null) {
                            mUsbDevice = device;
                        }
                    }
                }

            }
        };

        public UsbPort(PortInfo portInfo) {
            super(portInfo);
            if (portInfo.mPortType != PortType.USB && portInfo.mContext != null && portInfo.mUsbPathName.equals("")) {
                mPortInfo.mParIsOK = false;
            } else {
                mPortInfo.mParIsOK = true;
                if (mPortInfo.mUsbPathName != null && !portInfo.mUsbPathName.equals("")) {
                    mUserUsbName = mPortInfo.mUsbPathName;
                }
            }

        }

        private List<UsbDevice> GetUsbDeviceList() {
            ArrayList temList = new ArrayList();
            mUsbManager = (UsbManager) mPortInfo.mContext.getSystemService(Context.USB_SERVICE);
            HashMap deviceList = mUsbManager.getDeviceList();
            Iterator deviceIterator = deviceList.values().iterator();

            while (true) {
                while (deviceIterator.hasNext()) {
                    UsbDevice device = (UsbDevice) deviceIterator.next();

                    for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                        if (device.getInterface(iInterface).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                            temList.add(device);
                            break;
                        }
                    }
                }

                if (temList.size() == 0) {
                    return null;
                }

                return temList;
            }
        }

        /**
         * 打开端口
         * @return
         */
        ReturnMessage OpenPort() {
            if (!mPortInfo.mParIsOK) {
                return new ReturnMessage(StatusCode.OpenPortFailed, "PortInfo error !\n", 0);
            } else {
                List temDevList = GetUsbDeviceList();
                if (temDevList == null) {
                    return new ReturnMessage(StatusCode.OpenPortFailed, "Not find XPrinter\'s USB printer !\n", 0);
                } else {
                    mUsbDevice = null;
                    if (mUserUsbName == null) {
                        if (mUsbManager.hasPermission((UsbDevice) temDevList.get(0))) {
                            mUsbDevice = (UsbDevice) temDevList.get(0);
                        } else {
                            mPermissionIntent = PendingIntent.getBroadcast(mPortInfo.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            IntentFilter iInterface = new IntentFilter(ACTION_USB_PERMISSION);
                            iInterface.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                            iInterface.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                            iInterface.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                            iInterface.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                            mPortInfo.mContext.registerReceiver(mUsbReceiver, iInterface);
                            mUsbManager.requestPermission((UsbDevice) temDevList.get(0), mPermissionIntent);
                        }
                    } else {
                        boolean deviceFound = false;
                        Iterator var4 = temDevList.iterator();

                        while (var4.hasNext()) {
                            UsbDevice iEndpoint = (UsbDevice) var4.next();
                            if (iEndpoint.getDeviceName().equals(mUserUsbName)) {
                                if (mUsbManager.hasPermission(iEndpoint)) {
                                    mUsbDevice = iEndpoint;
                                } else {
                                    mPermissionIntent = PendingIntent.getBroadcast(mPortInfo.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                                    filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                                    filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                                    filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                                    filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                                    mPortInfo.mContext.registerReceiver(mUsbReceiver, filter);
                                    mUsbManager.requestPermission(iEndpoint, mPermissionIntent);
                                }

                                deviceFound = true;
                                break;
                            }
                        }

                        if (!deviceFound) {
                            return new ReturnMessage(StatusCode.OpenPortFailed, "Not find " + mUserUsbName + " !\n", 0);
                        }
                    }

                    Log.d("open id is", "" + Thread.currentThread().getId());
                    if (mUsbDevice == null) {
                        return new ReturnMessage(StatusCode.OpenPortFailed, "Get USB communication permission failed !\n", 0);
                    } else {
                        for (int var7 = 0; var7 < mUsbDevice.getInterfaceCount(); ++var7) {
                            if (mUsbDevice.getInterface(var7).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                                for (int var8 = 0; var8 < mUsbDevice.getInterface(var7).getEndpointCount(); ++var8) {
                                    if (mUsbDevice.getInterface(var7).getEndpoint(var8).getType() == 2) {
                                        if (mUsbDevice.getInterface(var7).getEndpoint(var8).getDirection() == 128) {
                                            mUsbInEndpoint = mUsbDevice.getInterface(var7).getEndpoint(var8);
                                        } else {
                                            mUsbOutEndpoint = mUsbDevice.getInterface(var7).getEndpoint(var8);
                                        }
                                    }

                                    if (mUsbInEndpoint != null && mUsbOutEndpoint != null) {
                                        break;
                                    }
                                }

                                mUsbInterface = mUsbDevice.getInterface(var7);
                                break;
                            }
                        }

                        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                        if (mUsbDeviceConnection != null && mUsbDeviceConnection.claimInterface(mUsbInterface, true)) {
                            mPortInfo.mUsbPathName = mUsbDevice.getDeviceName();
                            mIsOpen = true;
                            return new ReturnMessage(StatusCode.OpenPortSuccess, "Open USB port success !\n", 0);
                        } else {
                            return new ReturnMessage(StatusCode.OpenPortFailed, "Can\'t Claims exclusive access to UsbInterface", 0);
                        }
                    }
                }
            }
        }

        ReturnMessage ClosePort() {
            if (mUsbDeviceConnection != null) {
                mUsbInEndpoint = null;
                mUsbOutEndpoint = null;
                mUsbDeviceConnection.releaseInterface(mUsbInterface);
                mUsbDeviceConnection.close();
                mUsbDeviceConnection = null;
            }

            mIsOpen = false;
            return new ReturnMessage(StatusCode.ClosePortSuccess, "Close usb connection success !\n", 0);
        }

        ReturnMessage Write(int data) {
            byte[] tem = new byte[]{(byte) (data & 255)};
            return Write(tem);
        }

        ReturnMessage Write(byte[] data) {
            return Write(data, 0, data.length);
        }

        ReturnMessage Write(byte[] data, int offset, int count) {
            if (!mIsOpen) {
                return new ReturnMessage(StatusCode.WriteDataFailed, "USB port was closed !\n", 0);
            } else {
                byte[] temData = new byte[count];

                int writeCount;
                for (writeCount = offset; writeCount < offset + count; ++writeCount) {
                    temData[writeCount - offset] = data[writeCount];
                }

                writeCount = mUsbDeviceConnection.bulkTransfer(mUsbOutEndpoint, temData, temData.length, 3000);
                return writeCount < 0 ? new ReturnMessage(StatusCode.WriteDataFailed, "usb port write bulkTransfer failed !\n", 0) : new ReturnMessage(StatusCode.WriteDataSuccess, "send " + writeCount + " bytes.\n", writeCount);
            }
        }

        ReturnMessage Read(byte[] buffer, int offset, int count) {
            if (!mIsOpen) {
                return new ReturnMessage(StatusCode.ReadDataFailed, "USB port was closed !\n", 0);
            } else {
                boolean readBytes = true;
                byte[] temBuffer = new byte[count];
                int var7 = mUsbDeviceConnection.bulkTransfer(mUsbInEndpoint, buffer, count, 3000);
                if (var7 < 0) {
                    return new ReturnMessage(StatusCode.ReadDataFailed, "usb port read bulkTransfer failed !\n", 0);
                } else {
                    for (int i = offset; i < offset + var7; ++i) {
                        buffer[i] = temBuffer[i - offset];
                    }

                    return new ReturnMessage(StatusCode.ReadDataSuccess, "Read " + var7 + " bytes.\n", var7);
                }
            }
        }

        ReturnMessage Read(byte[] buffer) {
            return Read(buffer, 0, buffer.length);
        }

        int Read() {
            byte[] temBuffer = new byte[1];
            return Read(temBuffer).GetErrorCode() == StatusCode.OpenPortFailed ? -1 : temBuffer[0];
        }

        boolean PortIsOpen() {
            if (mUsbDevice != null && mUsbInEndpoint != null && mUsbOutEndpoint != null) {
                new ArrayList();
                List temStr = PosPrinterChecker.GetUsbPathNames(mPortInfo.mContext);
                if (temStr != null && temStr.size() > 0) {
                    Iterator var3 = temStr.iterator();

                    while (var3.hasNext()) {
                        String str = (String) var3.next();
                        if (str.equals(mUsbDevice.getDeviceName())) {
                            return mIsOpen = true;
                        }
                    }

                    return mIsOpen = false;
                } else {
                    return mIsOpen = false;
                }
            } else {
                return mIsOpen = false;
            }
        }
    }
}
