package com.nsw.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.result.CodeMsg;
import com.nsw.miaosha.result.Result;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.vo.LoginVo;

/**
 * 用户登录 
 * @author nsw  
 * @date 2020年12月30日  
 * @version V1.0
 */
@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	MiaoshaUserService userService;
	
	
	@RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }
    
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
    	log.info(loginVo.toString());
    	//参数校验———>使用validation进行优化
//    	String mobile = loginVo.getMobile();
//    	if(StringUtils.isEmpty(mobile)) {
//    		return Result.error(CodeMsg.MOBILE_EMPTY);
//    	}
//    	String formPass = loginVo.getPassword();
//		if(StringUtils.isEmpty(formPass)) {
//			return Result.error(CodeMsg.PASSWORD_EMPTY);		
//		}
    	//登录
    	String token = userService.login(response, loginVo);
    	return Result.success(token);
    }
}
