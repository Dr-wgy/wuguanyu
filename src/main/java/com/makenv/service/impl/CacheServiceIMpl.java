package com.makenv.service.impl;

import com.makenv.cache.RedisCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wgy on 2016/11/30.
 */
@Service
public class CacheServiceImpl {

    @Resource
    private RedisCache redisCache;

    public void buildCache() {



    }
}
