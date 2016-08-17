package com.makenv.service;

import com.makenv.domain.City;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 * 城市服务调用这个服务一定和城市有联系
 */
public interface CityService {

    List<Map<String,Object>> getAllCity();

}
