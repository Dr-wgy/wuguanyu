package com.makenv.service.impl;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.RedisCache;
import com.makenv.cache.StationCacheUtil;
import com.makenv.condition.StationDetailCondition;
import com.makenv.config.FigConfig;
import com.makenv.config.SpeciesConfig;
import com.makenv.constant.Contants;
import com.makenv.domain.StationDetail;
import com.makenv.enums.UnitLengthEnum;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.service.StationDetailService;
import com.makenv.task.MonthTask;
import com.makenv.task.SpecialDealer;
import com.makenv.util.DateUtils;
import com.makenv.util.RegionUtils;
import com.makenv.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/8/9.
 */
@Service
public class StationDetailServiceImpl implements StationDetailService {


    private final static Logger logger = LoggerFactory.getLogger(StationDetailServiceImpl.class);

    @Autowired
    private StationDetailMapper stationDetailMapper;

    @Resource
    private RedisCache redisCache;

    @Resource(name="redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private FigConfig figConfig;

    @Autowired
    private SpeciesConfig speciesConfig;

    @Autowired
    private AsyncServiceImpl asyncServiceImpl;

    @Override
    public List<StationDetail> selectStationDetailByTimeInterval(String startTime, String endTime) {

        return stationDetailMapper.selectStationDetailByTimeInterval(startTime,endTime);

    }

    @Override
    @Cacheable(key = "#stationCode+#range+#unit",value = "stationDetail")
    public List<Map<String, Object>> getAroundDataResult(String stationCode, Integer range, String unit) {


        if(UnitLengthEnum.KIlOMETER.getName().equals(unit) || UnitLengthEnum.KIlOMETER.getDescribe().equals(unit)) {

            StationVo station= StationCacheUtil.newInstance().getStation(stationCode);

            Double lon =  station.getLon();//获得经度

            Double lat =  station.getLat();//获得纬度

            if(!StringUtils.isEmpty(lon)&& !StringUtils.isEmpty(lat)) {

                Map<String,Object> map1 = RegionUtils.getRangeLonAndLat(lat,lon,range);

                return stationDetailMapper.selectAvgRange(map1);

            }

        }

        return null;
    }

    @Override
    @Cacheable(key = "#LonLatVo",value = "stationDetail")
    public List<Map<String, Object>> getAroundDataResult(LonLatVo lonLatVo) throws Exception {

        if(UnitLengthEnum.KIlOMETER.getName().equals(lonLatVo.getUnit()) || UnitLengthEnum.KIlOMETER.getDescribe().equals(lonLatVo.getUnit())) {

            float lat = lonLatVo.getLat();

            float lon = lonLatVo.getLon();

            if(lon < figConfig.getXmax()&& lon > figConfig.getXmin() && lat < figConfig.getYmax() && lat > figConfig.getYmin()) {

                if(!StringUtils.isEmpty(lon)&& !StringUtils.isEmpty(lat)) {

                    double range = Double.parseDouble(lonLatVo.getRange());

                    Map<String,Object> map1 = RegionUtils.getRangeLonAndLat(lat,lon,range);

                    return stationDetailMapper.selectAvgRange(map1);

                }

            }

            else {

                throw new Exception("经度纬度不在有效范围内");

            }

        }
        return null;
    }

    @Cacheable(key = "#year+#regionCode",value = "stationDetail")
    public Map<String, Object> getAvgYearResultByRegionCode(Integer year, String regionCode) {

        regionCode = RegionUtils.convertRegionCode(regionCode);

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> resultList = null;

        if(year > 2013) {

            map.put("tableName","PM_25");
        }

        else {

            map.put("tableName","Sum_All_PM25_copy");
        }

        map.put("year", year);

        List<StationVo> list = StationCacheUtil.newInstance().getStationListByCityCode(regionCode);

        map.put("stationList",  list.stream().sorted((station1, station2) -> {

            return station1.getStationId().compareTo(station2.getStationId());

        }).collect(Collectors.toList()));

        if(((List)map.get("stationList")).size() == 0){

            return null;
        }

        resultList =  stationDetailMapper.selectAvgYearResultByStationCode(map);

        //省级平均
        switch (regionCode.length()) {

            case 2:

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        item.put(key,map1);

                    });

                });

                break;

            case 4:
                //是平均加超标日期
                List<Map<String,Object>> overStandardList = stationDetailMapper.selectOverStandardGroupByDate(map);

                overStandardList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("days", value);

                        item.put(key, map1);

                    });
                });

                Map<String,Object> overStandardMap = overStandardList.get(0);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        if(overStandardMap.containsKey(key)) {

                            map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                        }

                        item.put(key, map1);

                    });

                });

                break;

            case 6:


                break;


            default:
                break;

        }

        if(resultList != null && resultList.size() == 1) {

            return resultList.get(0);
        }

        return null;
    }



    @Override
    @Cacheable(key = "#regionCode+#timeSpan+#unit",condition = "#unit.equals('m')",value = "stationDetail")
    public Map<String, Object> getLastTimeSpanResultData(String regionCode, Integer timeSpan, String unit) {

        regionCode = RegionUtils.convertRegionCode(regionCode);

        LocalDateTime endTime = LocalDateTime.now();

        LocalDateTime startTime = null;

        String dateFunction = "";

        switch(unit) {

            case "d":

                startTime = endTime.minus(timeSpan,ChronoUnit.DAYS);

                dateFunction = "DATE";

                break;

            case "m":

                startTime = endTime.minus(timeSpan,ChronoUnit.MONTHS);

                dateFunction = "MONTH";


                break;

            default:

                startTime = endTime.minus(timeSpan,ChronoUnit.HOURS);

                break;



        }

        List<StationVo> list = StationCacheUtil.newInstance().getStationListByCityCode(regionCode);

        Map<String,Object> map = new HashMap<String,Object>();

        map.put("stationList",list);

        map.put("startTime", DateUtils.dateFormat(startTime));

        map.put("endTime",  DateUtils.dateFormat(endTime));

        map.put("dateUnit",dateFunction);

        List<Map<String,Object>> station = stationDetailMapper.getLastTimeSpanResultData(map);

        Map<String,Object> resultMap = new HashMap<String,Object>();

        for(Map stationMap: station) {

            for(String spe:speciesConfig.getSpecies()) {

                Map map1 = null;

                if(resultMap.containsKey(spe)){

                    map1 = (Map)resultMap.get(spe);
                }

                else {

                    map1 = new HashMap();

                }

                map1.put(stationMap.get("timepoint").toString(),stationMap.get(spe));

                resultMap.put(spe,map1);

            }
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> getLast24ResultData(String regionCode) {

        regionCode = RegionUtils.convertRegionCode(regionCode);

        List<StationVo> list = StationCacheUtil.newInstance().getStationListByCityCode(regionCode);

        LocalDateTime endTime = LocalDateTime.now();

        LocalDateTime startTime = endTime.minus(24, ChronoUnit.HOURS);

        Map<String,Object> map = new HashMap<String,Object>();

        map.put("stationList",list);

        map.put("startTime", DateUtils.dateFormat(startTime));

        map.put("endTime",  DateUtils.dateFormat(endTime));

        List<Map<String,Object>> station = stationDetailMapper.getLastTimeSpanResultData(map);

        Map<String,Object> resultMap = new HashMap<String,Object>();

        station.forEach(stationMap -> {

            speciesConfig.getSpecies().forEach(spe -> {

                Map map1 = null;

                if(resultMap.containsKey(spe)){

                    map1 = (Map)resultMap.get(spe);
                }

                else {

                    map1 = new HashMap();
                }

                map1.put(stationMap.get("timepoint").toString(),stationMap.get(spe));

                resultMap.put(spe,map1);


            });

        });

        return resultMap;
    }

    @Override
    public Map<String, Object> getYearResultByVirtualSite(Integer year, String regionCode) {
        return null;
    }

    @Override
    public Map<String, Object> getLast24ResultDataByVirtualSite(String regionCode) {

        return null;
    }
    @Override
    @Cacheable(key="#year+#month+#regionCode",value="stationDetail")
    public Map<String, Object> getAvgMonthResultByRegionCode(Integer year, Integer month, String regionCode) {

        List<Map<String,Object>> resultList = null;

        regionCode = RegionUtils.convertRegionCode(regionCode);

        List<StationVo> list = StationCacheUtil.newInstance().getStationListByCityCode(regionCode);

        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        Map<String,Object> map = new HashMap<String,Object>();

        if(year > 2013) {

            map.put("tableName","PM_25");
        }

        else {

            map.put("tableName","Sum_All_PM25_copy");
        }


        map.put("stationList",list);

        map.put("startTime", DateUtils.dateFormat(startTime));

        map.put("endTime",  DateUtils.dateFormat(endTime));

        if(((List)map.get("stationList")).size() == 0){

            return null;
        }

        resultList =  stationDetailMapper.getAvgMonthResultByStationCode(map);

        switch(regionCode.length()){

            case 2:

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        item.put(key,map1);

                    });

                });


                break;

            case 4:

                //是平均加超标日期
                List<Map<String,Object>> overStandardList = stationDetailMapper.selectOverStandardGroupByDate1(map);

                overStandardList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("days", value);

                        item.put(key, map1);

                    });
                });

                Map<String,Object> overStandardMap = overStandardList.get(0);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        if(overStandardMap.containsKey(key)) {

                            map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                        }

                        item.put(key, map1);

                    });

                });

                break;

            case 6:

                break;

            default:

                break;
        }

        return null;
    }

    @Override
    public Map getAllCurrentPlace(CityParamVo cityParamVo) {

        List<Map<String,Object>> list = stationDetailMapper.getAllCurrentPlace(cityParamVo);

        Map<String, Map<String,Object>> mapper;

        mapper = list.stream().collect(

                Collectors.toMap((map) -> {

                    return CityCacheUtil.newInstance().getRegionCode((String) map.get(cityParamVo.getArea().toLowerCase()));

                }, (map) -> {
                    Map map1 = new HashMap();
                    map.forEach((k, v) -> {
                        if (!k.equals(cityParamVo.getArea().toLowerCase())) {

                            map1.put(k, v);
                        }

                    });

                    return map1;
                }, (existingValue, newValue) -> existingValue)
        );

        return mapper;
    }

    @Override
    public List<Map<String, Object>> getRankMonResultRegionCode(List<String> regionCode, LocalDateTime year, List<String> areas, String tUnit) {
        return null;
    }

    public List<RankAreaData> getRankResultDataByArea(StationDetailCondition stationDetailCondition){

        List<Callable> taskList = new ArrayList();

        List resultList = new ArrayList();

        Map map = DateUtils.initCondition(stationDetailCondition);

        LocalDateTime endTime = (LocalDateTime) map.get("endTime");

        LocalDateTime startTime = (LocalDateTime) map.get("startTime");

        ChronoUnit unit = (ChronoUnit) map.get("chronoUnit");

        LocalDateTime temp = null;

        final StationDetailService stationDetailService = this;

        while(startTime.isBefore(endTime)) {

            temp = endTime.minus(1,unit);

            final LocalDateTime finalTemp = temp;

            final LocalDateTime finalEndTime = endTime;

            MonthTask mon = new MonthTask((SpecialDealer)() -> {

                return  stationDetailService.getRankResultDataByArea(stationDetailCondition, finalTemp, finalEndTime);

            });

            taskList.add(mon);

            endTime = temp;

        }

        List<Future> list = asyncServiceImpl.executeAsyncTask(taskList);

        for(Future<List> fs:list){

            try {

                resultList.addAll(fs.get());

            } catch (InterruptedException e) {

                e.printStackTrace();

            } catch (ExecutionException e) {

                e.printStackTrace();
            }
        }


        return resultList;
    }


    public  List<RankAreaData> getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime) {

        List list = null;

        List areasList = parameter.getAreas();

        String datePattern = "";

        switch (parameter.getTunit()){

            case "m":

                datePattern = "yyyy-MM";

            break;

            case "d":

                datePattern = "yyyy-MM-dd";

            break;

            default:
        }

        StringBuffer redisKey = new StringBuffer(Contants.RANK_KEY + DateTimeFormatter.ofPattern(datePattern).format(startTime));

        if(areasList != null && areasList.size() != 0) {

            redisKey.append("areas-").append(areasList.parallelStream().collect(Collectors.joining(",")));
        }

        parameter.setRedisKey(redisKey.toString());

        if(redisCache.exists(parameter.getRedisKey())){

            list = (List)redisCache.get(parameter.getRedisKey());
        }

        if(list == null) {

            StringBuffer preSql = new StringBuffer("");

            StringBuffer headSql = new StringBuffer("");

            String tailSql = "";

            List<String> species = speciesConfig.getSpecies();

            String result="";
            //随机三位字符
            Random random = new Random();

            for (int j = 0; j < 3; j++) {

                result += (char)(97 + random.nextInt(25));

            }

            for(int i = 0;i < species.size();i++) {

                preSql.append("set @").append(result+i).append(" = 0").append(";");

                headSql.append("select *,@").append(result).append(i).append(":=@").append(result).append(i).append("+1 as ").append("rank"+species.get(i)).append(" from ( ");

                tailSql =" ) q" + i + " order by val_" + species.get(i) + " asc " + tailSql;
            }

            list = stationDetailMapper.getRankResultDataByArea(preSql.toString(),headSql.toString(), parameter, startTime, endTime, tailSql);

            if(list!= null && list.size() != 0) {

                redisCache.set(parameter.getRedisKey(), list);

            }

        }
        return list;
    }

    @Override
    @Cacheable(value = "stationDetail",key = "#year.toString()+'-'+#month.toString()+(#date!=null?'-'+#date.toString():'')+#regionCodes.toString()")
    public List  getRankResultDataByRes(Integer year, Integer month, Integer date, Set<String> regionCodes) {

        Map<String,Object> map = new HashMap<String,Object>();

        LocalDateTime startTime = null;

        LocalDateTime endTime = null;

        String tunit = "";

        if(date != null) {

            startTime = LocalDateTime.of(year, month, date, 0, 0);

            endTime = startTime.plus(1, ChronoUnit.DAYS);

            tunit="d";
        }
        else {

            startTime = LocalDateTime.of(year,month,1,0,0);

            endTime = startTime.plus(1,ChronoUnit.MONTHS);

            tunit="m";
        }

        String tableName="";

        if(endTime.getYear() < 2013) {

            tableName = "Sum_All_PM25_copy";
        }
        else {

            tableName = "PM_25";
        }


        List<Map<String,Object>> resultList = new ArrayList();

        for (String regionCode: regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            Map map1 = this.getRankResultDataByRe(tableName, tunit, startTime, endTime, regionCode);

            resultList.add(map1);

        }

        List<String> list = speciesConfig.getSpecies();

        int length = list.size();

        while(length-- > 0) {

            sort(resultList, list.get(length));
        }

        System.out.println(resultList);

        return resultList;
    }

    public List getRankResultDataByRe(Integer year, Integer month, Integer date, String regionCode){

        return null;
    }

    public  Map<String,Object> getRankResultDataByRe(String tableName,String tunit,LocalDateTime startTime,LocalDateTime endTime,String regionCode) {

        List<String> stationList = StationCacheUtil.newInstance().getStationListByCityCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        if(stationList == null) {

            return null;
        }
        else {

            Map<String,Object> map = stationDetailMapper.getAvgResultByRe(tableName,tunit,startTime,endTime,stationList);

            return map;
        }
    }


    private void sort(List<Map<String,Object>> list,String species) {

        list = list.parallelStream().sorted((map1, map2) -> {

            Object obj = map1.get(species);

            if (obj instanceof Double) {

                if ((((Double) map1.get(species)) - ((Double) map2.get(species))) > 0)

                    return -1;

                else if ((((Double) map1.get(species)) - ((Double) map2.get(species))) == 0)

                    return 0;

                else

                    return 1;

            } else if (obj instanceof BigDecimal) {

                if ((((BigDecimal) map1.get(species)).compareTo((BigDecimal) map2.get(species))) > 0)

                    return -1;

                else if ((((BigDecimal) map1.get(species)).compareTo((BigDecimal) map2.get(species))) == 0)

                    return 0;
                else
                    return 1;


            } else {

                return 0;
            }


        }).collect(Collectors.toList());

        int count = list.size();

        for(Map<String,Object> map:list) {

            map.put("rank" + species,count--);
        }

    }

}
