package com.nsw.miaosha.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.nsw.miaosha.access.AccessInterceptor;

/**
 * springboot 2.x之后WebMvcConfigurerAdapter过时，不推荐使用，推荐使用 WebMvcConfigurer
 * 这里使用 extends WebMvcConfigurationSupport 来代替 extends WebMvcConfigurerAdapter
 * @author nsw  
 * @date 2020年12月25日  
 * @version V1.0
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

	@Autowired
	UserArgumentResolvers userArgumentResolvers;
	
	@Autowired
	AccessInterceptor accessInterceptor;
	
	//注册参数的封装
	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//往controller中的参数赋值
		argumentResolvers.add(userArgumentResolvers);
	}
	
	
	//注册拦截器
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
	
	
	/**
	 *  继承了WebMvcConfigurationSupport后，静态资源失效的问题
	 * 资料1：-->比较正确
	 * 原因看WebMvcAutoConfiguration中源码，有@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
	 * 只有在没有WebMvcConfigurationSupport这个类的情况下才支持springmvc的自动配置
	 * 可以改成 implements WebMvcConfigurer  -->但是这里需要重载addArgumentResolvers，
	 * 资料2
	 * WebMvcConfigurationSupport是Spring Boot2.0以后用来替代WebMvcConfigurerAdapter，但是如果直接使用WebMvcConfigurationSupport替换掉WebMvcConfigurerAdapter会出现各种问题。
	 * 原因就是使用WebMvcConfigurationSupport时只是使用了SpringMVC的基本配置，webmvc自动化配置就会失效，导致静态资源使用不了。需要在继承类中处理。
	 * 最好的方法是：使用implements WebMvcConfigure替换extends WebMvcConfigurationSupport，这样就就可以在原来的自动化配置上扩展自己的功能。
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       
        registry.addResourceHandler("/**")
        		.addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/public/");
    }
}
