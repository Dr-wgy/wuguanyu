package com.makenv.service.impl;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.RedisCache;
import com.makenv.cache.StationCacheUtil;
import com.makenv.condition.StationDetailCondition;
import com.makenv.config.FigConfig;
import com.makenv.config.SpeciesConfig;
import com.makenv.config.SysConfig;
import com.makenv.constant.Constants;
import com.makenv.dao.StationDetailMultipleDao;
import com.makenv.domain.City;
import com.makenv.domain.StationDetail;
import com.makenv.enums.UnitLengthEnum;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.service.AsyncService;
import com.makenv.service.StationDetailService;
import com.makenv.task.MonthTask;
import com.makenv.task.SpecialDealer;
import com.makenv.util.DateUtils;
import com.makenv.util.RegionUtils;
import com.makenv.vo.*;
import org.hibernate.type.StringClobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/8/9.
 */
@Service
public class StationDetailServiceImpl implements StationDetailService {

    private final static Logger logger = LoggerFactory.getLogger(StationDetailServiceImpl.class);

    @Autowired
    private StationDetailMultipleDao stationDetailMultipleDao;

    @Autowired
    private StationDetailMapper stationDetailMapper;

    @Resource
    private RedisCache redisCache;

    @Resource(name="redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private FigConfig figConfig;


    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private SpeciesConfig speciesConfig;

    @Autowired
    private AsyncService asyncService;

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

    @Cacheable(key = "#year+#regionCode",value = "stationDetail",condition = "#year<T(java.time.LocalDateTime).now().getYear()")
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

        List<String> stationCodes = null;

        List repeatCodes = null;

        if(regionCode.length()==4 || regionCode.length()==2) {

            stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else if(regionCode.length()==6) {


            stationCodes = StationCacheUtil.newInstance().getStationListByAdCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }


        LocalDateTime pm_25_jjj_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_jjj_startTime.getYear() <= year) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();
        }


        if(stationCodes != null && stationCodes.size()!= 0 && repeatCodes!= null && repeatCodes.size()!= 0 ) {


            List copyStaionCodes =  new ArrayList<>();

            copyStaionCodes.addAll(stationCodes);

            stationCodes.removeAll(repeatCodes);

            repeatCodes.retainAll(copyStaionCodes);

        }

        map.put("stationList",stationCodes);

        map.put("repeatCodes",repeatCodes);

        if(((List)map.get("stationList")).size() == 0){

            return null;
        }

        //省级平均
        switch (regionCode.length()) {

            case 2:

                resultList =  stationDetailMapper.selectAvgYearResultByProvince(map);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        item.put(key,map1);

                    });

                });

                break;

/*            case 4:


                break;

            case 6:

                resultList =  stationDetailMapper.selectAvgYearResultByCity(map);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        item.put(key,map1);

                    });

                });

                break;*/


            default:

                resultList =  stationDetailMapper.selectAvgYearResultByCity(map);

                //是平均加超标日期
                List<Map<String,Object>> overStandardList = stationDetailMapper.selectOverStandardGroupByDate(map);

                overStandardList.forEach(item -> {

                    if(item != null) {

                        item.forEach((key, value) -> {

                            Map map1 = new HashMap<String, Object>();

                            map1.put("days", value);

                            item.put(key, map1);

                        });
                    }

                });

                Map<String,Object> overStandardMap = overStandardList.get(0);

                resultList.forEach(item -> {

                    if(item != null) {

                        item.forEach((key, value) -> {

                            Map map1 = new HashMap<String, Object>();

                            map1.put("val", value);

                            if(overStandardMap.containsKey(key)) {

                                map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                            }

                            item.put(key, map1);

                        });
                    }
                });


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
    public Map<String, Object> getLast24ResultData(String area, String areaId) {

        List<StationVo> list = null;

        if("city".equals(area)){

            areaId = RegionUtils.convertRegionCode(areaId);

            list = StationCacheUtil.newInstance().getStationListByCityCode(areaId);

        }
        else if("station".equals(area)){

            list = new ArrayList<StationVo>();

            StationVo station = new StationVo();

            station.setStationId(areaId);

            list.add(station);

        }


        LocalDateTime endTime = LocalDateTime.now();

        LocalDateTime startTime = endTime.minus(24, ChronoUnit.HOURS);

        Map<String,Object> map = new HashMap<String,Object>();

        map.put("stationList",list);

        map.put("startTime", DateUtils.dateFormat(startTime));

        map.put("endTime",  DateUtils.dateFormat(endTime));

        List<Map<String,Object>> JING_JIN_JI = stationDetailMultipleDao.getLastTimeSpanResultDataByJING_JIN_JI(map);

        List<Map<String,Object>> station = JING_JIN_JI;

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
    @Cacheable(key="#year+#month+#regionCode",value="stationDetail")
    public Map<String, Object> getAvgMonthResultByRegionCode(Integer year, Integer month, String regionCode) {

        Map<String,Object> resultMap = null;

        regionCode = RegionUtils.convertRegionCode(regionCode);

        List<StationVo> list = StationCacheUtil.newInstance().getStationListByCityCode(regionCode);

        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        Map<String,Object> map = new HashMap<String,Object>();

        if(year > 2013) {

            map.put("tableName","PM_25");
        }

        else {

            map.put("tableName", "Sum_All_PM25_copy");
        }


        map.put("stationList",list);

        map.put("startTime", DateUtils.dateFormat(startTime));

        map.put("endTime",  DateUtils.dateFormat(endTime));

        if(((List)map.get("stationList")).size() == 0){

            return null;
        }

        switch(regionCode.length()){

            case 2:

                resultMap =  stationDetailMapper.getAvgMonthResultByProvince(map);

                final Map<String, Object> finalResultMap = resultMap;

                resultMap.forEach((key, value) -> {

                    Map map1 = new HashMap<String, Object>();

                    map1.put("val", value);

                    finalResultMap.put(key, map1);

                });


                break;

/*            case 4:

                resultMap =  stationDetailMapper.getAvgMonthResultByCity(map);

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

                final Map<String, Object> finalResultMap1 = resultMap;

                resultMap.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        if(overStandardMap.containsKey(key)) {

                            map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                        }

                        finalResultMap1.put(key, map1);

                    });

                break;

            case 6:

                break;*/

            default:


                resultMap =  stationDetailMapper.getAvgMonthResultByCity(map);

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

                final Map<String, Object> finalResultMap1 = resultMap;

                resultMap.forEach((key, value) -> {

                    Map map1 = new HashMap<String, Object>();

                    map1.put("val", value);

                    if(overStandardMap.containsKey(key)) {

                        map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                    }

                    finalResultMap1.put(key, map1);

                });

                break;
        }

        return null;
    }

    @Override
    public Map getAllCurrentPlace(CityParamVo cityParamVo) {


        if(cityParamVo.getIsTimePointOrTimeInterval()) {

            if(cityParamVo.getDateTime() == null) {

                //cityParamVo.setDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00")));

                cityParamVo.setTableName(sysConfig.getYearTable().get("default"));

            }

            else  {

                String dateTime = cityParamVo.getDateTime();

                LocalDateTime  now = LocalDateTime.parse(cityParamVo.getDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

                dateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

                cityParamVo.setDateTime(dateTime);

                cityParamVo.setTableName(sysConfig.getYearTable().containsKey(now.getYear())?

                        (sysConfig.getYearTable().get(String.valueOf(now.getYear()))):

                        sysConfig.getYearTable().get("default"));

            }


        }

        else {

            String afterTime = "";

            String beforeTime = "";

            LocalDateTime after = LocalDateTime.parse(cityParamVo.getAfterTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

            afterTime = after.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            LocalDateTime before = LocalDateTime.parse(cityParamVo.getBeforeTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

            beforeTime = before.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            cityParamVo.setTableName(sysConfig.getYearTable().containsKey(after.getYear()) ?

                    (sysConfig.getYearTable().get(String.valueOf(after.getYear()))) :

                    sysConfig.getYearTable().get("default"));
        }

        List<Map<String,Object>> JING_JIN_JI = stationDetailMultipleDao.getAllCurrentPlaceByJING_JIN_JI(cityParamVo);

        List<Map<String,Object>> list = JING_JIN_JI;

        Map<String, Object> mapper;


        mapper = list.stream().collect(

                Collectors.toMap((map) -> {

                    String area = (String) map.get(cityParamVo.getArea().toLowerCase());

                    if("area".equals(cityParamVo.getArea().toLowerCase())) {

                        if(area != null && area.endsWith("市"))

                            area = area.substring(0,area.lastIndexOf("市"));

                        if((area = CityCacheUtil.newInstance().getRegionCode(area)) == null) {

                            return area;

                        }
                    }
                    else if("stationcode".equals(cityParamVo.getArea().toLowerCase())) {

                            String stationCode = (String)map.get("area");

                            if(StringUtils.isEmpty(stationCode)) {

                                stationCode = UUID.randomUUID().toString();
                            }

                            return stationCode;

                    }

                    return area;

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

        if(cityParamVo.getDateTime() == null) {

            mapper.put("maxTimePonit",stationDetailMapper.getMaxTimePoint());
        }

        return mapper;
    }

    @Override
    public List<Map<String, Object>> getRankMonResultRegionCode(List<String> regionCode, LocalDateTime year, List<String> areas, String tUnit) {
        return null;
    }

    public Map getRankResultDataByArea(StationDetailCondition stationDetailCondition){

        Map resultMap = new HashMap();

        List<Callable> taskList = new ArrayList();

        List<Map> resultList = new ArrayList();

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

       List<Future> list = asyncService.executeAsyncTask(taskList);

        for(Future<List> fs:list){

            try {

                resultList.addAll(fs.get());

            } catch (InterruptedException e) {

                e.printStackTrace();

            } catch (ExecutionException e) {

                e.printStackTrace();
            }
        }

        if(resultList!=null && resultList.size()!=0) {

            speciesConfig.getSpecies().forEach(spe -> {

                if (!resultMap.containsKey(spe)) {

                    resultMap.put(spe, new HashMap());
                }

                resultList.stream().forEach(map1 -> {

                    Map speMap = (Map) resultMap.get(spe);


                    Object cityCtyCode = map1.get("area");

                    if (!speMap.containsKey(cityCtyCode)) {

                        speMap.put(cityCtyCode, new ArrayList<>());
                    }

                    Map valueMap = new HashMap();

                    valueMap.put("rank", map1.get("rank" + spe));

                    valueMap.put("Time", map1.get("timePoint"));

                    valueMap.put("value", map1.get("val_" + spe));

                    ((List) speMap.get(cityCtyCode)).add(valueMap);

                });
            });
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getAvgDateResultByRegionCode(Integer year, Integer month, Integer date, String regionCode) {
        return null;
    }


    @Override
    @Cacheable(key = "#startDateTime.toString()+#endDateTime.toString()+#area+#areaId+#unit", condition = "!#unit.equals('h')",value = "stationDetail")
    public Map<String, Object> getAvgResultByAreaOrStation(LocalDateTime startDateTime, LocalDateTime endDateTime, String area, String areaId, String unit) {

        Map<String,Object> map = new HashMap();

        List<String> stationIds = null;

        if("station".equalsIgnoreCase(area)) {

            stationIds = new ArrayList<>();

            stationIds.add(areaId);

        }
        else {


            areaId = RegionUtils.convertRegionCode(areaId);

            if(areaId.length()==4) {

                stationIds = StationCacheUtil.newInstance().getStationIdList(areaId);

            }

            else if(areaId.length()== 6) {

                stationIds = StationCacheUtil.newInstance().getStationListByAdCode(areaId).stream().map(StationVo::getStationId).collect(Collectors.toList());

            }

            else {

                throw new RuntimeException("parameter city is not corrent");

            }

        }

        if(startDateTime.isAfter(endDateTime)) {

            throw new RuntimeException("startDate is after endDate, please check your startDate and endDate");
        }

        List<Map> list = new ArrayList();

        Integer startYear = startDateTime.getYear();

        Integer endYear = endDateTime.getYear();

        if(sysConfig.getYearTable().containsKey(endYear.toString()) && sysConfig.getYearTable().containsKey(startYear.toString())) {

            String tableName = sysConfig.getYearTable().get(endYear.toString());

            List<Map<String,Object>> list1 = stationDetailMapper.getAvgResultByAreaOrStation(startDateTime,endDateTime,area,stationIds,unit,tableName);

            list.addAll(list1);

        }else if(sysConfig.getYearTable().containsKey(startYear.toString()) && !sysConfig.getYearTable().containsKey(endYear.toString())) {

            String tableName = sysConfig.getYearTable().get(startYear.toString());

            String tableName1 = sysConfig.getYearTable().get("default");

            List<Map<String,Object>> list1 = stationDetailMapper.getAvgResultByAreaOrStation(startDateTime, endDateTime, area, stationIds, unit, tableName);

            List<Map<String,Object>> list2 = stationDetailMapper.getAvgResultByAreaOrStation(startDateTime,endDateTime,area,stationIds,unit,tableName1);

            list.addAll(list1);

            list.addAll(list2);
        }
        else {

            String tableName1 = sysConfig.getYearTable().get("default");

            List<Map<String,Object>> list1 = stationDetailMapper.getAvgResultByAreaOrStation(startDateTime, endDateTime, area, stationIds, unit, tableName1);

            list.addAll(list1);

        }

        list.stream().forEach(stationDetailMap -> {


            final Map<String, Object> stationDetailMap1 = stationDetailMap;

            speciesConfig.getSpecies().forEach(spe -> {

                Map map1 = null;

                if (map.containsKey(spe)) {

                    map1 = (Map) map.get(spe);

                } else {

                    map1 = new HashMap();
                }


                if (stationDetailMap1.get("timePoint") != null) {

                    map1.put(stationDetailMap1.get("timePoint").toString(), stationDetailMap1.get(spe));

                    map.put(spe, map1);

                }

            });

        });

        return map;
    }

    @Override
    public List getQualityData(Integer year, Integer month, String city, String timeSpan, String timeUnit) {

        String cityCode = RegionUtils.convertRegionCode(city);

        if(cityCode ==null || StringUtils.isEmpty(cityCode)) {

            throw new RuntimeException("cityCode is null && '', it not be allowed" );
        }

        List<String> stationCodes = null;

        if(cityCode.length()==4) {

            stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(cityCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else if(cityCode.length()==6) {

            stationCodes = StationCacheUtil.newInstance().getStationListByAdCode(cityCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else {

            throw new RuntimeException("cityCode is not effective" );

        }

        if(stationCodes == null || stationCodes.size()==0) {

            return null;
        }



        List list = new ArrayList();

        //时间间隔
        Integer timeSpan1 = Integer.parseInt(timeSpan);

        ChronoUnit unit = ChronoUnit.HOURS;

        String datePattern = "yyyy-MM-dd_HH";

        switch(timeUnit) {

            case "m":

                unit = ChronoUnit.MONTHS;

                datePattern = "yyyy-MM";

                break;

            case "d":

                unit = ChronoUnit.DAYS;

                datePattern = "yyyy-MM-dd";

                break;

            default:

                break;
        }


        LocalDateTime endTime = LocalDateTime.of(year, month, 1, 0, 0);

        LocalDateTime startTime = endTime.minus(timeSpan1, unit);

        while(startTime.isBefore(endTime)) {

            String redisKey = Constants.QUALITY_KRY + DateUtils.dateFormat(endTime,datePattern)+cityCode;

            if(redisCache.exists(redisKey)) {

                Object obj = redisCache.get(redisKey);

                list.add(obj);
            }

            else {

                Map<String,Object> map = stationDetailMapper.getQualityData(endTime,endTime.plus(1,unit),stationCodes,timeUnit);

                list.add(map);

                redisCache.set(redisKey, map);

            }

            endTime = endTime.minus(1,unit);


        }

        Collections.reverse(list);

        return list;
    }

    @Override
    public Map getHevData(Integer year, Integer month, String city, String timeSpan, String timeUnit) {


        String cityCode = RegionUtils.convertRegionCode(city);

        if(cityCode ==null || StringUtils.isEmpty(cityCode)) {

            throw new RuntimeException("cityCode is null && '', it not be allowed" );
        }

        List<String> stationCodes = null;

        if(cityCode.length()==4) {

            stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(cityCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else if(cityCode.length()==6) {

            stationCodes = StationCacheUtil.newInstance().getStationListByAdCode(cityCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else {

            throw new RuntimeException("cityCode is not effective" );

        }

        if(stationCodes == null || stationCodes.size()==0) {

            return null;
        }

        List<String>  repeatCodes = null;

        List<Map> list = new ArrayList();

        //时间间隔
        Integer timeSpan1 = Integer.parseInt(timeSpan);

        ChronoUnit unit = ChronoUnit.HOURS;

        String datePattern = "yyyy-MM-dd_HH";

        switch(timeUnit) {

            case "m":

                unit = ChronoUnit.MONTHS;

                datePattern = "yyyy-MM";

                break;

            case "d":

                unit = ChronoUnit.DAYS;

                datePattern = "yyyy-MM-dd";

                break;

            default:

                break;
        }


        LocalDateTime endTime = LocalDateTime.of(year, month, 1, 0, 0);

        LocalDateTime startTime = endTime.minus(timeSpan1, unit);

        LocalDateTime pm_25_jjj_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_jjj_startTime.isBefore(endTime)) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();
        }


        if(stationCodes != null && stationCodes.size()!= 0 && repeatCodes!= null && repeatCodes.size()!= 0 ) {


            List copyStaionCodes =  new ArrayList<>();

            copyStaionCodes.addAll(stationCodes);

            stationCodes.removeAll(repeatCodes);

            repeatCodes.retainAll(copyStaionCodes);

        }


        while(startTime.isBefore(endTime)) {

            String redisKey = Constants.HEV_KEY + DateUtils.dateFormat(endTime,datePattern)+cityCode;

            if(redisCache.exists(redisKey)) {

                Map obj = (Map)redisCache.get(redisKey);

                list.add(obj);


            }

            else {

                String tailSql = "having avg(AQI)<=200";

                Map<String,Object> hevDataClean = stationDetailMapper.getHevData(endTime,endTime.plus(1,unit),repeatCodes,stationCodes,timeUnit,tailSql);

                Map<String,Object> hevDataTotal = stationDetailMapper.getHevData(endTime,endTime.plus(1,unit),repeatCodes,stationCodes,timeUnit,"");

                Map map = new HashMap();

                map.put("clean",hevDataClean);

                map.put("total",hevDataTotal);

                list.add(map);

                redisCache.set(redisKey, map);

            }

            endTime = endTime.minus(1,unit);

        }


        if(list != null && list.size() != 0) {

            Map resultMap = new HashMap();

            speciesConfig.getSpecies().forEach(spe -> {

                Map map = null;

                if (resultMap.containsKey(spe)) {

                    map = (Map) resultMap.get(spe);

                } else {

                    map = new LinkedHashMap();

                    resultMap.put(spe, map);
                }

                Collections.reverse(list);

                for (Map item : list) {

                    if (!map.containsKey("clean") || !map.containsKey("cleandays")) {

                        map.put("clean", new ArrayList());

                        map.put("cleandays", new ArrayList());


                    }

                    if (!map.containsKey("tot") || !map.containsKey("totdays")) {

                        map.put("tot", new ArrayList());

                        map.put("totdays", new ArrayList());

                    }

                    Map cleanMap = (Map) item.get("clean");

                    ((List) map.get("clean")).add(cleanMap.get(spe));

                    ((List) map.get("cleandays")).add(cleanMap.get("ct"));

                    Map totalMap = (Map) item.get("total");

                    ((List) map.get("tot")).add(totalMap.get(spe));

                    ((List) map.get("totdays")).add(totalMap.get("ct"));
                }
            });

            return resultMap;
        }
        return null;

    }

    @Override
    public Map<String, Object> getAllMonResult(String city) {

        Map resultMap = new HashMap();

        List repeatCodes = null;

        String regionCode = RegionUtils.convertRegionCode(city);


        List<String> stationCodes = null;

        if(regionCode.length()==4 || regionCode.length()==2) {

            stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }
        else if(regionCode.length()==6) {


            stationCodes = StationCacheUtil.newInstance().getStationListByAdCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime endTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);

        LocalDateTime startTime = LocalDateTime.of(sysConfig.getStartYear(), 2, 1, 0, 0);

        LocalDateTime pm_25_JJJ_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_JJJ_startTime.isBefore(endTime)) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();
        }

        if(stationCodes != null && stationCodes.size()!= 0 && repeatCodes!= null && repeatCodes.size()!= 0 ) {

            List copyStaionCodes =  new ArrayList<>();

            copyStaionCodes.addAll(stationCodes);

            stationCodes.removeAll(repeatCodes);

            repeatCodes.retainAll(copyStaionCodes);

        }

        List<Map<String,Object>>avgList = new ArrayList<>();

        List<Map<String,Object>> maxMinList = new ArrayList<>();

        while(startTime.isBefore(endTime)){

            String redisAvgKey = Constants.AVG_MON_KEY+DateUtils.dateFormat(endTime,"yyyy-MM")+"cityCode:"+regionCode;

            String redisMaxMinKey = Constants.MIN_MAX_KEY+DateUtils.dateFormat(endTime,"yyyy-MM")+"cityCode:"+regionCode;

            Map map = new HashMap();

            String year = String.valueOf(endTime.getYear());

            String tableName = sysConfig.getYearTable().containsKey(year)?sysConfig.getYearTable().get(year):sysConfig.getYearTable().get("default");

            map.put("tableName",tableName);

            map.put("stationList",stationCodes);

            map.put("startTime", DateUtils.dateFormat(endTime));

            map.put("endTime", DateUtils.dateFormat(endTime.plus(1,ChronoUnit.MONTHS)));

            map.put("repeatCodes",repeatCodes);

            if(redisCache.exists(redisAvgKey)) {

                avgList.add((Map)redisCache.get(redisAvgKey));

            }

            else {

                Map avgMonMap = stationDetailMapper.getAvgMonthResultByCity(map);

                avgList.add(avgMonMap);

                if(!(endTime.getMonthValue() == now.getMonthValue() && endTime.getYear() == now.getYear())) {

                    redisCache.set(redisAvgKey,avgMonMap);

                }
            }

            if(redisCache.exists(redisMaxMinKey)) {

                maxMinList.add((Map) redisCache.get(redisMaxMinKey));

            }
            else {

                Map minMaxMap = stationDetailMapper.getMaxMinResultByCity(map);

                if(minMaxMap == null||(minMaxMap.get("MAX") == null && minMaxMap.get("MIN") == null)) {

                    minMaxMap = null;
                }

                maxMinList.add(minMaxMap);

                redisCache.set(redisMaxMinKey, minMaxMap);


            }

            endTime = endTime.minus(1,ChronoUnit.MONTHS);

        }

        avgList.stream().forEach(avgMap ->{

            if(avgMap!=null) {

                Timestamp avgStamp = (Timestamp) avgMap.get("TimePoint");

                LocalDateTime avgLocalDateTime = DateUtils.convertTimeStampToLocalDateTime(avgStamp);

                Integer avgYear = avgLocalDateTime.getYear();

                if (!resultMap.containsKey(avgYear)) {

                    resultMap.put(avgYear, new HashMap());
                }

                Map map1 = (Map) resultMap.get(avgYear);

                String monDatePattern = DateUtils.dateFormat(avgLocalDateTime, "yyyy-MM");

                if (!map1.containsKey(monDatePattern)) {

                    map1.put(monDatePattern, new HashMap());
                }

                Map innerMap = (Map)map1.get(monDatePattern);

                speciesConfig.getSpecies().forEach(spe -> {

                    innerMap.put(spe, avgMap.get(spe));

                });


            }
        });

        maxMinList.stream().forEach(min_max_Map -> {

            if (min_max_Map != null) {

                Timestamp min_MaxStamp = (Timestamp) min_max_Map.get("dt");

                LocalDateTime min_MaxLocalDateTime = DateUtils.convertTimeStampToLocalDateTime(min_MaxStamp);

                Integer min_maxYear = min_MaxLocalDateTime.getYear();

                if (!resultMap.containsKey(min_maxYear)) {

                    resultMap.put(min_maxYear, new HashMap());

                }

                Map map2 = (Map) resultMap.get(min_maxYear);

                String monDatePattern1 = DateUtils.dateFormat(min_MaxLocalDateTime, "yyyy-MM");

                if (!map2.containsKey(monDatePattern1)) {

                    map2.put(monDatePattern1, new HashMap());
                }

                Map innerMap = (Map) map2.get(monDatePattern1);

                innerMap.put("MAX", min_max_Map.get("MAX"));

                innerMap.put("MIN", min_max_Map.get("MIN"));


            }
        });
        return resultMap;
    }

    @Override
    public Map getRankALLResultDataByArea() {

        Map resultMap = new HashMap();

        List list = new ArrayList();

        List<String>  repeatCodes = null;

        LocalDateTime nowTime = LocalDateTime.now();
        //开始时间
        LocalDateTime startTime = LocalDateTime.of(sysConfig.getStartYear(), 2, 1, 0, 0);

        LocalDateTime endTime = LocalDateTime.of(nowTime.getYear(), nowTime.getMonthValue(), 1, 0, 0);

        List<String> species = speciesConfig.getSpecies();

        LocalDateTime pm_25_JJJ_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_JJJ_startTime.isBefore(endTime)) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();
        }

        List<String> areas = CityCacheUtil.newInstance().getKeyCities().stream().map(CityVo::getRegionName).collect(Collectors.toList());

        while(startTime.isBefore(endTime)) {

            StringBuffer redisKey = new StringBuffer(Constants.RANK_ALL_KEY + DateTimeFormatter.ofPattern("yyyy-MM").format(endTime));

            StringBuffer preSql = new StringBuffer("");

            StringBuffer headSql = new StringBuffer("");

            String tailSql = "";

            if(redisCache.exists(redisKey.toString())) {

                List list1 = (List)redisCache.get(redisKey.toString());

                list.addAll(list1);
            }

            else {

                Map parameterMap = new HashMap<>();

                if(sysConfig.getYearTable().containsKey(endTime.getYear())) {

                    parameterMap.put("tableName", sysConfig.getYearTable().get(endTime.getYear()));

                }
                else {

                    parameterMap.put("tableName",sysConfig.getYearTable().get("default"));
                }

                parameterMap.put("startTime",endTime);

                parameterMap.put("endTime", endTime.plus(1, ChronoUnit.MONTHS));

                parameterMap.put("areas",areas);

                parameterMap.put("repeatCodes",repeatCodes);

                String result = "";

                Random random = new Random();

                for (int j = 0; j < 3; j++) {

                    result += (char)(97 + random.nextInt(25));

                }

                for(int i = 0;i < species.size();i++) {

                    preSql.append("set @").append(result + i).append(" = 0").append(";");

                    headSql.append("select *,@").append(result).append(i).append(":=@").append(result).append(i).append("+1 as ").append("rank"+species.get(i)).append(" from ( ");

                    tailSql =" ) q" + i + " order by val_" + species.get(i) + " asc " + tailSql;
                }

                List<Map> stationDetailList = stationDetailMapper.getRankALLResultDataByArea(preSql.toString(), headSql.toString(),parameterMap,tailSql);

                if(stationDetailList!= null && stationDetailList.size() != 0) {

                    for(Map map:stationDetailList) {

                        String area = (String)map.get("area");

                        String cityCode = CityCacheUtil.newInstance().getRegionCode(area);

                        if(!StringUtils.isEmpty(cityCode)) {

                            map.put("area",cityCode);
                        }
                    }

                    list.addAll(stationDetailList);
                }

                if(!(endTime.getYear()==nowTime.getYear()&&endTime.getMonthValue()==nowTime.getMonthValue())) {

                    redisCache.set(redisKey.toString(), stationDetailList);
                }
            }

            endTime = endTime.minus(1,ChronoUnit.MONTHS);
        }

        if(list!=null && list.size()!=0) {

            final List<Map> finalList = list;

            speciesConfig.getSpecies().forEach(spe -> {

                if (!resultMap.containsKey(spe)) {

                    resultMap.put(spe, new HashMap());
                }

                finalList.stream().forEach(map1 -> {

                    Map speMap = (Map) resultMap.get(spe);

                    Object cityCtyCode = map1.get("area");

                    if (!speMap.containsKey(cityCtyCode)) {

                        speMap.put(cityCtyCode, new ArrayList<>());
                    }

                    Map valueMap = new HashMap();

                    valueMap.put("rank", map1.get("rank" + spe));

                    valueMap.put("Time", map1.get("timePoint"));

                    valueMap.put("value", map1.get("val_" + spe));

                    ((List) speMap.get(cityCtyCode)).add(valueMap);

                });
            });
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getALLDate(String city, Integer year, Integer month) {

        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);

        String dataPattern = DateUtils.dateFormat(startTime, "yyyy-MM");

        String regionCode = RegionUtils.convertRegionCode(city);


        String redisKey = Constants.AVG_DATE_KEY+dataPattern+"city:"+regionCode;

        if(redisCache.exists(redisKey)) {

            return (Map)redisCache.get(redisKey);
        }

        Map resultMap = new HashMap();

        List<String> stationCodes = null;

        String area = null;

        if(regionCode.length()==4 || regionCode.length()==2) {

            stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

            area = CityCacheUtil.newInstance().getCityByRegionCode(regionCode).getRegionName();
        }
        else if(regionCode.length()==6) {

            stationCodes = StationCacheUtil.newInstance().getStationListByAdCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

            if(CityCacheUtil.newInstance().getCityByRegionCode(regionCode.substring(0, 4))== null) {

                if(CityCacheUtil.newInstance().getCityByRegionCode(regionCode.substring(0,2))!= null) {

                    area = CityCacheUtil.newInstance().getCityByRegionCode(regionCode.substring(0, 2)).getRegionName();

                }
            }
            else {

                area = CityCacheUtil.newInstance().getCityByRegionCode(regionCode.substring(0, 4)).getRegionName();

            }

        }

        String tableName = "";

        if(sysConfig.getYearTable().containsKey(String.valueOf(startTime.getYear()))) {

            tableName = sysConfig.getYearTable().get(String.valueOf(startTime.getYear()));

        }
        else {

            tableName = sysConfig.getYearTable().get(String.valueOf(startTime.getYear()));

        }

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        List repeatCodes = null;

        LocalDateTime pm_25_JJJ_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_JJJ_startTime.isBefore(startTime)) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();
        }

        if(stationCodes != null && stationCodes.size()!= 0 && repeatCodes!= null && repeatCodes.size()!= 0 ) {

            List copyStaionCodes =  new ArrayList<>();

            copyStaionCodes.addAll(stationCodes);

            stationCodes.removeAll(repeatCodes);

            repeatCodes.retainAll(copyStaionCodes);

        }

        List<Map> avgList =  stationDetailMapper.getAvgDateByArea(tableName,area,startTime, endTime,repeatCodes);

        List<Map> min_maxList =  stationDetailMapper.getMax_minDataByArea(tableName, area, startTime, endTime, repeatCodes);

        avgList.forEach(avgMap->{

            if(avgMap!=null) {

                Timestamp avgStamp = (Timestamp) avgMap.get("TimePoint");

                LocalDateTime avgLocalDateTime = DateUtils.convertTimeStampToLocalDateTime(avgStamp);

                String monDatePattern = DateUtils.dateFormat(avgLocalDateTime, "yyyy-MM-dd");

                if (!resultMap.containsKey(monDatePattern)) {

                    resultMap.put(monDatePattern, new HashMap());
                }

                Map innerMap = (Map) resultMap.get(monDatePattern);

                speciesConfig.getSpecies().forEach(spe -> {

                    innerMap.put(spe, avgMap.get(spe));

                });
            }
        });

        min_maxList.forEach(min_maxMap -> {

            if (min_maxMap != null) {

                Timestamp min_MaxStamp = (Timestamp) min_maxMap.get("dt");

                LocalDateTime min_MaxLocalDateTime = DateUtils.convertTimeStampToLocalDateTime(min_MaxStamp);

                String monDatePattern1 = DateUtils.dateFormat(min_MaxLocalDateTime, "yyyy-MM-dd");

                if (!resultMap.containsKey(monDatePattern1)) {

                    resultMap.put(monDatePattern1, new HashMap());
                }

                Map innerMap = (Map) resultMap.get(monDatePattern1);

                innerMap.put("MAX", min_maxMap.get("MAX"));

                innerMap.put("MIN", min_maxMap.get("MIN"));
            }

        });

        redisCache.set(redisKey, resultMap);

        return resultMap;
    }

    public  List getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime) {

        List<Map> list = null;

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

        StringBuffer redisKey = new StringBuffer(Constants.RANK_KEY + DateTimeFormatter.ofPattern(datePattern).format(startTime));

        if(areasList != null && areasList.size() != 0) {

            redisKey.append("areas-").append(areasList.parallelStream().collect(Collectors.joining(",")));
        }

        if(redisCache.exists(redisKey.toString())){

            list = (List)redisCache.get(redisKey.toString());

            return list;
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

                preSql.append("set @").append(result + i).append(" = 0").append(";");

                headSql.append("select *,@").append(result).append(i).append(":=@").append(result).append(i).append("+1 as ").append("rank"+species.get(i)).append(" from ( ");

                tailSql =" ) q" + i + " order by val_" + species.get(i) + " asc " + tailSql;
            }

            list = stationDetailMapper.getRankResultDataByArea(preSql.toString(),headSql.toString(), parameter, startTime, endTime, tailSql);

            if(list!= null && list.size() != 0) {

                for(Map map:list) {

                    String area = (String)map.get("area");

                    String cityCode = CityCacheUtil.newInstance().getRegionCode(area);

                    if(!StringUtils.isEmpty(cityCode)) {

                        map.put("area",cityCode);
                    }
                }
            }

            redisCache.set(redisKey.toString(), list);
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

            endTime = startTime.plus(1, ChronoUnit.MONTHS);

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
