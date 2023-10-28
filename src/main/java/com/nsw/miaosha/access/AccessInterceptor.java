package com.nsw.miaosha.access;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.redis.AccessKey;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.result.CodeMsg;
import com.nsw.miaosha.result.Result;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.service.impl.MiaoshaUserServiceImpl;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 
		if(handler instanceof HandlerMethod) {
			MiaoshaUser user = getUser(request,response);
			//todo 查询为空，即用户未登录处理
			
			//将用户信息放入ThreadLocal中
			UserContext.setUser(user);
			//限流的注解
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit ==null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			
			String key = request.getRequestURI();
			if(needLogin) {
				if(user ==null) {
					//返回响应信息
					render(response, CodeMsg.SESSION_ERROR);
					return false;
				}
				key += "_" + user.getId();
			}else {
				//do nothing
			}
			//mertine flower,重构-改善 既有代码的设计
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count == null) {
	    		redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		redisService.incr(ak, key);
	    	}else {
	    		render(response, CodeMsg.ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		//请求结束，移除ThreadLocal中的用户信息
		UserContext.remove();
	}

	
	//返回客户端
	private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str = JSON.toJSONString(Result.error(codeMsg));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	//获取用户信息
	private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoshaUserServiceImpl.COOKI_NAME_TOKEN);
		String cookieToken = getCookieValue(request,MiaoshaUserServiceImpl.COOKI_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		return userService.getByToken(response, token);
	}
	
	private String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies =  request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
