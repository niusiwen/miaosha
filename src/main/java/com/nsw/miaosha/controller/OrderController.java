package com.nsw.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.domain.OrderInfo;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.result.CodeMsg;
import com.nsw.miaosha.result.Result;
import com.nsw.miaosha.service.GoodsService;
import com.nsw.miaosha.service.MiaoshaUserService;
import com.nsw.miaosha.service.OrderService;
import com.nsw.miaosha.vo.GoodsVo;
import com.nsw.miaosha.vo.OrderDetailVo;

/**
 * 订单相关的接口 
 * @author nsw  
 * @date 2020年12月30日  
 * @version V1.0
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model,MiaoshaUser user,
    		@RequestParam("orderId") long orderId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	OrderInfo order = orderService.getOrderById(orderId);
    	if(order == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setOrder(order);
    	vo.setGoods(goods);
    	return Result.success(vo);
    }
    
}
