package com.nsw.miaosha.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.nsw.miaosha.access.UserContext;
import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.service.impl.MiaoshaUserServiceImpl;

/**
 * 
 * @author nsw  
 * @date 2020年12月25日  
 * @version V1.0
 */
@Service
public class UserArgumentResolvers implements HandlerMethodArgumentResolver {

//	@Autowired
//	private MiaoshaUserService userService;
	
	/**
	 * 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz == MiaoshaUser.class;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//		代码优化，把下面获取用户信息的代码放入拦截器中，拦截器获取用户信息后放入ThreadLocal中
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//		
//		String paramToken = request.getParameter(MiaoshaUserServiceImpl.COOKI_NAME_TOKEN);
//		String cookieToken = getCookieValue(request,MiaoshaUserServiceImpl.COOKI_NAME_TOKEN);
//		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//			return null;
//		}
//		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
//		return userService.getByToken(response, token);
		
		//这里直接从ThreadLocal获取用户信息
		return UserContext.getUser();
	}

//	private String getCookieValue(HttpServletRequest request, String cookieName) {
//		Cookie[] cookies =  request.getCookies();
//		for(Cookie cookie : cookies) {
//			if(cookie.getName().equals(cookieName)) {
//				return cookie.getValue();
//			}
//		}
//		return null;
//	}

}
