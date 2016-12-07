package com.makenv;


import com.makenv.config.RootConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Date;

/**
 * Created by wgy on 2016/8/5.
 * 主函数入口
 */
@SpringBootApplication
public class MakenObsApplication {

    public static void main(String[] args) {

        SpringApplication.run(MakenObsApplication.class,args);
    }

}
