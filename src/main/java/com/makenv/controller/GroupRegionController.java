package com.makenv.controller;

import com.makenv.domain.GroupRegion;
import com.makenv.resultAppend.GroupRegionResult;
import com.makenv.service.GroupRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/12/4.
 */
@RestController
@RequestMapping("/api")
public class GroupRegionController extends BaseController {

    @Autowired
    private GroupRegionService groupRegionService;

    @RequestMapping(value = "/getAllGroupRegionCode")
    public Map<String,Object> getAllGroupRegionCode() {

        Map map = new HashMap();

        List<GroupRegion> groupRegions = groupRegionService.getAllGroupRegionCode();

        map.put(RESULT,SUCCESS);

        map.put(DATA,groupRegions);

        return map;
    }


    @RequestMapping("/getGroupRegionCode")
    public Map<String,Object> getSpeGroupRegionCode(@RequestParam("groupId")String groupId){

        Map map = new HashMap();

        GroupRegion groupRegion = groupRegionService.selectGroupRegionByID(groupId);

        map.put(RESULT,SUCCESS);

        map.put(DATA,new GroupRegionResult(groupRegion).getResult());

        return map;
    }

    @RequestMapping("/updateGroupRegion")
    public Map<String,Object> updateGroupRegion(@RequestParam("groupId")String groupId,@RequestParam(value = "regionCode[]")List<String> regionCodes,String describe){

        Map map = new HashMap();

        GroupRegion groupRegion = groupRegionService.updateGroupRegion(groupId, regionCodes, describe);

        if(groupRegion != null) {

            map.put(RESULT,SUCCESS);

            map.put(RESULT,new GroupRegionResult(groupRegion).getResult());

        }
        else {

            map.put(RESULT,FAILED);

            map.put(INFO,"修改请联系管理员");
        }

        return map;
    }


    @RequestMapping("/addGroupRegion")
    public Map<String,Object> addGroupRegion(@RequestParam(value = "regionCode[]")List<String> regionCodes,String describe){

        Map map = new HashMap();

        GroupRegion groupRegion = groupRegionService.addGroupRegion(regionCodes,describe);

        if(groupRegion != null) {

            map.put(RESULT,SUCCESS);

            map.put(DATA,new GroupRegionResult(groupRegion).getResult());

        }
        else {

            map.put(RESULT,FAILED);

            map.put(INFO,"添加失败请联系管理员");
        }

        return map;
    }

    @RequestMapping("/delGroupRegion")
    public Map<String,Object> delGroupRegion(@RequestParam("groupId")String groupId){

        Map map = new HashMap();

        boolean flag = groupRegionService.delGroupRegion(groupId);

        if(flag) {

            map.put(RESULT,SUCCESS);

        }
        else {

            map.put(RESULT,FAILED);

            map.put(INFO,"数据库中无此id");
        }


        return map;
    }
}
