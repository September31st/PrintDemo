package com.print.mylo.printdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.print.mylo.printdemo.bean.OrderPrintReceiptBean;
import com.print.mylo.printdemo.bean.OrderPrintTagBean;
import com.print.mylo.printdemo.util.PrintUtil;
import com.print.mylo.printdemo.util.QRCodeCreator;
import com.print.posprinterface.IMyBinder;
import com.print.posprinterface.ProcessData;
import com.print.posprinterface.UiExecute;
import com.print.service.PosprinterService;
import com.print.utils.BitmapToByteData;
import com.print.utils.DataForSendToPrinterPos80;
import com.print.utils.DataForSendToPrinterTSC;
import com.print.utils.PosPrinterChecker;
import com.print.utils.PosPrinterDev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MainActivity2 extends Activity {

    public static final int ENABLE_BLUETOOTH = 1;

    private static final int smallFontSplitLength = 46;
    private static final int smallFontFullLength = 48;
    private static final int remarkFontSplitLength = 40;
    private static final int productFullNameSplitLength = 34;

    private static final String TSC_FONT = "TSS24.BF2";
    private final static int OFFSET = 10;


    //默认微信公众号
    private static final String DEFAULT_WECHAT_PUBLIC = "http://weixin.qq.com/r/VypRSaDExP27reQq939F";

    public static IMyBinder binder;//IMyBinder接口，所有可供调用的连接和发送数据的方法都封装在这个接口内
    //bindService的参数conn
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            //绑定成功
            binder = (IMyBinder) service;


        }
    };
    public static boolean isConnect;//用来标识连接状态的一个boolean值
    Button btn0, btn1, btn2, btn4, btn_scan, btn_sb;
    Spinner spinner;
    EditText et;
    int pos;
    private View dialogView, dialogView2, dialogView3;
    BluetoothAdapter blueadapter;
    private ArrayAdapter<String> adapter1, adapter2, adapter3;
    private ListView lv1, lv2, lv_usb;
    private LinearLayout ll1;
    private ArrayList<String> deviceList_bonded = new ArrayList<String>();
    private ArrayList<String> deviceList_found = new ArrayList<String>();
    AlertDialog dialog;
    public String mac = "", usbDev = "";
    TextView tv_usb;
    private List<String> usbList, usblist;
    PosPrinterDev posdev;
    private DeviceReceiver myDevice = new DeviceReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //绑定service，获取ImyBinder对象
        Intent intent = new Intent(this, PosprinterService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        //注册蓝牙广播接收者
        IntentFilter filterStart = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(myDevice, filterStart);
        registerReceiver(myDevice, filterEnd);
        //初始化控件
        btn0 = (Button) findViewById(R.id.button0);
        btn2 = (Button) findViewById(R.id.button2);
        btn1 = (Button) findViewById(R.id.button1);
        btn4 = (Button) findViewById(R.id.button4);
        btn_sb = (Button) findViewById(R.id.button7);
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setSelection(2);
        et = (EditText) findViewById(R.id.editText1);
        //给控件添加监听事件
        addListener();
    }


    private void addListener() {
        // TODO Auto-generated method stub
        btn_sb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setUsb();
            }
        });
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                pos = arg2;
                switch (arg2) {
                    case 0:
                        et.setText("");
                        et.setHint(getString(R.string.hint));
                        et.setEnabled(true);
                        btn_sb.setVisibility(View.GONE);
                        break;
                    case 1:
                        et.setText("");
                        et.setHint(getString(R.string.bleselect));
                        et.setEnabled(false);
                        btn_sb.setVisibility(View.VISIBLE);
                        //connectBLE();
                        break;
                    case 2:
                        et.setText("");
                        et.setHint(getString(R.string.usbselect));
                        et.setEnabled(false);
                        btn_sb.setVisibility(View.VISIBLE);
                        //setUsb();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        //点击连接按钮，连接打印机
        btn0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (pos) {
                    case 0:
                        connetNet();
                        break;

                    case 1:
                        //connectBLE();
                        sendble();
                        break;
                    case 2:
                        connectUSB();
                        break;
                    default:
                        break;
                }

            }
        });
        //断开按钮btn1的监听事件
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isConnect) {//如果是连接状态才执行断开操作
                    binder.disconnectCurrentPort(new UiExecute() {

                        @Override
                        public void onsucess() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_discon_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onfailed() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_discon_faile), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_present_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    binder.writeDataByYouself(new UiExecute() {
                        @Override
                        public void onsucess() {
                            Toast.makeText(MainActivity2.this, "打印成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onfailed() {
                            Toast.makeText(MainActivity2.this, "打印失败", Toast.LENGTH_SHORT).show();
                        }
                    }, new ProcessData() {
                        private void addLabel(List<byte[]> list, OrderPrintTagBean bean) {

                            //示例代码，用于打印样式
                            bean.code = "009";
                            bean.productName = "百香芒果瑞纳冰";
                            bean.type = "外送";
                            bean.process = "第25/30杯";
                            bean.size = "大杯";
                            bean.temperature = "常温";
                            bean.productEnName = "Passionfruit Mango Exfreezo";
                            bean.additionList = new ArrayList<>();
                            bean.additionList.add("无奶油");
                            bean.additionList.add("单份糖");
                            bean.additionList.add("无奶");
                            bean.makeDate = "2017-11-27 14:03";


                            if (bean.productName == null) {
                                bean.productName = "";
                            }
                            int line = 1;
                            byte[] data0 = DataForSendToPrinterTSC.sizeBymm(40,
                                    30);
                            list.add(data0);
                            //设置Gap,同上
                            list.add(DataForSendToPrinterTSC.gapBymm(5, 1));
                            list.add(DataForSendToPrinterTSC.cls());
                            int leftMargin = 24;
                            int startHeight = 5;
                            int lineMargin = 8;
                            int fontHight = 20;


                            //第一行 取餐码、自提、进度
                            if (bean.code == null) {
                                bean.code = "";
                            }
                            byte[] line1 = DataForSendToPrinterTSC
                                    .text(leftMargin, startHeight, TSC_FONT, 0, 2, 2,
                                            bean.code);
                            list.add(line1);
                            StringBuilder firstLine = new StringBuilder();
                            if (!TextUtils.isEmpty(bean.type)) {
                                firstLine.append(bean.type);
                            }

                            if (!TextUtils.isEmpty(bean.process)) {
                                firstLine.append("  ").append(bean.process);
                            }
                            byte[] line11 = DataForSendToPrinterTSC
                                    .text(leftMargin + 90, startHeight + 20, TSC_FONT, 0, 1, 1,
                                            firstLine.toString());
                            list.add(line11);
                            //第二行 品名
                            if (bean.productName == null) {
                                bean.productName = "";
                            }
                            String productName = bean.productName;
                            line++;
                            //需要一行
                            byte[] line2 = DataForSendToPrinterTSC
                                    .text(leftMargin, startHeight + (lineMargin + fontHight) * line, TSC_FONT, 0, 1, 1,
                                            productName);
                            list.add(line2);
                            line++;
                            if (bean.productEnName == null) {
                                bean.productEnName = "";
                            }
                            String enName = bean.productEnName;
                            if (enName.length() > 30) {
                                enName = enName.substring(0, 30);
                            }
                            byte[] line2_en = DataForSendToPrinterTSC
                                    .text(leftMargin, startHeight + (lineMargin + fontHight) * line, TSC_FONT, 0, 1, 1,
                                            enName);
                            list.add(line2_en);
                            line++;

                            //第三行 大小和温度：
                            StringBuilder sizeAndTemp = new StringBuilder();
                            String sizeTempOutput;
                            if (!TextUtils.isEmpty(bean.size)) {
                                sizeAndTemp.append(bean.size);
                            }
                            if (!TextUtils.isEmpty(bean.size) && !TextUtils.isEmpty(bean.temperature)) {
                                sizeAndTemp.append("/");
                            }
                            if (!TextUtils.isEmpty(bean.temperature)) {
                                sizeAndTemp.append(bean.temperature);
                            }
                            sizeTempOutput = sizeAndTemp.toString();
                            //只有都不为空的时候才会打印这一行
                            if (!TextUtils.isEmpty(sizeTempOutput)) {
                                byte[] line3 = DataForSendToPrinterTSC
                                        .text(leftMargin, startHeight + (lineMargin + fontHight) * line + OFFSET, TSC_FONT, 0, 1, 1,
                                                sizeTempOutput);
                                list.add(line3);
                                line++;
                            }


                            //第四行 口味：
                            if (bean.additionList != null && !bean.additionList.isEmpty()) {
                                boolean needSecondLine = bean.additionList.size() > 3;
                                int size = needSecondLine ? 3 : bean.additionList.size();
                                StringBuilder fourthLine = new StringBuilder();
                                for (int i = 0; i < size; i++) {
                                    String add = bean.additionList.get(i);
                                    if (add == null) {
                                        add = "";
                                    }
                                    fourthLine.append(add).append(" ");
                                }
                                byte[] line4 = DataForSendToPrinterTSC
                                        .text(leftMargin, startHeight + (lineMargin + fontHight) * line + OFFSET, TSC_FONT, 0, 1, 1,
                                                fourthLine.toString());
                                list.add(line4);
                                line++;
                                if (needSecondLine) {
                                    StringBuilder fourthSLine = new StringBuilder();
                                    int sSize = bean.additionList.size() > 6 ? 6 : bean.additionList.size();
                                    for (int i = 3; i < sSize; i++) {
                                        String add = bean.additionList.get(i);
                                        if (add == null) {
                                            add = "";
                                        }
                                        fourthSLine.append(add).append(" ");
                                    }
                                    byte[] line41 = DataForSendToPrinterTSC
                                            .text(leftMargin, startHeight + (lineMargin + fontHight) * line + OFFSET, TSC_FONT, 0, 1, 1,
                                                    fourthSLine.toString());
                                    list.add(line41);
                                    line++;
                                }
                            }
                            //第五行 制作时间：
                            if (!TextUtils.isEmpty(bean.makeDate)) {
                                byte[] data4 = DataForSendToPrinterTSC
                                        .text(leftMargin, startHeight + (lineMargin + fontHight) * line + OFFSET + OFFSET, TSC_FONT, 0, 1, 1,
                                                bean.makeDate);
                                list.add(data4);

                            }

                            list.add(DataForSendToPrinterTSC.feed(1));
                            list.add(DataForSendToPrinterTSC.print(1));
                        }


                        @Override
                        public List<byte[]> processDataBeforeSend() {
                            ArrayList<byte[]> list = new ArrayList<byte[]>();
                            for (int i = 0; i < 2; i++) {
                                OrderPrintTagBean bean = new OrderPrintTagBean();
                                addLabel(list, bean);
                            }
                            return list;
                        }
                    });
                } else {
                    Toast.makeText(MainActivity2.this, "当前并为连接", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //打印二维码
        btn4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isConnect) {
                    // TODO Auto-generated method stub
                    binder.writeDataByYouself(new UiExecute() {

                        @Override
                        public void onsucess() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onfailed() {
                            // TODO Auto-generated method stub

                        }
                    }, new ProcessData() {
                        @Override
                        public List<byte[]> processDataBeforeSend() {

                            //示例代码，用于打印样式
                            OrderPrintReceiptBean bean = new OrderPrintReceiptBean();
                            bean.shopName = "";
                            bean.shopTelephone = "400-123456";
                            bean.code = "345";
                            bean.type = "外送";
                            bean.memberName = "老李";
                            bean.memberSex = "先生";
                            bean.memberMobile = "15810201221";
                            bean.dispatchAddress = "朝阳门外大街128号老李卤煮店";
                            bean.remark = "多加一份奶";
                            bean.marketingInfo = "扫码解锁更多优惠信息";
                            bean.productList = new ArrayList<>();
                            for (int i = 0; i < 3; i++) {
                                OrderPrintReceiptBean.ItemBean itembean = new OrderPrintReceiptBean.ItemBean();
                                itembean.name = "(大/热)冰焦糖玛奇朵";
                                itembean.enName = "Iced Caramel Macchiato";
                                itembean.number = 1;
                                itembean.additionList = new ArrayList<>();
                                int max = (int) (Math.random() * 6);
                                for (int j = 0; j < max; j++) {
                                    int select = (int) (Math.random() * 4) + 1;
                                    String str;
                                    switch (select) {
                                        case 1:
                                            str = "加奶油x2";
                                            break;
                                        case 2:
                                            str = "单份糖";
                                            break;
                                        case 3:
                                            str = "加奶油x2";
                                            break;
                                        default:
                                            str = "单份糖";
                                            break;
                                    }
                                    itembean.additionList.add(str);
                                }
                                bean.productList.add(itembean);
                            }


                            int smallSize = 0;
                            int bigSize = 51;
                            byte[] feedTowRow = DataForSendToPrinterPos80.printAndFeedForward(7);
                            byte[] textAlignmentLeft = DataForSendToPrinterPos80.selectAlignment(48);
                            byte[] textAlignmentCenter = DataForSendToPrinterPos80.selectAlignment(49);
                            byte[] textAlignmentRight = DataForSendToPrinterPos80.selectAlignment(50);
                            byte[] fullCut = DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(48);


                            ArrayList<byte[]> list = new ArrayList<byte[]>();
                            DataForSendToPrinterPos80.setCharsetName("gbk");
                            String contentLine = "\n------------------------------------------------\n\n";

                            //第一块 店名+取餐码+取餐方式
                            if (bean.shopName == null) {
                                bean.shopName = "";
                            }
                            if (bean.shopTelephone == null) {
                                bean.shopTelephone = "";
                            }
                            StringBuilder contentHead = new StringBuilder();
                            contentHead.append(bean.shopName);
                            int contentHeadLength = PrintUtil.getPrintLength(bean.shopName + bean.shopTelephone);
                            for (int i = 0; i < smallFontFullLength - contentHeadLength; i++) {
                                contentHead.append(" ");
                            }
                            contentHead.append(bean.shopTelephone).append("\n\n");
                            //顶部店名
                            list.add(textAlignmentLeft);
                            list.add(DataForSendToPrinterPos80.printContent(contentHead.toString()));
                            if (bean.code == null) {
                                bean.code = "";
                            }
                            String getNumber = bean.code + "\n";

                            //取餐码
                            list.add(textAlignmentCenter);
                            list.add(DataForSendToPrinterPos80.selectCharacterSize(bigSize));
                            list.add(DataForSendToPrinterPos80.printContent(getNumber));

                            if (bean.type == null) {
                                bean.type = "";
                            }
                            String mealsTakingType = " " + bean.type + "\n";

                            //是否外卖
                            list.add(DataForSendToPrinterPos80.selectCharacterSize(smallSize));
                            list.add(DataForSendToPrinterPos80.printContent(mealsTakingType));


                            //第二块 订单详情
                            list.add(textAlignmentCenter);
                            list.add(DataForSendToPrinterPos80.printContent(contentLine));
                            list.add(textAlignmentLeft);
                            list.add(DataForSendToPrinterPos80.printContent("份数 商品名称\n\n"));
                            if (bean.productList != null && !bean.productList.isEmpty()) {
                                for (OrderPrintReceiptBean.ItemBean itemBean : bean.productList) {
                                    StringBuilder itemString = new StringBuilder();
//                    itemString.append(" ");
                                    if (itemBean.number == null) {
                                        itemString.append("     ");
                                    } else if (itemBean.number > 9) {
                                        itemString.append(itemBean.number).append("   ");
                                    } else {
                                        itemString.append(itemBean.number).append("    ");
                                    }
                                    String fullName = "";
                                    if (!TextUtils.isEmpty(itemBean.name)) {
                                        fullName = itemBean.name + " ";
                                    }
                                    if (!TextUtils.isEmpty(itemBean.enName)) {
                                        fullName = fullName + itemBean.enName;
                                    }
                                    if (PrintUtil.getPrintLength(fullName) > productFullNameSplitLength) {
                                        String[] fullNameSplit = PrintUtil.splitStringByPrintLength(fullName, productFullNameSplitLength);
                                        fullName = fullNameSplit[0];
                                    }
                                    itemString.append(fullName).append("\n");
                                    if (itemBean.additionList != null && !itemBean.additionList.isEmpty()) {
                                        itemString.append("     ");
                                        for (int index = 0; index < itemBean.additionList.size(); index++) {
                                            if (index == 3) {
                                                itemString.append("\n     ").append(itemBean.additionList.get(index)).append(" ");
                                            } else if (index == 6) {
                                                itemString.append("\n     ").append(itemBean.additionList.get(index)).append(" ");
                                            } else {
                                                itemString.append(itemBean.additionList.get(index)).append(" ");
                                            }
                                        }
                                        itemString.append("\n");
                                    }
                                    list.add(DataForSendToPrinterPos80.printContent(itemString.toString()));
                                }
                                list.add(textAlignmentCenter);
                                list.add(DataForSendToPrinterPos80.printContent(contentLine));
                                list.add(textAlignmentLeft);

                            }

                            //第三块 订单人信息
                            StringBuilder customerInfo = new StringBuilder();
                            if (bean.memberName == null) {
                                bean.memberName = "";
                            }
                            customerInfo.append("客户：").append(bean.memberName).append(" ");
                            if (!TextUtils.isEmpty(bean.memberSex)) {
                                customerInfo.append(bean.memberSex);
                            }
                            if (!TextUtils.isEmpty(bean.memberMobile)) {
                                customerInfo.append("       ").append("手机尾号：").append(bean.memberMobile.substring(bean.memberMobile.length() - 4, bean.memberMobile.length())).append("\n");
                            }
                            if (!TextUtils.isEmpty(bean.memberMobile)) {
                                customerInfo.append("\n").append("送货地址：");
//                String address = addressSplitString(new StringBuilder(), "送货地址：" + bean.dispatchAddress, smallFontSplitLength);
                                customerInfo.append(bean.dispatchAddress).append("\n");
                            } else {
                                customerInfo.append("\n\n");
                            }
                            if (!TextUtils.isEmpty(bean.remark)) {
                                customerInfo.append("\n");
//                String remark = remarkSplitString(new StringBuilder(), "备注：" + bean.remark, remarkFontSplitLength);
                                customerInfo.append("备注：").append(bean.remark).append("\n");
                            }

                            list.add(textAlignmentLeft);
                            list.add(DataForSendToPrinterPos80.printContent(customerInfo.toString()));
                            list.add(textAlignmentCenter);
                            list.add(DataForSendToPrinterPos80.printContent(contentLine));
                            list.add(textAlignmentLeft);


                            //第四块 二维码信息
                            if (bean.marketingInfo == null) {
                                bean.marketingInfo = "";
                            }


                            list.add(textAlignmentLeft);
                            Bitmap bitmap = null;
                            try {
                                char checkBit = (char) ('a' + String.valueOf(bean.orderId).length());
                                String prefix = TextUtils.isEmpty(bean.weChatPublic) ? DEFAULT_WECHAT_PUBLIC : bean.weChatPublic;
                                String qrCodeString = prefix + "?" + bean.orderId + checkBit;
                                bitmap = QRCodeCreator.createQRCodeBitmap(qrCodeString);
                                list.add(DataForSendToPrinterPos80.printRasterBmp(0, bitmap, BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Center, 576));

//                bitmap = BytesUtil.getBytesFromBitMap(qrCodeString);

//                list.add(ESCUtil.getPrintQRCode(qrCodeString, 6, 3));
                                list.add(textAlignmentCenter);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            list.add(textAlignmentCenter);
                            list.add(DataForSendToPrinterPos80.printContent(bean.marketingInfo + "\n"));
                            list.add(textAlignmentCenter);
                            list.add(DataForSendToPrinterPos80.printContent(contentLine));

                            //第五块 结束语
                            StringBuilder endString = new StringBuilder();
                            if (TextUtils.isEmpty(bean.endEnDesc)) {
                                endString.append("luckin coffee");
                            } else {
                                endString.append(bean.endEnDesc);
                            }
                            endString.append("\n");
//            if (TextUtils.isEmpty(bean.endDesc)) {
//                endString.append("欢迎您再次光临！");
//            } else {
//                endString.append(bean.endDesc);
//            }
//            endString.append("\n");
                            list.add(DataForSendToPrinterPos80.printContent(endString.toString()));

                            list.add(feedTowRow); //往前出两行纸
                            list.add(fullCut);//自动切纸
                            return list;
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.not_con_printer), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    protected void setUsb() {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView3 = inflater.inflate(R.layout.usb_link, null);
        tv_usb = (TextView) dialogView3.findViewById(R.id.textView1);
        lv_usb = (ListView) dialogView3.findViewById(R.id.listView1);

        usbList = PosPrinterChecker.GetUsbPathNames(this);
        if (usbList == null) {
            usbList = new ArrayList<String>();
        }
        tv_usb.setText(getString(R.string.usb_pre_con) + usbList.size());
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usbList);
        lv_usb.setAdapter(adapter3);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView3)
                .create();
        dialog.show();
        set_lv_usb_listener(dialog);

    }

    private void set_lv_usb_listener(final AlertDialog dialog) {
        // TODO Auto-generated method stub
        lv_usb.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                usbDev = usbList.get(arg2);
                et.setText(usbDev);
                /*new Thread(){
                    public void run() {
						posdev=null;
						try {
							posdev=new PosPrinterDev(net.posprinter.utils.PosPrinterDev.PortType.USB, getApplicationContext(), usbDev);
							posdev.Open();

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();*/
                binder.connectUsbPort(getApplicationContext(), usbDev, new UiExecute() {

                    @Override
                    public void onsucess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onfailed() {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.cancel();
                Log.i("TAG", usbDev);
            }
        });
    }

    protected void connectUSB() {
        // TODO Auto-generated method stub
        binder.connectUsbPort(getApplicationContext(), et.getText().toString(), new UiExecute() {

            @Override
            public void onsucess() {
                // TODO Auto-generated method stub
                //连接成功后在UI线程中的执行
                isConnect = true;
                Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();
                btn0.setText(getString(R.string.con_success));
                //此处也可以开启读取打印机的数据
                //参数同样是一个实现的UiExecute接口对象
                //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                //直到连接断开或异常才结束，并执行onfailed
                /*binder.acceptdatafromprinter(new UiExecute() {

					@Override
					public void onsucess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onfailed() {
						// TODO Auto-generated method stub
						isConnect=false;
						Toast.makeText(getApplicationContext(), getString(R.string.con_has_discon), 0).show();
					}
				});*/
            }

            @Override
            public void onfailed() {
                // TODO Auto-generated method stub
                //连接失败后在UI线程中的执行
                isConnect = false;
                Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                btn0.setText(getString(R.string.con_failed));
            }
        });
    }

    protected void connectBLE() {
        // TODO Auto-generated method stub
        setbluetooth();
        //sendble();
    }

    public void sendble() {
        binder.connectBtPort(et.getText().toString(), new UiExecute() {

            @Override
            public void onsucess() {
                // TODO Auto-generated method stub
                //连接成功后在UI线程中的执行
                isConnect = true;
                Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();
                btn0.setText(getString(R.string.con_success));
                //此处也可以开启读取打印机的数据
                //参数同样是一个实现的UiExecute接口对象
                //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                //直到连接断开或异常才结束，并执行onfailed
                binder.acceptdatafromprinter(new UiExecute() {

                    @Override
                    public void onsucess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onfailed() {
                        // TODO Auto-generated method stub
                        isConnect = false;
                        Toast.makeText(getApplicationContext(), getString(R.string.con_has_discon), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onfailed() {
                // TODO Auto-generated method stub
                //连接失败后在UI线程中的执行
                isConnect = false;
                Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                //btn0.setText("连接失败");
            }
        });
    }

    protected void setbluetooth() {
        // TODO Auto-generated method stub
        blueadapter = BluetoothAdapter.getDefaultAdapter();
        //确认开启蓝牙
        if (!blueadapter.isEnabled()) {
            //请求用户开启
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);

        } else {
            //蓝牙已开启
            showblueboothlist();
        }

    }

    private void showblueboothlist() {
        if (!blueadapter.isDiscovering()) {
            blueadapter.startDiscovery();
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.printer_list, null);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_bonded);
        lv1 = (ListView) dialogView.findViewById(R.id.listView1);
        btn_scan = (Button) dialogView.findViewById(R.id.btn_scan);
        ll1 = (LinearLayout) dialogView.findViewById(R.id.ll1);
        lv2 = (ListView) dialogView.findViewById(R.id.listView2);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_found);
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        dialog = new AlertDialog.Builder(this).setTitle("BLE").setView(dialogView).create();
        dialog.show();
        setlistener();
        findAvalibleDevice();
    }

    protected void connetNet() {
        // TODO Auto-generated method stub
        //示例：连接打印机网口，参数为：（string）ip地址，（int）端口号，和一个实现的UiExecute接口对象
        //这个接口的实现在连接过程结束后执行（执行于UI线程），onsucess里执行连接成功的代码，onfailed反之；
        binder.connectNetPort(et.getText().toString(), 9100, new UiExecute() {

            @Override
            public void onsucess() {
                // TODO Auto-generated method stub
                //连接成功后在UI线程中的执行
                isConnect = true;
                Toast.makeText(getApplicationContext(), getString(R.string.con_success), Toast.LENGTH_SHORT).show();
                btn0.setText(getString(R.string.con_success));
                //此处也可以开启读取打印机的数据
                //参数同样是一个实现的UiExecute接口对象
                //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                //直到连接断开或异常才结束，并执行onfailed
                binder.acceptdatafromprinter(new UiExecute() {

                    @Override
                    public void onsucess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onfailed() {
                        // TODO Auto-generated method stub
                        isConnect = false;
                        Toast.makeText(getApplicationContext(), getString(R.string.con_has_discon), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onfailed() {
                // TODO Auto-generated method stub
                //连接失败后在UI线程中的执行
                isConnect = false;
                Toast.makeText(getApplicationContext(), getString(R.string.con_failed), Toast.LENGTH_SHORT).show();
                btn0.setText(getString(R.string.con_failed));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //通过去图库选择图片，然后得到返回的bitmap对象
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            final Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            binder.writeDataByYouself(new UiExecute() {

                @Override
                public void onsucess() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onfailed() {
                    // TODO Auto-generated method stub

                }
            }, new ProcessData() {//发送数据的处理和封装

                @Override
                public List<byte[]> processDataBeforeSend() {
                    // TODO Auto-generated method stub
                    ArrayList<byte[]> list = new ArrayList<byte[]>();
                    list.add(DataForSendToPrinterTSC.bitmap(10, 10, 0,
                            bitmap, BitmapToByteData.BmpType.Threshold));
                    list.add(DataForSendToPrinterTSC.print(1));
                    return list;
                }
            });
        }
        if (requestCode == ENABLE_BLUETOOTH && resultCode == RESULT_OK) {
            showblueboothlist();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(myDevice);
        binder.disconnectCurrentPort(new UiExecute() {

            @Override
            public void onsucess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onfailed() {
                // TODO Auto-generated method stub

            }
        });
        unbindService(conn);
    }

    private void setlistener() {
        // TODO Auto-generated method stub
        btn_scan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ll1.setVisibility(View.VISIBLE);
                //btn_scan.setVisibility(View.GONE);
            }
        });
        //已配对的设备的点击连接
        lv1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if (blueadapter != null && blueadapter.isDiscovering()) {
                        blueadapter.cancelDiscovery();

                    }

                    String msg = deviceList_bonded.get(arg2);
                    mac = msg.substring(msg.length() - 17);
                    String name = msg.substring(0, msg.length() - 18);
                    //lv1.setSelection(arg2);
                    dialog.cancel();
                    et.setText(mac);
                    //Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        //未配对的设备，点击，配对，再连接
        lv2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if (blueadapter != null && blueadapter.isDiscovering()) {
                        blueadapter.cancelDiscovery();

                    }
                    String msg = deviceList_found.get(arg2);
                    mac = msg.substring(msg.length() - 17);
                    String name = msg.substring(0, msg.length() - 18);
                    //lv2.setSelection(arg2);
                    dialog.cancel();
                    et.setText(mac);
                    Log.i("TAG", "mac=" + mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void findAvalibleDevice() {
        // TODO Auto-generated method stub
        //获取可配对蓝牙设备
        Set<BluetoothDevice> device = blueadapter.getBondedDevices();

        deviceList_bonded.clear();
        if (blueadapter != null && blueadapter.isDiscovering()) {
            adapter1.notifyDataSetChanged();
        }
        if (device.size() > 0) {
            //存在已经配对过的蓝牙设备
            for (Iterator<BluetoothDevice> it = device.iterator(); it.hasNext(); ) {
                BluetoothDevice btd = it.next();
                deviceList_bonded.add(btd.getName() + '\n' + btd.getAddress());
                adapter1.notifyDataSetChanged();
            }
        } else {  //不存在已经配对过的蓝牙设备
            deviceList_bonded.add("No can be matched to use bluetooth");
            adapter1.notifyDataSetChanged();
        }

    }

    /**
     * byte数组拼接
     */
    private byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 蓝牙搜索状态广播监听
     */
    private class DeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {    //搜索到新设备
                BluetoothDevice btd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索没有配过对的蓝牙设备
                if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (!deviceList_found.contains(btd.getName() + '\n' + btd.getAddress())) {

                        deviceList_found.add(btd.getName() + '\n' + btd.getAddress());
                        try {
                            adapter2.notifyDataSetChanged();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {   //搜索结束

                if (lv2.getCount() == 0) {
                    deviceList_found.add("No can be matched to use bluetooth");
                    try {
                        adapter2.notifyDataSetChanged();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
