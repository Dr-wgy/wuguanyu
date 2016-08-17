package com.makenv.service.impl;

import com.makenv.mapper.ProvinceMapper;
import com.makenv.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/12.
 */
@Service
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceMapper provinceMapper;

    public List<Map<String,Object>> getAllProvinceList(){

        return provinceMapper.getAllProvinces();
    }


}
