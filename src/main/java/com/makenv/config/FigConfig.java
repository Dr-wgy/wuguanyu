package com.makenv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by wgy on 2016/7/10.
 */
@Configuration
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

}
