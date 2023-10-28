package com.nsw.miaosha.service;

import javax.servlet.http.HttpServletResponse;

import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.vo.LoginVo;

public interface MiaoshaUserService {

	
	public String login(HttpServletResponse response, LoginVo loginVo);

	public MiaoshaUser getByToken(HttpServletResponse response, String token);
	
}
