package com.nsw.miaosha.access;

import com.nsw.miaosha.domain.MiaoshaUser;

public class UserContext {

	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}
	
	public static void remove(){
		userHolder.remove();
    }

}
