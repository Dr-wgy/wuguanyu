package com.makenv.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.makenv.common.DynamicDataSource;
import com.makenv.enums.DataBaseType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mockito.InjectMocks;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wgy on 2016/8/5.
 */
@Configuration
@MapperScan("com.makenv.mapper")
public class DataSourceConfig {

    //配置数据源primary数据源1
    @Bean(name="dataSource1")
    @ConfigurationProperties(prefix="spring.datasource1")
    public DataSource dataSource1() {

        return new DruidDataSource();
    }

    //配置数据源2
   /* @Bean(name="dataSource2")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource dataSource2(){
        return new DruidDataSource();
    }*/


    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource(){

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();

        targetDataSources.put(DataBaseType.db1, dataSource1());

        //targetDataSources.put(DataBaseType.db2, dataSource2());


        dynamicDataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法

        dynamicDataSource.setDefaultTargetDataSource(dataSource1());

        return dynamicDataSource;
    }



    //配置mybatisSqlSession
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        //保证HashMap返回后必须要即使是null值也要返回
/*        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCallSettersOnNulls(true);
        sqlSessionFactoryBean.setConfiguration(configuration);*/
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }


    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {

        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
