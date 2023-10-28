package com.nsw.miaosha.redis;

/**
 * 
 * @author nsw
 * @date 2020/12/24 
 */
public abstract class BasePrefix implements KeyPrefix {

	//过期时间
	private int expireSeconds;
	
	//前缀
	private String prefix;
	
	public BasePrefix(String prefix) {//默认0 代表永不过期
		this(0, prefix);
	}
	
	public BasePrefix(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	@Override
	public int expireSeconds() {
		
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		//获取类名
		String className = getClass().getSimpleName();
		return className + ":" + prefix;
	}

}
