package com.nsw.miaosha.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsw.miaosha.dao.GoodsDao;
import com.nsw.miaosha.domain.MiaoshaGoods;
import com.nsw.miaosha.service.GoodsService;
import com.nsw.miaosha.vo.GoodsVo;

@Service
public class GoodsServiceImpl implements GoodsService {
	
	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int ret = goodsDao.reduceStock(g);
		return ret > 0;
	}

	public void resetStock(List<GoodsVo> goodsList) {
		for(GoodsVo goods : goodsList ) {
			MiaoshaGoods g = new MiaoshaGoods();
			g.setGoodsId(goods.getId());
			g.setStockCount(goods.getStockCount());
			goodsDao.resetStock(g);
		}
	}
	
	
	
}
