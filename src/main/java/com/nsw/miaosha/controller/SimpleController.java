package com.nsw.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nsw.miaosha.domain.User;
import com.nsw.miaosha.rabbitmq.MQSender;
import com.nsw.miaosha.redis.RedisService;
import com.nsw.miaosha.redis.UserKey;
import com.nsw.miaosha.result.Result;

public class SimpleController {

	@Autowired
	private RedisService redisService;
	
	@Autowired
	private MQSender sender;
	
	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet(){	
		User user = redisService.get(UserKey.getById,""+1, User.class);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet(){	
		User user = new User();
		boolean ret = redisService.set(UserKey.getById,""+1, user);//UserKey:id1
		//String str = redisService.get("key2", String.class);
		return Result.success(ret);
	}
	
	@RequestMapping("/mq")
	@ResponseBody
	public Result<String> mq(){	
//		sender.send("hello world");
		return Result.success("hello world");
	}
	
	@RequestMapping("/mq/topic")
	@ResponseBody
	public Result<String> topic(){	
//		sender.sendTopic("hello world");
		return Result.success("hello world");
	}
	
	@RequestMapping("/mq/fanout")
	@ResponseBody
	public Result<String> fanout(){	
//		sender.sendFanout("hello world");
		return Result.success("hello world");
	}
	
	@RequestMapping("/mq/header")
	@ResponseBody
	public Result<String> header(){	
//		sender.sendHeader("hello world");
		return Result.success("hello world");
	}
	
}
