package com.nsw.miaosha.redis;

/**
 * 秒杀模块的key
 * @author nsw
 *
 */
public class MiaoshaUserKey extends BasePrefix {

	//token过期时间 2天
	public static final int TOKEN_EXPIRE = 3600*24*2;
	
	public MiaoshaUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	//tk开头是用户登录的token缓存
	public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE ,"tk");
	//id开头是从数据库中查到的用户信息缓存
	public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
