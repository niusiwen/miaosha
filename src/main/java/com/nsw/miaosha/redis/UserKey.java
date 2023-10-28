package com.nsw.miaosha.redis;

/**
 * 用户模块的key
 * @author nsw
 *
 */
public class UserKey extends BasePrefix {

	
	public UserKey( String prefix) {
		super( prefix);
		
	}
	
	public static UserKey getById = new UserKey("id");
	public static UserKey getByName = new UserKey("name");

}
