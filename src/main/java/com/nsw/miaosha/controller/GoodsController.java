package com.nsw.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.redis.GoodsKey;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.result.Result;
import com.nsw.miaosha.service.GoodsService;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.service.impl.MiaoshaUserServiceImpl;
import com.nsw.miaosha.vo.GoodsDetailVo;
import com.nsw.miaosha.vo.GoodsVo;

/**
 * 商品相关的接口 
 * @author nsw  
 * @date 2020年12月30日  
 * @version V1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	RedisService redisService;
	
	//Thymeleaf的渲染工具
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	/*
	 * 商品列表
	 * 页面相关的优化：
	 * 1、页面缓存：1.取缓存 2.手动渲染模板 3.结果输出 -->主要是防止一瞬间的并发量过大，缓存过期时间不易过长
	 * 2、页面静态化，前后端分离
	 */
	 @RequestMapping(value="/to_list", produces="text/html;charset=utf-8")
	 @ResponseBody  //
	 public String list(Model model,
//			 @CookieValue(value=MiaoshaUserServiceImpl.COOKI_NAME_TOKEN,required=false) String cookieToken,
//			 @RequestParam(value=MiaoshaUserServiceImpl.COOKI_NAME_TOKEN,required=false) String paramToken,
			 HttpServletRequest request, HttpServletResponse response,
			 MiaoshaUser user
	    	) {
		 //代码优化-->这里使用WebMvcConfigurationSupport配置将参数配置进去
//		 if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//			 return "login";
//		 }
//		 String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
//		 MiaoshaUser user = userService.getByToken(response, token);
		
		 model.addAttribute("user", user);
		 
		 //代码优化
		 //1.取缓存
		 String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		 if(!StringUtils.isEmpty(html)) {
			 return html;
		 }
		 
		 //查询商品列表
		 List<GoodsVo> goodsList = goodsService.listGoodsVo();
		 model.addAttribute("goodsList", goodsList);
		 
//		 return "goods_list";
		 
		 //org.thymeleaf.spring4 提供的WebContext
//		 SpringWebContext ctx = new SpringWebContext(request,response,
//	    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		 
		 //thymeleaf.spring5的API中把大部分的功能移到了IWebContext下面,用来区分边界。
		 //剔除了ApplicationContext 过多的依赖，现在thymeleaf渲染不再过多依赖spring容器
		 IWebContext ctx = new WebContext(request,response,
	                request.getServletContext(),request.getLocale(),model.asMap());
		 
		 //2.手动渲染
		 //thymeleafViewResolver.setCharacterEncoding("UTF-8");
		 html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		 if(!StringUtils.isEmpty(html)) {
			 redisService.set(GoodsKey.getGoodsList, "", html);
		 }
		 return html;
	 }
	 
	 
	 /*
	  * 商品详情
	  * 优化：url缓存-->也是对页面进行缓存，但是不同的url展示的页面是不一样的，所以叫做URL缓存
	  */
	@RequestMapping(value = "/to_detail2/{goodsId}", produces="text/html;charset=utf-8")
	@ResponseBody
	public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,
			MiaoshaUser user, @PathVariable("goodsId") long goodsId) {
		model.addAttribute("user", user);
		
		//代码优化
		 //1.取缓存
		 String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		 if(!StringUtils.isEmpty(html)) {
			 return html;
		 }
		
		 //2.手动渲染
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods",goods);
		
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {// 秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {// 秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {// 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		model.addAttribute("miaoshaStatus",miaoshaStatus);
		model.addAttribute("remainSeconds",remainSeconds);
	
//		return "goods_detail";
		
		IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap());
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;
	}
	 
	
	/**
	 * 详情页面优化：前后端分离 
	 *
	 */
	@RequestMapping(value = "/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model,
			MiaoshaUser user, @PathVariable("goodsId") long goodsId) {
		
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
	
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {// 秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {// 秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {// 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
	
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
	}
	 
}
