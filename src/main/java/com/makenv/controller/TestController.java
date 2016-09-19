package com.makenv.controller;

import com.makenv.condition.StationDetailCondition;
import com.makenv.config.SpeciesConfig;
import com.makenv.domain.City;
import com.makenv.service.StationDetailService;
import com.makenv.service.StationService;
import com.makenv.service.impl.StationDetailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/6.
 */
@RestController
@RequestMapping("makenv")
public class TestController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private StationService stationService;

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private SpeciesConfig speciesConfig;

/*    @Resource
    private RedisTemplate RedisTemplate;*/

    @RequestMapping("/test")
    @ResponseBody
    public Map test(HttpServletRequest request){


        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());

        String beanDefinitionName[] =applicationContext.getBeanDefinitionNames();

        Object obj = applicationContext.getBean("stationDetailServiceImpl");
        System.out.println(obj);

         return new HashMap();
    }

    @RequestMapping("/testCache")
    public List test(City city){

        LocalDateTime startTime = LocalDateTime.of(2016,8,1,0,0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        stationDetailService.getRankResultDataByArea(new StationDetailCondition(),startTime,endTime);

        return null;
    }



}