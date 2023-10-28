package com.nsw.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//@Service
@Configuration
public class RedisPoolFactory {

	@Autowired
	RedisConfig redisConfig;
	
	@Bean
	public JedisPool jedisPoolFactory() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
		poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
		poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
		//连接没有密码的redis使用下面的这个构造函数，否则会连接不成功，JedisPool里拿不到资源
		JedisPool jp = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
				redisConfig.getTimeout()*1000, null);
		//连接有密码的redis使用下面的这个构造函数
//		JedisPool jp =  new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
//				redisConfig.getTimeout()*1000, redisConfig.getPassword(), 0);
		return jp;
	}
}
