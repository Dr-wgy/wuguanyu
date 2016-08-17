package com.makenv.config;

import com.makenv.service.StationService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(value = {RedisCacheConfig.class,DataSourceConfig.class})
@EnableTransactionManagement
public class RootConfig {

}