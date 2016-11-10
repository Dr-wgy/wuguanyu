package com.makenv.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.makenv.cache.StationCacheUtil;
import com.makenv.constant.Constants;
import com.makenv.domain.VisitToken;
import com.makenv.vo.StationVo;
import com.makenv.vo.TokenTemp;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wgy on 2016/11/2.
 */
public class TokenUtil {

    public static Boolean checkValid(VisitToken visitToken, String area, String areaId) {

        if (visitToken == null || StringUtils.isEmpty(visitToken.getRegioncode()) || StringUtils.isEmpty(area)
                || StringUtils.isEmpty(areaId)) {

            return false;
        }

        String regionCode = visitToken.getRegioncode();

        if (Constants.REGION.equals(area)) {

           return checkValid(regionCode, areaId,visitToken);

        } else if (Constants.STATION.equals(area)) {

            return checkValid(regionCode, areaId,visitToken);
        }

        return false;
    }


    private static Boolean checkValid(String regionCode, String areaId,VisitToken visitToken){

        TokenTemp tokenTemp = checkValid(regionCode, areaId);

        if(StringUtils.isEmpty(tokenTemp) || !tokenTemp.isFlag()) {

            return false;
        }

        else {

            visitToken.setAreaId(tokenTemp.getAreaId());

            return tokenTemp.isFlag();

        }

    }

    private static  TokenTemp commonCheck(String regionCode, String areaId) {

        regionCode = RegionUtils.convertRegionCode(regionCode);

        if (Constants.PUBLIC_REGIONCODE.equals(regionCode)) {

            return new TokenTemp(true,areaId);
        }

        areaId = RegionUtils.convertRegionCode(areaId);

        if (areaId.startsWith(regionCode)) {

            return new TokenTemp(true,areaId);


        } else if (regionCode.startsWith(areaId)) {


            return new TokenTemp(true,regionCode);

        }

        return new TokenTemp(false);
    }


    private static  TokenTemp commonCheck(List<String> regionCodes, String areaId) {

        for(String exeryCode : regionCodes) {

            TokenTemp tokenTemp = commonCheck(exeryCode, areaId);

            if(tokenTemp.isFlag()) {

                return tokenTemp;
            }
        }

        return new TokenTemp(false);
    }

    private static TokenTemp checkValid(String regionCode, String areaId) {

        if (JsonUtil.isString(regionCode)) {

           return commonCheck(regionCode,areaId);
        }

        TypeFactory factory = JsonUtil.getTypeFactory();

        JavaType javaType = factory.constructParametrizedType(ArrayList.class, List.class, String.class);

        Object obj = JsonUtil.getList(regionCode, javaType);

        if (obj instanceof Boolean) {

            return new TokenTemp(false);

        } else if (obj instanceof String) {

            return commonCheck(regionCode,areaId);
        }
        else if(obj instanceof  List) {

            List<String> regionCodes = (List)obj;

            return commonCheck(regionCodes,areaId);

        }
        return new TokenTemp(false);
    }

}
