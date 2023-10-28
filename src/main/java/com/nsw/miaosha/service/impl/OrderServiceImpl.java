package com.nsw.miaosha.service.impl;

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
import com.nsw.miaosha.service.OrderService;
import com.nsw.miaosha.vo.GoodsVo;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;
	
	/**
	 * 判断用户是否已经秒杀到该商品
	 */
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		//return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		//优化，判断是否秒杀成功查缓存，不查数据库了
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}
	
	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}
	

	/**
	 * 生成订单
	 */
	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		//优化：秒杀成功，将秒杀订单存入缓存
		redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		 
		return orderInfo;
	}

	public void deleteOrders() {
		orderDao.deleteOrders();
		orderDao.deleteMiaoshaOrders();
	}

}
