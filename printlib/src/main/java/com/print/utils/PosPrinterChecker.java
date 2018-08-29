package com.print.utils;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mylo on 2017/7/5.
 */

public class PosPrinterChecker {

    /**
     * 检查usb设备的vendorid
     * @param usbDevice
     * @return
     */
    public static boolean CheckUsbVid(UsbDevice usbDevice) {
        //打印机可识别的vendorID，目前可以通过该id来区分标签打印和小票打印机
        int[] xprinterUsbID = new int[]{1659, 1046, 7358, 1155, 8137, 1003, 11575};
        int vid = usbDevice.getVendorId();

        for (int id : xprinterUsbID) {
            if (vid == id) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取usb的路径名称
     * @param context
     * @return
     */
    public static List<String> GetUsbPathNames(Context context) {
        ArrayList usbNames = new ArrayList();
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap usbList = usbManager.getDeviceList();
        Iterator deviceIterator = usbList.values().iterator();

        while (true) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = (UsbDevice) deviceIterator.next();

                for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                    if (device.getInterface(iInterface).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                        usbNames.add(device.getDeviceName());
                        break;
                    }
                }
            }

            if (usbNames.size() == 0) {
                usbNames = null;
            }

            return usbNames;
        }
    }
}
