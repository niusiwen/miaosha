package com.nsw.miaosha.redis;

/**
 * 订单模块的key
 * @author nsw
 *
 */
public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
