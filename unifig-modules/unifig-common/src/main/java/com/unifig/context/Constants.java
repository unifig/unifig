package com.unifig.context;

/**
 *
 * @create 2018-11-14
 */
public class Constants {


	/**
	 * redis ratel 统一路径
	 */
	public static final String RATEL_PATH_DEF = "ratel:";

	public static final Integer DEFAULT_VAULE_FOUR = 4;
	public static final Integer DEFAULT_VAULE_THREE = 3;
	public static final Integer DEFAULT_VAULE_TOW = 2;
	public static final Integer DEFAULT_VAULE_ONE = 1;
	public static final Integer DEFAULT_VAULE_ZERO = 0;

	public static final String RATEL_PLAT_TAG = "plat";

	public static final String RATEL_ADMIN_TAG = "admin";

	public static final String RATEL_USER_ID = "userId";
	public static final String RATEL_USER_NAME = "username";
	public static final String RATEL_USER_PASSWORD = "password";
	public static final String RATEL_USER_STATUS = "status";
	public static final String RATEL_USER_OPENID = "openId";
	public static final int WE_CHAT = 0;

	//机构最多层级
	public static final Integer ORGAN_DEPT_LEVEL = 3;

	/**
	 * hexing 统一常量
	 */

	//删除状态(0:删除,1:正常)
	public static final String HEXING_DELETE_NORMAL = "1";
	public static final String HEXING_DELETE_DELETE = "0";

	//出入库单状态(0:草稿状态1:待收货2:已收货)
	public static final String HEXING_STOCKORDERS_STATUS_DRAFT = "0";
	public static final String HEXING_STOCKORDERS_STATUS_STAY = "1";
	public static final String HEXING_STOCKORDERS_STATUS_CONFIRM = "2";

	//出入库单类别(0:正常调货1:采购入库2退货流转)
	public static final String HEXING_STOCKORDERS_TYPE_NORMAL = "0";
	public static final String HEXING_STOCKORDERS_TYPE_PURCHASE = "1";
	public static final String HEXING_STOCKORDERS_TUPE_RETURN = "2";

	//车机状态(0:库存1:绑定)
	public static final String HEXING_VEHICLE_ENGINE_STATUS_STOCKE = "0";
	public static final String HEXING_STOCKORDERS_STATUS_BINDING = "1";

	//故障状态(0:故障1:正常)
	public static final String HEXING_STOCKORDERS_FAULT_STATUS_FAULT = "0";
	public static final String HEXING_STOCKORDERS_FAULT_STATUS_NORMAL = "1";

	//盘存明细类别(0:实际剩余1:差额)
	public static final String HEXING_TAKE_INVENTORY_TYPE_ACTUAL_RESIDUAL_QUANTITY = "0";
	public static final String HEXING_TAKE_INVENTORY_TYPE_DIFFERENCE = "1";

	//事故还原状态(0:待审核1:通过2:未通过)
	public static final String HEXING_ACCIDENT_RESTORE_STATUS_AUDIT = "0";
	public static final String HEXING_STOCKORDERS_STATUS_ADOPT = "1";
	public static final String HEXING_STOCKORDERS_STATUS_NOT_THROUGH = "2";


}