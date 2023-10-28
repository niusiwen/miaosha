package com.nsw.miaosha.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

	public static final String MIAOSHA_QUEUE = "miaosha.queue";
	
	//测试
	public static final String QUEUE = "queue";
	public static final String TOPIC_QUEUE1 = "topic.queue1";
	public static final String TOPIC_QUEUE2 = "topic.queue2";
	public static final String HEADER_QUEUE = "header.queue";
	
	public static final String TOPIC_EXCHANGE = "topicExchange";
	public static final String FANOUT_EXCHANGE = "fanoutExchage";
	public static final String HEADERS_EXCHANGE = "headersExchage";
	
	/**
	 * 秒杀的队列MIAOSHA_QUEUE 使用Direct模式--> 使用默认的交换机
	 * 这里应为生产者和消费者在一个项目中，无法在第一次启动时，先启动Provider再启动Consumer
	 * 说要在配置中把队列手动声明出来
	 * @return
	 */
	@Bean
	public Queue queue() {
		return new Queue(MIAOSHA_QUEUE, true);
	}
	
	
	/**
	 * Direct模式 使用默认的交换机-->交换机Exchange
	 * @return
	 */
//	@Bean
//	public Queue queue() {
//		return new Queue(QUEUE, true);
//	}
	
	/**
	 * Topic模式 路由可以使用通配符-->交换机Exchange
	 * @return
	 */
//	@Bean
//	public Queue topicQueue1() {
//		return new Queue(TOPIC_QUEUE1, true);
//	}
//	@Bean
//	public Queue topicQueue2() {
//		return new Queue(TOPIC_QUEUE2, true);
//	}
//	@Bean
//	public TopicExchange topicExchange() {
//		return new TopicExchange(TOPIC_EXCHANGE);
//	}
//	@Bean
//	public Binding topicBinding1() {
//		return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1"); 
//	}
//	@Bean
//	public Binding topicBinding2() {
//		return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#"); 
//	}
	
	/**
	 * Fanout模式(广播模式) 不需要定义路由规则 -->交换机Exchange
	 * @return
	 */
//	@Bean
//	public FanoutExchange fanoutExchage(){
//		return new FanoutExchange(FANOUT_EXCHANGE);
//	}
//	@Bean
//	public Binding FanoutBinding1() {
//		//这里直接使用了topic模式定义的两个队列
//		return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
//	}
//	@Bean
//	public Binding FanoutBinding2() {
//		return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
//	}
	
	/**
	 * Header模式 -->交换机Exchange
	 * @return
	 */
//	@Bean
//	public HeadersExchange headersExchage(){
//		return new HeadersExchange(HEADERS_EXCHANGE);
//	}
//	@Bean
//	public Queue headerQueue1() {
//		return new Queue(HEADER_QUEUE, true);
//	}
//	@Bean
//	public Binding headerBinding() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("header1", "value1");
//		map.put("header2", "value2");
//		return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
//	}
	
	
}
