package com.makenv.config;

import com.makenv.serializer.LocalDateTimeResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
/*@ComponentScan(basePackages = "com.makenv.config")*/
@EnableTransactionManagement(order = 2)
@EnableWebMvc
@EnableAspectJAutoProxy
public class RootConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        argumentResolvers.add(new LocalDateTimeResolver());
    }
}