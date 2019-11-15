package com.unifig.organ.constant;

public class Constants {

    /*
     * 密码加密key
     */
    public static final String ENCRYPT_KEY = "0hAbCcb^5etYXpRU9r835dSQtsA16!EhYCwFaX8N4hN&D8Z%fn";


    /*
     * 1
     */
    public static final String DEFAULT_VAULE_FU_TWO = "-2";

    /*
     * 1
     */
    public static final String DEFAULT_VAULE_FU_ONE = "-1";

    /*
     * 0
     */
    public static final String DEFAULT_VAULE_ZERO = "0";
    public static final int DEFAULT_VAULE_INT_ZERO = 0;

    /*
     * 1
     */
    public static final String DEFAULT_VAULE_ONE = "1";
    public static final int DEFAULT_VAULE_INT_ONE = 1;
    /*
     * 2
     */
    public static final String DEFAULT_VAULE_TWO = "2";
    public static final int DEFAUL_INT_VAULE_TWO = 2;

    public static final String DEFAULT_VAULE_FOR = "4";

    /*
     * 未知
     */
    public static final String DEFAULT_STRING = "未知";

    /*
     * 	60001个体司机；60002内部人员；60003公司用户，60004货主用户，60006平台用户 ,60007 部门人员 , 60008 客服  60009 员工 60010 销售 60011 组织内司机
     */
    public static final String USER_TYPE_INSIDE_DRIVER = "60001";

    public static final String USER_TYPE_OUTSIDE_DRIVER = "60002";

    public static final String USER_TYPE_OUTSIDE_COMPANY = "60003";

    public static final String USER_TYPE_OUTSIDE_SHIPPER = "60004";

    public static final String USER_TYPE_OUTSIDE_PLATFORM = "60006";

    public static final String USER_TYPE_IN_DEPT = "60007";

    public static final String USER_TYPE_IN_COMPANY_DRIVER = "60011";


    public static final int ORDER_STATUS_CODE_PENDING = 98;

    public static final int ORDER_STATUS_CODE_MEET = 100;


    public static final int ORDER_STATUS_CODE_TAKE_GOODS = 101;


    public static final int ORDER_STATUS_CODE_RELEASE_GOODS = 107;

    /**
     * 货品运输状态 -1 未指派； 0 待指派 ；11指派中 ；12待提货 ；1 运输中  ； 2 已送达 ； 3 待确认； 4运输完成
     *
     * "93":"撤回发布","94":"发货中","95":"抢单中","96":"指派中","961":"拒绝接单","97":"竞价中","98":"待审核","99":"报价中","100":"待接单","101":"待提货",
     * "102":"运输中","103":"待确认","104":"已完成","105":"异常运单","106":"驳回运单","107":"待发布"
     *
     * 订单状态 200-抢单中；201-指派中；202-竞价中
     *
     * 竞价状态         竞价中 600 竞价成功 601 竞价失败 602  非竞价单 603
     *
     * 车辆
     * 20011审核通过,20012审核驳回，20013待审核
     *
     *
     * 是否是我创建单 0 是接的单  1 是发的单
     *
     * myOrderStatus= Constants.DEFAULT_VAULE_ZERO;
     *
     *  {"95":"抢单中","96":"指派中","961":"拒绝接单","97":"竞价中","98":"待审核","99":"报价中","100":"待接单","101":"待提货","102":"运输中","103":"待确认","104":"已完成","105":"异常运单","106":"驳回运单","107":"待调度","108":"驳回后数据回滚 此状态不做续选"}
     */


    //=========================货品状态======start===================================
    /**
     * 平台 未指派 货品
     */
    public static final String GOODS_NOT_DESIGNATE = "-1";
    /**
     * 物流公司 未指派 货品
     */
    public static final String GOODS_WAIT_DESIGNATE = "0";
    /**
     * 指派中 货品
     */
    public static final String GOODS_STATUS_DESIGNATE = "11";
    /**
     * 待提货 货品
     */
    public static final String GOODS_WAITING_PICK_UP_GOODS = "12";
    /**
     * 运输中 货品
     */
    public static final String GOODS_IN_TRANSIT = "1";
    /**
     * 已送达 货品
     */
    public static final String GOODS_TO_DESTINATION = "2";

    //=========================货品状态======start===================================


    //=========================运单状态======start===================================

    /**
     * 货源发布中
     */
    public static final String ORDER_STATUS_SUPPLY = "94";

    /**
     * 指派中
     */
    public static final String ORDER_STATUS_DESIGNATE = "96";
    /**
     * 拒绝接单
     */
    public static final String ORDER_STATUS_REFUSE_ORDER = "961";


    /**
     * 抢单中
     */
    public static final String ORDER_STATUS_GRAB = "95";

    /**
     * 竞价中
     */
    public static final String ORDER_STATUS_BIDDING = "97";

    /**
     * 待提货
     */
    public static final String ORDER_WAITING_PICK_UP_GOODS = "101";
    /**
     * 运输中
     */
    public static final String ORDER_IN_TRANSIT = "102";
    /**
     * 待确认
     */
    public static final String ORDER_WAIT_FOR_CONFIRMATION = "103";
    /**
     * 已完成
     */
    public static final String ORDER_HAVA_COMPLETED = "104";
    /**
     * 异常运单
     */
    public static final String ORDER_IN_EXCEPTION = "105";

    /**
     * 驳回运单
     */
    public static final String ORDER_IN_EXCEPTION_BILL = "106";

    /**
     * 数据回滚了
     */
    public static final String ORDER_IN_EXCEPTION_ORDER = "108";

    //=========================运单状态======end===================================


    //=========================竞价状态======start===================================

    /**
     * 竞价中
     */
    public static final String ORDER_BIDD_ING = "600";
    /**
     * 竞价成功
     */
    public static final String ORDER_BIDD_OK = "601";
    /**
     * 竞价失败
     */
    public static final String ORDER_BIDD_NO = "602";

    /**
     * 没有竞价数据
     */
    public static final String ORDER_NO_BIDD = "603";

    //=========================竞价状态======end===================================


    //=========================user======start===================================

    /**
     * 800 新增;  801 待审核；802-通过；803-拒绝；
     */
    public static final String USER_IS_NEW = "800";

    /**
     *
     */
    public static final String USER_IS_WAIT = "801";

    /**
     *
     */
    public static final String USER_IS_OK = "802";

    /**
     *
     */
    public static final String USER_IS_NO = "803";
    //=========================user======end===================================


    /**
     * 车辆类型
     * 平板-20001
     * 高栏-20002
     * 厢式-20003
     * 高低板-20004
     * 冷藏车-20005
     * 危险品-20006
     * <p>
     * <p>
     * 车辆尺寸-单位cm
     * 620-6.2米
     * 680-6.8米
     * 700-7.0米
     * 860-8.6米
     * 960-9.6米
     * 1250-12.5米
     * 1300-13米
     * 1350-13.5米
     * 1600-16米
     * 1750-17.5米
     */


    /**
     * 异常状态
     * 50000  提货单异常    50001  提货单异常待审核
     * 50002  回执单异常    50003  回执单异常待审核
     * 50004  运输延时      50005  运输延时待审核
     * 50006  货物亏损      50007  货物亏损待审核
     * 50008  货物遗失      50009  货物遗失待审核
     * 50010  货物异常      50011  货物异常待审核
     * 处理方式
     * 50012 重传提货单      50013 重传回执单
     * 50014 扣费           50015 补费
     * 50016 异常处理完毕
     */
    public static final String ORDER_EX_50000 = "50000";
    public static final String ORDER_EX_50001 = "50001";
    public static final String ORDER_EX_50002 = "50002";
    public static final String ORDER_EX_50003 = "50003";
    public static final String ORDER_EX_50004 = "50004";
    public static final String ORDER_EX_50005 = "50005";
    public static final String ORDER_EX_50006 = "50006";
    public static final String ORDER_EX_50007 = "50007";
    public static final String ORDER_EX_50008 = "50008";
    public static final String ORDER_EX_50009 = "50009";
    public static final String ORDER_EX_50010 = "50010";
    public static final String ORDER_EX_50011 = "50011";
    public static final String ORDER_EX_50012 = "50012";
    public static final String ORDER_EX_50013 = "50013";
    public static final String ORDER_EX_50014 = "50014";
    public static final String ORDER_EX_50015 = "50015";
    public static final String ORDER_EX_50016 = "50016";


    public static final String SHARE_SUCCESS = "SHARE_SUCCESS";
    public static final String USER_BUY = "USER_BUY";

}
