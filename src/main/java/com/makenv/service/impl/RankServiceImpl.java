package com.makenv.service.impl;

import com.makenv.cache.ActualTimeCache;
import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.RedisCache;
import com.makenv.cache.StationCacheUtil;
import com.makenv.condition.StationDetailCondition;
import com.makenv.config.SpeciesConfig;
import com.makenv.config.SysConfig;
import com.makenv.constant.Constants;
import com.makenv.domain.GroupRegion;
import com.makenv.mapper.StationDetailCopyMapper;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.service.*;
import com.makenv.task.MonthTask;
import com.makenv.task.SpecialDealer;
import com.makenv.util.DateUtils;
import com.makenv.util.Md5Util;
import com.makenv.util.RegionUtils;
import com.makenv.util.aqi.AqiUtil;
import com.makenv.util.aqi.model.Aqi;
import com.makenv.vo.StationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/11/16.
 */
@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private StationDetailMapper stationDetailMapper;

    @Autowired
    private StationDetailCopyMapper stationDetailCopyMapper;

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private SpeciesConfig speciesConfig;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private ActualTimeCache actualTimeCache;


    @Autowired
    private GroupRegionService groupRegionService;

    @Override
    @Cacheable(key = "#year+#month+#date+#regionCode.toString()",condition = "T(java.time.LocalDate).now().isAfter(T(java.time.LocalDate).of(#year,#month,#date))",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Set<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month, date, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.DAYS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime, endTime,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime),regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#month+#regionCode.toString()",condition = "T(java.time.LocalDate).now().getYear() > #year || T(java.time.LocalDate).now().getMonthValue() > #month",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, Set<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month,1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime,endTime,DateTimeFormatter.ofPattern("yyyy-MM").format(startTime),regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#month+#date+#hour+#regionCode.toString()",condition = "T(java.time.LocalDateTime).now().getYear() > #year || T(java.time.LocalDateTime).now().getMonthValue() > #month || " +
            "T(java.time.LocalDateTime).now().getDayOfMonth() > #date || T(java.time.LocalDateTime).now().getHour() > #hour",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Integer hour, Set<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month,date, hour, 0);

        LocalDateTime endTime = startTime;

        List list = commonRank(startTime, endTime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH").format(startTime),regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#regionCode.toString()",condition = "T(java.time.LocalDate).now().getYear() > #year",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Set<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, 1, 1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.YEARS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime, endTime,DateTimeFormatter.ofPattern("yyyy").format(startTime),regionCode);

        return list;
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

        final RankService rankService = this;

        while(startTime.isBefore(endTime)) {

            temp = endTime.minus(1,unit);

            final LocalDateTime finalTemp = temp;

            final LocalDateTime finalEndTime = endTime;

            MonthTask mon = new MonthTask((SpecialDealer)() -> {

                return  rankService.getRankResultDataByArea(stationDetailCondition, finalTemp, finalEndTime);

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
    public List<Map<String,Object>> getRankResultDataByRegionCodes(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCodes) {

    /*    LocalDateTime nowTime = LocalDateTime.now();*/

        if( null == regionCodes || regionCodes.size() == 0) {

            return null;
        }

        List resultList = new ArrayList();

        long day = ChronoUnit.DAYS.between(startTime, endTime);

        List<Map<String,Object>> resultList1 = new ArrayList<Map<String,Object>>();

        String startTimeStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime);

        String endTimeStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(endTime);

        String timePoint = String.join("-", startTimeStr, endTimeStr);

        for(int currDay = 0; currDay <= day; currDay++ ) {

            LocalDateTime startTime1 = startTime.plus(currDay, ChronoUnit.DAYS);

            Set<String> regionCodes1 = new HashSet<>(regionCodes);

            Set set = new HashSet();

            regionCodes1.parallelStream().forEach(regionCode -> {

                String key = String.join(":", Constants.AVG_DATE_KEY_EACH_REGION, DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1), regionCode);

                    Map map = (Map) redisCache.get(key);

                    if(map != null) {

                        resultList1.add(map);

                        set.add(regionCode);
                    }
            });

            //取出已经有的缓存
            regionCodes1.removeAll(set);

            Map map = regionCodes1.stream().collect(Collectors.groupingBy(str -> str.length()));

            List provinceList = null;

            List cityList = null;

            List countyList = null;

            if(map.containsKey(2)) {

                provinceList = (List)map.get(2);
            }

            if(map.containsKey(4)) {

                cityList = (List)map.get(4);
            }

            if(map.containsKey(6)) {

                countyList = (List)map.get(6);
            }

            if(provinceList != null && provinceList.size() != 0) {

                List provinceTemList = new ArrayList<>();

                provinceList.stream().forEach(regionCode->{

                    provinceTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> provinceMap1  = stationDetailCopyMapper.getEveryDayByProvince(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS),provinceTemList);

                if(provinceMap1 != null) {

                    final List finalProvinceList = provinceList;

                    provinceMap1.stream().forEach(Map->{

                        String regionId =  (String)Map.get("regionId");

                        String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                        if(!StringUtils.isEmpty(regionId)) {

                            if(Map != null) {

                                redisCache.set(key,Map);

                            }

                            finalProvinceList.remove(regionId);
                        }

                    });

                    finalProvinceList.stream().forEach(str->{

                        String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),str.toString());

                        redisCache.set(key,new HashMap());

                    });

                    resultList1.addAll(provinceMap1);

                }
            }

            if (cityList != null && cityList.size() != 0) {

                List cityTemList = new ArrayList<>();

                cityList.stream().forEach(regionCode->{

                    cityTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> cityMap1= stationDetailCopyMapper.getEveryDayByCity(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), cityTemList);

                if(cityMap1 != null) {

                    final List finalCityList = cityList;


                    cityMap1.stream().forEach(Map->{

                        String regionId =  (String)Map.get("regionId");

                        String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                        if(!StringUtils.isEmpty(regionId)) {

                            if (Map != null) {

                                redisCache.set(key, Map);

                                finalCityList.remove(regionId);

                            }
                        }
                    });

                    finalCityList.stream().forEach(str->{

                        String cityKey = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),str.toString());

                        redisCache.set(cityKey,new HashMap());

                    });

                    resultList1.addAll(cityMap1);
                }

            }

            if(countyList != null && countyList.size() != 0) {

                List<Map<String,Object>>  countyMap = stationDetailCopyMapper.getEveryDayByCounty(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), countyList);

                if(countyMap != null) {

                    final List finalCountyList = countyList;

                    countyMap.stream().forEach(Map->{

                        String regionId =  (String)Map.get("regionId");

                        String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                        if(!StringUtils.isEmpty(regionId)) {

                            if(Map != null) {

                                redisCache.set(key,Map);

                                finalCountyList.remove(regionId);

                            }
                        }
                    });

                    finalCountyList.stream().forEach(str->{

                        String countyKey = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),str.toString());

                        redisCache.set(countyKey,new HashMap());

                    });


                    resultList1.addAll(countyMap);

                }
            }
        }

        Predicate<Map> predicate = (map)->map != null && !StringUtils.isEmpty(map.get("regionId")) && map.get("regionId") != "null";

        Map regionIdMap = resultList1.stream()
                .filter(predicate)
                .collect(Collectors.groupingBy(map1 -> map1.get("regionId")));

        Iterator<Map.Entry<String,List>> iterator = regionIdMap.entrySet().iterator();

        while(iterator.hasNext()){

            Map avgMap = new HashMap<String,Object>();

            Map.Entry<String,List> entry = iterator.next();

            String regionId = entry.getKey();

            List<Map> regionIdList = entry.getValue();

            int standardDays = 0;

            for(Map regionIdMap1:regionIdList) {

                Map map = new HashMap();

                for(String spe:speciesConfig.getSpeciesNoAQI()) {

                    Object obj = regionIdMap1.get(spe);

                    if(obj == null) {

                        map.put(spe,String.valueOf(0.0));
                    }
                    else {
                        map.put(spe,regionIdMap1.get(spe));
                    }
                }

                Aqi aqi = AqiUtil.getAqi(map);

                if(aqi.getAqi() < 200) {

                    standardDays++;

                }


            }

            avgMap.put("standardDays",standardDays);

            for(String spe:speciesConfig.getSpeciesNoAQI()) {

                Double value ;

                value = regionIdList.stream().collect(Collectors.averagingDouble(
                        temMap ->
                                temMap == null  || temMap.get(spe) == null ?0.0:Double.parseDouble(temMap.get(spe).toString())
                                )
                );

                if(value == null) {

                    value = 0.0;
                }

                BigDecimal bigDecimal = new BigDecimal(value);

                double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                avgMap.put(spe,f1);

            }

            Aqi aqi = AqiUtil.getAqi(avgMap);

            avgMap.put("AQI", aqi.getAqi());

            avgMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

            avgMap.put("primaryPollutant",aqi.getPrimaryPollutant());

            avgMap.put("regionId", regionId);

            avgMap.put("timePoint", timePoint);

            avgMap.put("startTime", startTimeStr);

            avgMap.put("endTime", endTimeStr);

            avgMap.put("timePoint", timePoint);

            String regionName = RegionUtils.getRegionName(regionId);

            String parentName = RegionUtils.getParentName(regionId, regionName);

            avgMap.put("regionName", regionName);

            avgMap.put("parentName", parentName);

            resultList.add(avgMap);

        }

        List<String> speciesList = speciesConfig.getSpecies();

        speciesList.add("standardDays");

        int length = speciesList.size();

        while(length-- > 0) {

            sort(resultList, speciesList.get(length));
        }
        return resultList;
    }

    @Override
    public List getRankResultDataByRegionCodesInMonth(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCodes) {

        LocalDateTime lastDayOfThisMonth = startTime.with(TemporalAdjusters.lastDayOfMonth());//获取初月的最后一天

        lastDayOfThisMonth = LocalDateTime.of(lastDayOfThisMonth.getYear(), lastDayOfThisMonth.getMonthValue(), lastDayOfThisMonth.getDayOfMonth(), 0, 0, 0);

        LocalDateTime firstDayofThisMonth = endTime.with(TemporalAdjusters.firstDayOfMonth());//获取末月的第一天

        firstDayofThisMonth = LocalDateTime.of(firstDayofThisMonth.getYear(),firstDayofThisMonth.getMonthValue(),1,0,0,0);

        List resultList = new ArrayList();

        LocalDateTime intervalStartTime = null;

        LocalDateTime intervalEndTime = null;

        //开始时间和结束时间在同一个月
        if (lastDayOfThisMonth.getYear() == firstDayofThisMonth.getYear() && lastDayOfThisMonth.getMonthValue() == firstDayofThisMonth.getMonthValue()) {

            List rankList = rankEveryMonthEachDay(startTime, endTime, DateTimeFormatter.ofPattern("yyyy-MM").format(startTime), regionCodes);

            resultList.addAll(rankList);

        } else {

            List startMonthList = rankEveryMonthEachDay(startTime, lastDayOfThisMonth, DateTimeFormatter.ofPattern("yyyy-MM").format(startTime), regionCodes);

            List endMonList = rankEveryMonthEachDay(firstDayofThisMonth, endTime, DateTimeFormatter.ofPattern("yyyy-MM").format(firstDayofThisMonth), regionCodes);

            LocalDateTime startMonTime = lastDayOfThisMonth.plus(1, ChronoUnit.DAYS);

            LocalDateTime endMonthTime = firstDayofThisMonth;

            resultList.addAll(startMonthList);

            resultList.addAll(endMonList);

            while (startMonTime.isBefore(endMonthTime)) {

                LocalDateTime temEndTime  = startMonTime.plus(1, ChronoUnit.MONTHS);

                List middelMonList = rankEveryMonthEachDay(startMonTime, temEndTime,DateTimeFormatter.ofPattern("yyyy-MM").format(startMonTime), regionCodes);

                resultList.addAll(middelMonList);

                startMonTime = temEndTime;
            }
        }

      return resultList;

    }

    private List <Map<String,Object>>rankAtDay1(LocalDateTime startTime,LocalDateTime endTime,String timePoint,Set<String> regionCodes) {

        boolean cache = false;

        List regionCodeList = new ArrayList();

        for(String regionCode:regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            List<Map<String,Object>> monthList = new ArrayList();

            LocalDateTime temStartTime = startTime;

            LocalDateTime temEndTime = temStartTime.plus(1,ChronoUnit.MONTHS);

            while(temStartTime.isBefore(temEndTime) || temStartTime.isEqual(temEndTime)) {

                LocalDateTime dayEndTime = startTime.plus(1, ChronoUnit.DAYS);

                String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime),regionCode);

                Map map = null;

                if(cache) {

                   map = (Map)redisCache.get(key);

                }

                if(map == null) {

                    Map regionMap = null;

                    switch (regionCode.length()) {

                        case 2:

                            regionMap = stationDetailCopyMapper.getEveryDayByProvinceOrCity(temStartTime, dayEndTime, regionCode + "%");

                            break;

                        case 4:

                            regionMap = stationDetailCopyMapper.getEveryDayByProvinceOrCity(startTime, dayEndTime, regionCode + "%");


                            break;

                        case 6:

                            regionMap = stationDetailCopyMapper.getEveryDayRegionCode(startTime, dayEndTime, regionCode);

                            break;
                    }

                    redisCache.set(key,regionMap);

                }

                monthList.add(map);

                temStartTime = dayEndTime;

            }

            Map avgMap = new HashMap<String,Object>();

            for(String spe:speciesConfig.getSpeciesNoAQI()) {

                Double value ;

                value = monthList.stream().collect(Collectors.averagingDouble(
                                temMap ->
                                        temMap == null  || temMap.get(spe) == null ?0.0:Double.parseDouble(temMap.get(spe).toString())
                        )
                );

                if(value == null) {

                    value = 0.0;
                }

                BigDecimal bigDecimal = new BigDecimal(value);

                double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                avgMap.put(spe,f1);

            }

            Aqi aqi = AqiUtil.getAqi(avgMap);

            avgMap.put("AQI", aqi.getAqi());

            avgMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

            avgMap.put("primaryPollutant",aqi.getPrimaryPollutant());

            avgMap.put("regionId", regionCode);

            avgMap.put("timePoint", timePoint);

            String regionName = RegionUtils.getRegionName(regionCode);

            String parentName = RegionUtils.getParentName(regionCode,regionName);

            avgMap.put("regionName", regionName);

            avgMap.put("parentName", parentName);

            regionCodeList.add(avgMap);
        }

        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(regionCodeList, speciesList.get(length));
        }

        return regionCodeList;

    }

    private List <Map<String,Object>>rankEveryMonthEachDay(LocalDateTime startTime,LocalDateTime endTime,String timePoint,Set<String> regionCodes) {

        List regionCodeList = new ArrayList();

        List<Map<String,Object>> monthList = new ArrayList();

        long days = ChronoUnit.DAYS.between(startTime, endTime);

        for(int currDay = 0; currDay <= days;currDay++) {

            LocalDateTime startTime1 = startTime.plus(currDay, ChronoUnit.DAYS);

            Set cacheSet = new HashSet();

            Set<String> regionCodes1 = new HashSet<>(regionCodes);

            regionCodes1.parallelStream().forEach(regionCode->{

                String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionCode);
                Map map = (Map)redisCache.get(key);

                if(map != null) {

                    monthList.add(map);

                    cacheSet.add(regionCode);
                }
            });

            //取出已经有的缓存
            regionCodes1.removeAll(cacheSet);

            Map map = regionCodes1.stream().collect(Collectors.groupingBy(str -> str.length()));

            List provinceList = null;

            List cityList = null;

            List countyList = null;

            if (map.containsKey(2)) {

                provinceList = (List)map.get(2);
            }

            if(map.containsKey(4)) {

                cityList = (List)map.get(4);
            }

            if(map.containsKey(6)) {

                countyList = (List)map.get(6);
            }

            if(provinceList != null && provinceList.size() != 0) {

                List provinceTemList = new ArrayList<>();

                provinceList.stream().forEach(regionCode->{

                    provinceTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> provinceMap1  = stationDetailCopyMapper.getEveryDayByProvince(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS),provinceTemList);

                provinceMap1.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });


                monthList.addAll(provinceMap1);
            }

            if (cityList != null && cityList.size() != 0) {

                List cityTemList = new ArrayList<>();

                provinceList.stream().forEach(regionCode->{

                    cityTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> cityMap1= stationDetailCopyMapper.getEveryDayByCity(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), cityTemList);

                cityMap1.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });

                monthList.addAll(cityMap1);
            }

            if(countyList != null && countyList.size() != 0) {

                List<Map<String,Object>>  countyMap = stationDetailCopyMapper.getEveryDayByCounty(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), countyList);

                countyMap.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });

                monthList.addAll(countyMap);
            }

            if(countyList != null && countyList.size() != 0) {

                List<Map<String,Object>>  countyMap = stationDetailCopyMapper.getEveryDayByCounty(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), countyList);

                countyMap.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        if(Map != null) {

                            redisCache.set(key,Map);

                        }
                    }

                });

                monthList.addAll(countyMap);
            }
        }

        Map regionIdMap = monthList.stream().collect(Collectors.groupingBy(map1 -> map1.get("regionId")));

        Iterator<Map.Entry<String,List>> iterator = regionIdMap.entrySet().iterator();

        while(iterator.hasNext()){

            Map avgMap = new HashMap<String,Object>();

            Map.Entry<String,List> entry = iterator.next();

            String regionId = entry.getKey();

            List<Map> regionIdList = entry.getValue();

            for(String spe:speciesConfig.getSpeciesNoAQI()) {

                Double value = 0.0;

                value = regionIdList.stream().collect(Collectors.averagingDouble(temMap -> Double.parseDouble(temMap.get(spe).toString())));

                BigDecimal bigDecimal = new BigDecimal(value);

                double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                avgMap.put(spe,f1);

            }

            Aqi aqi = AqiUtil.getAqi(avgMap);

            avgMap.put("AQI", aqi.getAqi());

            avgMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

            avgMap.put("primaryPollutant",aqi.getPrimaryPollutant());

            avgMap.put("regionId", regionId);

            avgMap.put("timePoint", timePoint);

            String regionName = RegionUtils.getRegionName(regionId);

            String parentName = RegionUtils.getParentName(regionId, regionName);

            avgMap.put("regionName", regionName);

            avgMap.put("parentName", parentName);

            regionCodeList.add(avgMap);

        }

        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(regionCodeList, speciesList.get(length));
        }

        return regionCodeList;

    }

    @Override
    public List getRankResultDataByRegionCodesInDate(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCodes) {

        List<Map<String,Object>> resultList = new ArrayList();

        boolean resetCache = false;

        Map resultMap = new LinkedHashMap();

        long day = ChronoUnit.DAYS.between(startTime, endTime);

        for(int currDay = 0; currDay <= day ;currDay++) {

            List<Map<String,Object>> resultList1 = new ArrayList();

            Set cacheSet = new HashSet();

            LocalDateTime startTime1 = startTime.plus(currDay, ChronoUnit.DAYS);

            Set<String> regionCodes1 = new HashSet(regionCodes);

            regionCodes1.parallelStream().forEach(regionCode->{

                String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionCode);

                Map map = (Map)redisCache.get(key);

                if(map != null) {

                    resultList1.add(map);

                    cacheSet.add(regionCode);

                }
            });

            //取出已经有的缓存
            regionCodes1.removeAll(cacheSet);

            Map map = regionCodes1.stream().collect(Collectors.groupingBy(str -> str.length()));

            List provinceList = null;

            List cityList = null;

            List countyList = null;

            if(map.containsKey(2)) {

                provinceList = (List)map.get(2);
            }

            if(map.containsKey(4)) {

                cityList = (List)map.get(4);
            }

            if(map.containsKey(6)) {

                countyList = (List)map.get(6);
            }

            if(provinceList != null && provinceList.size() != 0) {

                List provinceTemList = new ArrayList<>();

                provinceList.stream().forEach(regionCode->{

                    provinceTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> provinceMap1  = stationDetailCopyMapper.getEveryDayByProvince(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS),provinceTemList);

                provinceMap1.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });


                resultList1.addAll(provinceMap1);
            }

            if (cityList != null && cityList.size() != 0) {

                List cityTemList = new ArrayList<>();

                cityList.stream().forEach(regionCode->{

                    cityTemList.add(RegionUtils.convertRegionCode(regionCode.toString()));

                });

                List<Map<String,Object>> cityMap1= stationDetailCopyMapper.getEveryDayByCity(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), cityTemList);

                cityMap1.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });

                resultList1.addAll(cityMap1);
            }

            if(countyList != null && countyList.size() != 0) {

                List<Map<String,Object>>  countyMap = stationDetailCopyMapper.getEveryDayByCounty(startTime.plus(currDay,ChronoUnit.DAYS), startTime.plus(currDay+1,ChronoUnit.DAYS), countyList);

                countyMap.stream().forEach(Map->{

                    String regionId =  (String)Map.get("regionId");

                    String key = String.join(":",Constants.AVG_DATE_KEY_EACH_REGION,DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startTime1),regionId);

                    if(!StringUtils.isEmpty(regionId)) {

                        redisCache.set(key,Map);

                    }

                });

                resultList1.addAll(countyMap);
            }

            List avgMap = new ArrayList<>();

            for(Map<String,Object> everyMap: resultList1) {

                Map species = new HashMap();

                String regionId =  (String)everyMap.get("regionId");

                String timePoint =  (String)everyMap.get("timePoint");

                LocalDateTime dateTime = LocalDateTime.parse(timePoint, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                timePoint = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime);

                for(String spe : speciesConfig.getSpeciesNoAQI()) {

                    Object obj = everyMap.get(spe);

                    Double value = 0.0;

                    if(obj != null) {

                        value = ((Double) obj);

                    }

                    species.put(spe,value);

                    BigDecimal bigDecimal = new BigDecimal(value);

                    double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                    everyMap.put(spe,f1);

                }


                Aqi aqi = AqiUtil.getAqi(species);

                everyMap.put("AQI", aqi.getAqi());

                everyMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

                everyMap.put("primaryPollutant",aqi.getPrimaryPollutant());

                everyMap.put("regionId", regionId);

                everyMap.put("timePoint", timePoint);

                String regionName = RegionUtils.getRegionName(regionId);

                String parentName = RegionUtils.getParentName(regionId,regionName);

                everyMap.put("regionName", regionName);

                everyMap.put("parentName", parentName);

                avgMap.add(everyMap);

            }

            List<String> speciesList = speciesConfig.getSpecies();

            int length = speciesList.size();

            while(length-- > 0) {

                sort(avgMap, speciesList.get(length));
            }

            resultList.addAll(avgMap);
        }

        return resultList;
    }

    @Override
    public List getRankResultDataNow(Set<String> regionCodes) {

        List resultList = new ArrayList();

        Map.Entry<LocalDateTime,Map> entry = actualTimeCache.getCacheNewMap();

        LocalDateTime latestTime = entry.getKey();

        Map regionMap = entry.getValue();

        for (String regionCode: regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            Map eachMap = null;

            if(regionMap.containsKey(regionCode)) {

                 eachMap = (Map)regionMap.get(regionCode);


            }

            else {

                switch (regionCode.length()) {

                    case 2:

                        eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity1(latestTime, regionCode + "%");

                        break;

                    case 4:

                        eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity1(latestTime, regionCode + "%");


                        break;

                    case 6:

                        eachMap = stationDetailCopyMapper.getAvgSpeciesResultByCounty2(latestTime, regionCode);

                        break;
                }

                regionMap.put(regionCode,eachMap);
            }

            Map species = new HashMap();

            if(eachMap != null && eachMap.size()!= 0) {

                for(String spe : speciesConfig.getSpeciesNoAQI()) {

                    Object obj = eachMap.get(spe);

                    Double value = 0.0;

                    if(obj != null) {

                        value = ((Double) obj);

                    }

                    species.put(spe,value);

                    BigDecimal bigDecimal = new BigDecimal(value);

                    double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                    eachMap.put(spe,f1);

                }

                Aqi aqi = AqiUtil.getAqi(species);

                eachMap.put("AQI", aqi.getAqi());

                eachMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

                eachMap.put("primaryPollutant",aqi.getPrimaryPollutant());

                eachMap.put("regionId", regionCode);

                eachMap.put("timePoint", DateTimeFormatter.ofPattern("yyyy-MM-dd_HH").format(latestTime));

                String regionName = RegionUtils.getRegionName(regionCode);

                String parentName = RegionUtils.getParentName(regionCode,regionName);

                eachMap.put("regionName", regionName);

                eachMap.put("parentName", parentName);

                resultList.add(eachMap);
            }

        }

        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(resultList, speciesList.get(length));
        }



        return resultList;
    }


 /*   public List getRankResultDataNow1(Set<String> regionCodes) {

        List resultList = new ArrayList();

        Map.Entry<LocalDateTime,Map> entry = actualTimeCache.getCacheNewMap();

        LocalDateTime latestTime = entry.getKey();

        Map regionMap = entry.getValue();

        for (String regionCode: regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            Map eachMap = null;

            if(regionMap.containsKey(regionCode)) {

                eachMap = (Map)regionMap.get(regionCode);


            }

            else {

                switch (regionCode.length()) {

                    case 2:

                        eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity(latestTime,regionCode + "%");

                        break;

                    case 4:

                        eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity(latestTime, regionCode + "%");


                        break;

                    case 6:

                        eachMap = null;

                        break;
                }

                regionMap.put(regionCode,eachMap);
            }

            Map species = new HashMap();

            for(String spe : speciesConfig.getSpeciesNoAQI()) {

                Object obj = eachMap.get(spe);

                Double value = 0.0;

                if(obj != null) {

                    value = ((Double) obj);

                }

                species.put(spe,value);

                BigDecimal bigDecimal = new BigDecimal(value);

                double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                eachMap.put(spe,f1);

            }

            Aqi aqi = AqiUtil.getAqi(species);

            eachMap.put("AQI", aqi.getAqi());

            eachMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

            eachMap.put("primaryPollutant",aqi.getPrimaryPollutant());

            eachMap.put("regionId", regionCode);

            eachMap.put("timePoint", DateTimeFormatter.ofPattern("yyyy-MM-dd_HH").format(latestTime));

            String regionName = RegionUtils.getRegionName(regionCode);

            String parentName = RegionUtils.getParentName(regionCode,regionName);

            eachMap.put("regionName", regionName);

            eachMap.put("parentName", parentName);

            resultList.add(eachMap);

        }

        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(resultList, speciesList.get(length));
        }



        return resultList;
    }*/

    @Override
    public List getRankResultDataLast24(Set<String> regionCodes) {

       Map map = actualTimeCache.getAll();

       List resultList = new ArrayList();

       Iterator<Map.Entry<LocalDateTime,Map>> iterator = map.entrySet().iterator();

       if(24 != map.size()) {



       }

       while(iterator.hasNext()) {

            List regionCodeList = new ArrayList();

            Map.Entry<LocalDateTime,Map> everyEntry = iterator.next();

            for (String regionCode: regionCodes) {

                regionCode = RegionUtils.convertRegionCode(regionCode);

                LocalDateTime dateTime = everyEntry.getKey();

                Map regionMap = everyEntry.getValue();

                Map eachMap = null;

                if(regionMap.containsKey(regionCode)) {

                    eachMap = (Map)regionMap.get(regionCode);
                }

                else {

                    switch (regionCode.length()) {

                        case 2:

                            eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity1(dateTime, regionCode + "%");

                            break;

                        case 4:

                            eachMap = stationDetailCopyMapper.getAvgSpeciesResultByProvinceOrCity1(dateTime, regionCode + "%");


                            break;

                        case 6:

                            eachMap = stationDetailCopyMapper.getAvgSpeciesResultByCounty2(dateTime, regionCode);

                            break;
                    }

                    regionMap.put(regionCode,eachMap);
                }


                Map species = new HashMap();

                for(String spe : speciesConfig.getSpeciesNoAQI()) {

                    Object obj = eachMap.get(spe);

                    Double value = 0.0;

                    if(obj != null) {

                        value = ((Double) obj);

                    }

                    species.put(spe,value);

                    BigDecimal bigDecimal = new BigDecimal(value);

                    double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                    eachMap.put(spe,f1);

                }

                Aqi aqi = AqiUtil.getAqi(species);

                eachMap.put("AQI", aqi.getAqi());

                eachMap.put("quality",AqiUtil.getQuality(aqi.getAqi()));

                eachMap.put("primaryPollutant",aqi.getPrimaryPollutant());

                eachMap.put("regionId", regionCode);

                eachMap.put("timePoint", DateTimeFormatter.ofPattern("yyyy-MM-dd_HH").format(dateTime));

                String regionName = RegionUtils.getRegionName(regionCode);

                String parentName = RegionUtils.getParentName(regionCode,regionName);

                eachMap.put("regionName", regionName);

                eachMap.put("parentName", parentName);

                regionCodeList.add(eachMap);
            }

            List<String> speciesList = speciesConfig.getSpecies();

            int length = speciesList.size();

            while(length-- > 0) {

               sort(regionCodeList, speciesList.get(length));
            }

            resultList.addAll(regionCodeList);
        }

        Collections.reverse(resultList); //翻转

        return resultList;
    }

    @Override
    public List getRankResultDataNowByGroupBy(String groupId) {

        GroupRegion groupRegion = groupRegionService.selectGroupRegionByID(groupId);

        String [] strs = groupRegion.getValue().split(",");

        Set set = new LinkedHashSet(Arrays.asList(strs));

        return getRankResultDataNow(set);

    }

    @Override
    public List getRankResultDataByRegionCodes1(LocalDateTime startTime1, LocalDateTime endTime1, String groupId) {

        GroupRegion groupRegion = groupRegionService.selectGroupRegionByID(groupId);

        String [] strs = groupRegion.getValue().split(",");

        Set set = new LinkedHashSet(Arrays.asList(strs));

        return getRankResultDataByRegionCodes(startTime1,endTime1,set);
    }

    public  List getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime) {

        List<Map> list = null;

        List areasList = parameter.getAreas();

        String datePattern = "";

        switch (parameter.getTunit()) {

            case "m":

                datePattern = "yyyy-MM";

                break;

            case "d":

                datePattern = "yyyy-MM-dd";

                break;

            default:
        }

        StringBuffer redisKey = new StringBuffer(Constants.RANK_KEY + DateTimeFormatter.ofPattern(datePattern).format(startTime));

        if (areasList != null && areasList.size() != 0) {

            redisKey.append("areas-").append(areasList.parallelStream().collect(Collectors.joining(",")));
        }

        if (redisCache.exists(redisKey.toString())) {

            list = (List) redisCache.get(redisKey.toString());

            return list;
        }

        if (list == null) {

            StringBuffer preSql = new StringBuffer("");

            StringBuffer headSql = new StringBuffer("");

            String tailSql = "";

            List<String> species = speciesConfig.getSpecies();

            String result = "";
            //随机三位字符
            Random random = new Random();

            for (int j = 0; j < 3; j++) {

                result += (char) (97 + random.nextInt(25));

            }

            for (int i = 0; i < species.size(); i++) {

                preSql.append("set @").append(result + i).append(" = 0").append(";");

                headSql.append("select *,@").append(result).append(i).append(":=@").append(result).append(i).append("+1 as ").append("rank" + species.get(i)).append(" from ( ");

                tailSql = " ) q" + i + " order by val_" + species.get(i) + " asc " + tailSql;
            }

            list = stationDetailMapper.getRankResultDataByArea(preSql.toString(), headSql.toString(), parameter, startTime, endTime, tailSql);

            if (list != null && list.size() != 0) {

                for (Map map : list) {

                    String area = (String) map.get("area");

                    String cityCode = CityCacheUtil.newInstance().getRegionCode(area);

                    if (!StringUtils.isEmpty(cityCode)) {

                        map.put("area", cityCode);
                    }
                }
            }

            redisCache.set(redisKey.toString(), list);
        }
        return list;
    }

    public  Map<String,Object> getRankResultDataByRe(LocalDateTime startTime,LocalDateTime endTime,String regionCode) {

        String tableName;

        List repeatCodes = null;

        Boolean unionFlag = false;

        List<String> stationCodes = StationCacheUtil.newInstance().getStationListByCityCode(regionCode).stream().map(StationVo::getStationId).collect(Collectors.toList());

        LocalDateTime pm_25_jjj_startTime = DateUtils.convertStringToLocalDateTime(sysConfig.getStartTime(), "yyyy-MM-dd HH:mm:ss");

        if(pm_25_jjj_startTime.isBefore(endTime) || pm_25_jjj_startTime .isEqual(endTime)) {

            repeatCodes = stationDetailMapper.getRepeatStationCodes();

            unionFlag = true;
        }

        if(stationCodes != null && stationCodes.size()!= 0 && repeatCodes!= null && repeatCodes.size()!= 0 ) {

            List copyStaionCodes =  new ArrayList<>();

            copyStaionCodes.addAll(stationCodes);

            stationCodes.removeAll(repeatCodes);

            repeatCodes.retainAll(copyStaionCodes);

        }


        if( endTime.getYear() == sysConfig.getStartYear()) {

            tableName = sysConfig.getYearTable().get(String.valueOf(endTime.getYear()));
        }
        else {

            tableName = sysConfig.getYearTable().get("default");
        }

        if(stationCodes == null ||  stationCodes.size() == 0) {

            return null;
        }
        else {

            Map<String,Object> map = stationDetailMapper.getAvgResultByStas(tableName, startTime, endTime, stationCodes, repeatCodes);

            return map;
        }
    }

    private List<Map<String,Object>> commonRank(LocalDateTime startTime,LocalDateTime endTime,String timePoint,Set<String> regionCodes ) {

        List list = new ArrayList();

        boolean cache = true;//当前是否使用cache

        LocalDateTime nowTime = LocalDateTime.now();

        LocalDateTime dateTime = LocalDateTime.of(nowTime.getYear(),nowTime.getMonthValue(),nowTime.getDayOfMonth(),0,0,0);

        if(startTime.toLocalDate().isEqual(nowTime.toLocalDate())) {

             cache = false;

        }

        String rankKey = Constants.RRANK_STTOEN_KEY+Md5Util.generateKay(startTime,endTime,regionCodes);

        List redisList = null;

        if(cache) {

            redisList = (List)redisCache.get(rankKey);

        }

        if(redisList != null && redisList.size() != 0) {

            return redisList;
        }

        for (String regionCode: regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            String key = Constants.AVG_STTOEN_KEY+Md5Util.generateKay(startTime,endTime,regionCode);

            Map map1 = null;

            if(cache) {

                map1 = (Map)redisCache.get(rankKey);

            }

            if(map1 == null || map1.size() == 0) {

                map1 = this.getRankResultDataByRe(startTime, endTime, regionCode);

                Map species = new HashMap();

                for(String spe : speciesConfig.getSpeciesNoAQI()) {

                    Object obj = map1.get(spe);

                    Double value = 0.0;

                    if(obj != null) {

                        value = ((Double) obj);

                    }

                    species.put(spe,value);

                    BigDecimal bigDecimal = new BigDecimal(value);

                    double f1 = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); //物种保留两位小数

                    map1.put(spe,f1);

                }

                Aqi aqi = AqiUtil.getAqi(species);

                map1.put("AQI", aqi.getAqi());

                map1.put("quality",AqiUtil.getQuality(aqi.getAqi()));

                map1.put("primaryPollutant",aqi.getPrimaryPollutant());

                map1.put("regionId", regionCode);

                map1.put("timePoint", timePoint);

                String regionName = RegionUtils.getRegionName(regionCode);

                String parentName = RegionUtils.getParentName(regionCode,regionName);

                map1.put("regionName", regionName);

                map1.put("parentName", parentName);

                if(cache) {

                    redisCache.set(key,map1);
                }
            }

            if(map1 == null) {

                map1 = new HashMap();
            }

            list.add(map1);

        }
        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(list, speciesList.get(length));
        }

        if(cache) {

            redisCache.set(rankKey,list);
        }

        return list;
    }

    private void sort(List<Map<String,Object>> list,String species) {

        list = list.parallelStream().sorted((map1, map2) -> {

            Object obj1 = map1.get(species);

            Object obj2 = map2.get(species);

            if (obj1 == null && obj2 == null) {

                return 0;

            } else if (obj1 == null && obj2 != null) {

                return 1;
            }
            else if (obj2 == null && obj1 != null) {

                return -1;
            }

            if (obj1 instanceof Double && obj2 instanceof Double) {

                if ((((Double) obj1) - ((Double) obj2)) > 0)

                    return -1;

                else if ((((Double) map1.get(species)) - ((Double) map2.get(species))) == 0)

                    return 0;

                else

                    return 1;

            } else if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {

                if ((((BigDecimal) obj1).compareTo((BigDecimal) obj2)) > 0)

                    return -1;

                else if ((((BigDecimal) obj1).compareTo((BigDecimal) obj2)) == 0)

                    return 0;
                else
                    return 1;


            } else {

                return 0;
            }


        }).collect(Collectors.toList());

        int count = list.size();

        for(Map<String,Object> map:list) {

            map.put("rank"+species,count--);
        }

    }

}
