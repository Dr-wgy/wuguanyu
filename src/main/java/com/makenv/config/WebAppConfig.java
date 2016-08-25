package com.makenv.config;

import com.makenv.interceptor.SelfInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by wgy on 2016/8/8.
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private SelfInterceptor selfInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(selfInterceptor).addPathPatterns("/makenv/self/**");
    }
}
