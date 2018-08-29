package com.print.mylo.printdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wjd on 2017/8/9.
 */

public class OrderPrintTagBean implements Serializable {

    public String code;//取餐码
    public String type;//取餐类型（外带、外送）
    public String process;//打印进度（第1/3杯）
    public String productName;//商品名称
    public String productEnName;//商品英文名称
    public String size;//大小
    public String makeDate;//制作时间
    public String temperature;//温度
    public List<String> additionList;//商品信息(eg: 加糖X1 )
}
