package com.nsw.miaosha.redis;

/**
 * Redis的key的前缀接口
 * @author nsw
 *
 */
public interface KeyPrefix {

	/**
	 * 有效时间
	 * @return
	 */
	public int expireSeconds();
	
	/**
	 * 获取前缀
	 * @return
	 */
	public String getPrefix();
}
