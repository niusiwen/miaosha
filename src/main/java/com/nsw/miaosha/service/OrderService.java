package com.nsw.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nsw.miaosha.dao.OrderDao;
import com.nsw.miaosha.domain.MiaoshaOrder;
import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.domain.OrderInfo;
import com.nsw.miaosha.redis.OrderKey;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.vo.GoodsVo;

public interface OrderService {
	
	
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId); 
	
	public OrderInfo getOrderById(long orderId); 
	
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods);

	public void deleteOrders() ;

}
