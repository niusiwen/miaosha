package com.nsw.miaosha.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nsw.miaosha.access.AccessLimit;
//import com.nsw.miaosha.access.AccessLimit;
import com.nsw.miaosha.domain.MiaoshaOrder;
import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.domain.OrderInfo;
import com.nsw.miaosha.rabbitmq.MQSender;
import com.nsw.miaosha.rabbitmq.MiaoshaMessage;
import com.nsw.miaosha.redis.AccessKey;
import com.nsw.miaosha.redis.GoodsKey;
//import com.nsw.miaosha.rabbitmq.MiaoshaMessage;
//import com.nsw.miaosha.redis.GoodsKey;
import com.nsw.miaosha.redis.MiaoshaKey;
import com.nsw.miaosha.redis.OrderKey;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.result.CodeMsg;
import com.nsw.miaosha.result.Result;
import com.nsw.miaosha.service.GoodsService;
import com.nsw.miaosha.service.MiaoshaService;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.service.OrderService;
import com.nsw.miaosha.vo.GoodsVo;

/**
 * 秒杀相关的接口
 * @author nsw  
 * @date 2020年12月30日  
 * @version V1.0
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	@Autowired
	MQSender sender;
	
	/**
	 * 记录商品是否秒杀结束的变量
	 */
	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
	
	/**
	 * 系统初始化-->将商品库存数量加载到redis缓存
	 * */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
	
//	@RequestMapping(value="/reset", method=RequestMethod.GET)
//    @ResponseBody
//    public Result<Boolean> reset(Model model) {
//		List<GoodsVo> goodsList = goodsService.listGoodsVo();
//		for(GoodsVo goods : goodsList) {
//			goods.setStockCount(10);
//			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
//			localOverMap.put(goods.getId(), false);
//		}
//		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
//		redisService.delete(MiaoshaKey.isGoodsOver);
//		miaoshaService.reset(goodsList);
//		return Result.success(true);
//	}
	
	/**
	 * 优化前的秒杀接口
	 * 
	 */
	@RequestMapping(value="/do_miaosha2")
	public String miaosha2(Model model,MiaoshaUser user,
  		@RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return "login";
		}
		//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getGoodsStock();
		if(stock <= 0) {
			model.addAttribute("errorMsg", CodeMsg.MIAO_SHA_OVER.getMsg());
			return "miaosha_fail";
		}
		//判断是否已经秒杀到了，不能重复秒杀
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			model.addAttribute("errorMsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
			return "miaosha_fail";
		}
		//减库存，下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		model.addAttribute("orderInfo", orderInfo);
		model.addAttribute("goods", goods);
		return "order_detail";
	}
	
	/**
	 * GET POST有什么区别
	 * GET幂等性 POST不是幂等性
	 * 秒杀接口优化1：前后端分离
	 * 
	 * 秒杀优化2： 减少数据库的访问
	 * 1、系统初始化，把商品库存数量加载到redis
	 * 2、收到请求，redis预减库存，库存不足，直接返回，否则进入3
	 * 3、请求入队，立即返回排队中
	 * 4、请求出队，生成订单，减少库存
	 * 5、客户端轮询，是否秒杀成功
	 * */
    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,
    		@PathVariable("path") String path) {
    	
    	//model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	
    	//验证path 安全优化：隐藏秒杀地址
    	boolean check = miaoshaService.checkPath(user, goodsId, path);
    	if(!check){
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	//内存标记，减少redis访问
    	boolean over = localOverMap.get(goodsId);
    	if(over) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	/**
    	 * 优化后的逻辑-->异步下单
    	 */
    	//预减库存
    	long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
    	if(stock < 0) {
    		//库存小于0，记录商品已经秒杀结束
    		localOverMap.put(goodsId, true);
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//入队
    	MiaoshaMessage mm = new MiaoshaMessage();
    	mm.setUser(user);
    	mm.setGoodsId(goodsId);
    	sender.sendMiaoshaMessage(mm);
    	return Result.success(0);//排队中
    	
    	/**
    	 * 原秒杀的逻辑-->同步下单
    	 */
    	//1、判断库存
//    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
//    	int stock = goods.getStockCount();
//    	if(stock <= 0) {
//    		return Result.error(CodeMsg.MIAO_SHA_OVER);
//    	}
//    	//2、判断是否已经秒杀到了
//    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//    	if(order != null) {
//    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
//    	}
//    	//3、减库存 下订单 写入秒杀订单
//    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//      return Result.success(orderInfo);
        
    }
    
    /**
     * 前端轮询该接口，判断是否秒杀成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	//model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
    	return Result.success(result);
    }
    
    /**
     * 获取秒杀的地址
     * 安全优化:隐藏秒杀地址
     * 
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,
    		@RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    		) {
    	
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//查询访问次数 进行访问次数限制  -->代码优化 写成注解
//    	String uri = request.getRequestURI();
//    	String key = uri + "_" +user.getId();
//    	Integer count = redisService.get(AccessKey.access, key, Integer.class);
//    	if(count == null) {
//    		redisService.set(AccessKey.access, key, 1);
//    	}else if(count < 5) {
//    		redisService.incr(AccessKey.access, key);
//    	}else {
//    		return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//    	}
    	
    	boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
    	if(!check) {  
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	String path  =miaoshaService.createMiaoshaPath(user, goodsId);
    	return Result.success(path);
    }
    
    
    /**
     * 秒杀验证码
     * 
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	try {
    		BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
    		OutputStream out = response.getOutputStream();
    		ImageIO.write(image, "JPEG", out);
    		out.flush();
    		out.close();
    		return null;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return Result.error(CodeMsg.MIAOSHA_FAIL);
    	}
    }
}
