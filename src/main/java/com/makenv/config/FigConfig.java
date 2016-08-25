package com.makenv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by wgy on 2016/7/10.
 */
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:fig.properties")
public class FigConfig {
    public  float getXmin() {
        return xmin;
    }

    public  float getXmax() {
        return xmax;
    }

    public  float getYmin() {
        return ymin;
    }

    public  float getYmax() {
        return ymax;
    }

    public  float getStep() {
        return step;
    }

    @Value("${xmin}")
    private  float xmin;

    @Value("${xmax}")
    private  float xmax;

    @Value("${ymin}")
    private  float ymin;

    @Value("${ymax}")
    private  float ymax;

    @Value("${step}")
    private  float step;

/*
    @Bean//注入配置文件使用@propertySource指定文件,如果使用@Value需要配置下面的Bean,此方法必须是静态方法
    public static PropertySourcesPlaceholderConfigurer propertyConfigure(){

        return new  PropertySourcesPlaceholderConfigurer();
    }
*/


}
