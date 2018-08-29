package com.print.mylo.printdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wjd on 2017/8/9.
 */

public class OrderPrintReceiptBean implements Serializable {
    public String code;//   取餐码
    public Long orderId;//   orderId
    public String orderNo;//   订单编号

    public String type;//  取餐类型（外带、外送）
    public String shopName;// 门店名称
    public String shopTelephone;// 门店电话
    public String shopAddress;//   门店地址
    public Double dispatchFee;//   配送费
    public Double coffeeShop;//   咖啡库
    public Double discount;//   减免金额
    public Double payAmount;//   实付金额
    public String payTypeName;//   支付方式名称
    public String dispatchAddress;//   收货人地址
    public String memberName;//   收货人姓名
    public String memberMobile;//   收货人手机
    public String memberSex;//   收货人性别
    public String remark;//   备注
    public String makeDate;//   制作时间

    public String qrCodeDesc;//   二维码描述语
    public String endEnDesc;//   结尾英文描述语
    public String endDesc;//   结尾中文描述语
    public String marketingInfo;//营销信息
    public String weChatPublic;//微信公众号前缀


    public List<ItemBean> productList;//商品信息


    public static class ItemBean implements Serializable {
        public Integer number;//	商品数量
        public String name;//	商品名称
        public String enName;//	商品名称英文名
        public Double amount;//	商品金额
        public String code;//商品代码
        public List<String> additionList;//附属品信息(eg: 加糖X1 )

    }

}
