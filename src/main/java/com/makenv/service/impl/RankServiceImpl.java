package com.makenv.service.impl;

import com.makenv.cache.*;
import com.makenv.condition.StationDetailCondition;
import com.makenv.config.SpeciesConfig;
import com.makenv.config.SysConfig;
import com.makenv.constant.Constants;
import com.makenv.domain.Province;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.mapper.StationMapper;
import com.makenv.service.AsyncService;
import com.makenv.service.RankService;
import com.makenv.service.StationDetailService;
import com.makenv.task.MonthTask;
import com.makenv.task.SpecialDealer;
import com.makenv.util.DateUtils;
import com.makenv.util.RegionUtils;
import com.makenv.vo.StationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/11/16.
 */
@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private StationDetailMapper stationDetailMapper;

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private SpeciesConfig speciesConfig;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private AsyncService asyncService;

    @Override
    @Cacheable(key = "#year+#month+#date+#regionCode.toString()",condition = "T(java.time.LocalDate).now().isAfter(T(java.time.LocalDate).of(#year,#month,#date))",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, TreeSet<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month, date, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.DAYS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime, endTime,"yyyy-MM-dd",regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#month+#regionCode.toString()",condition = "T(java.time.LocalDate).now().getYear() > #year || T(java.time.LocalDate).now().getMonthValue() > #month",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, TreeSet<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month,1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.MONTHS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime,endTime,"yyyy-MM",regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#month+#date+#hour+#regionCode.toString()",condition = "T(java.time.LocalDateTime).now().getYear() > #year || T(java.time.LocalDateTime).now().getMonthValue() > #month || " +
            "T(java.time.LocalDateTime).now().getDayOfMonth() > #date || T(java.time.LocalDateTime).now().getHour() > #hour",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Integer hour, TreeSet<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, month,date, hour, 0);

        LocalDateTime endTime = startTime;

        List list = commonRank(startTime, endTime,"yyyy-MM-dd_HH",regionCode);

        return list;
    }

    @Override
    @Cacheable(key = "#year+#regionCode.toString()",condition = "T(java.time.LocalDate).now().getYear() > #year",value = "rank")
    public List getRankResultDataByRegionCodes(Integer year, TreeSet<String> regionCode) {

        LocalDateTime startTime = LocalDateTime.of(year, 1, 1, 0, 0);

        LocalDateTime endTime = startTime.plus(1, ChronoUnit.YEARS);

        endTime = endTime.minus(1,ChronoUnit.HOURS);

        List list = commonRank(startTime, endTime,"yyyy",regionCode);

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

    private List commonRank(LocalDateTime startTime,LocalDateTime endTime,String pattern,TreeSet<String> regionCodes ) {

        List list = new ArrayList();

        String timePoint = DateTimeFormatter.ofPattern(pattern).format(startTime);

        for (String regionCode: regionCodes) {

            regionCode = RegionUtils.convertRegionCode(regionCode);

            Map map1 = this.getRankResultDataByRe(startTime, endTime, regionCode);

            if(map1 == null) {

                map1 = new HashMap();
            }

            map1.put("regionId", regionCode);

            map1.put("timePoint", timePoint);

            String regionName = "";

            switch (regionCode.length()) {

                case 2:
                    regionName = ProvinceCacheUtil.newInstance().getArea(regionCode);

                    break;

                case 4:

                    regionName = CityCacheUtil.newInstance().getArea(regionCode);

                    break;

                case 6:

                    regionName = CountyCacheUtil.newInstance().getArea(regionCode);



            }

            if(StringUtils.isEmpty(regionName))

                map1.put("regionName", regionName);


            list.add(map1);

        }
        List<String> speciesList = speciesConfig.getSpecies();

        int length = speciesList.size();

        while(length-- > 0) {

            sort(list, speciesList.get(length));
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
