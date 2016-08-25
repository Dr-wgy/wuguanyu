package com.makenv.service.impl;

import com.makenv.mapper.CityMapper;
import com.makenv.service.CityService;
import com.makenv.vo.CityVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 */
@Service
@Transactional
public class CityServiceImpl implements CityService {

    private final static Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityMapper cityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CityVo> getAllCity() {

        return cityMapper.selectAllCity();
    }
}
