package com.nsw.miaosha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nsw.miaosha.domain.MiaoshaOrder;
import com.nsw.miaosha.domain.MiaoshaUser;
import com.nsw.miaosha.domain.OrderInfo;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.util.MD5Util;
import com.nsw.miaosha.util.UUIDUtil;
import com.nsw.miaosha.vo.GoodsVo;

public interface MiaoshaService {


	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods);

	public long getMiaoshaResult(Long userId, long goodsId);

//	private void setGoodsOver(Long goodsId) ;
//	
//	private boolean getGoodsOver(long goodsId) ;
//	
	public void reset(List<GoodsVo> goodsList) ;

	public boolean checkPath(MiaoshaUser user, long goodsId, String path) ;

	public String createMiaoshaPath(MiaoshaUser user, long goodsId);

	public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId);
	
	public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) ;
	
	
}
