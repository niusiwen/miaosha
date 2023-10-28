package com.nsw.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsw.miaosha.dao.GoodsDao;
import com.nsw.miaosha.domain.MiaoshaGoods;
import com.nsw.miaosha.vo.GoodsVo;

public interface GoodsService {

	public List<GoodsVo> listGoodsVo();

	public GoodsVo getGoodsVoByGoodsId(long goodsId);

	public boolean reduceStock(GoodsVo goods);

	public void resetStock(List<GoodsVo> goodsList) ;
	
	
	
	
}
