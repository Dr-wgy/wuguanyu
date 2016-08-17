package com.makenv.controller;

import com.makenv.cache.RedisCache;
import com.makenv.config.SpeciesConfig;
import com.makenv.domain.City;
import com.makenv.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private SpeciesConfig speciesConfig;

/*    @Resource
    private RedisTemplate RedisTemplate;*/

    @RequestMapping("/test")
    public Map test(HttpSession session){

         return new HashMap();
    }

    @RequestMapping("/testCache")
    public List test(City city){

        //speciesConfig.getAQIFilter();

        return stationService.getStationByCity1(city);
    }
}