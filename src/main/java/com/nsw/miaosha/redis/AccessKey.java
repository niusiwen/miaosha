package com.nsw.miaosha.redis;

public class AccessKey extends BasePrefix{

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
	
//	public static AccessKey access = new AccessKey(5, "access");//过期时间5s
}
