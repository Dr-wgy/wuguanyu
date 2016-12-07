package com.makenv.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by wgy on 2016/11/2.
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Object typeFactory;

    public static String writeJson(Object obj) {

        String jsonStr = "";

        try {

            jsonStr = objectMapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {


            e.printStackTrace();
        }

        return jsonStr;
    }

    public static <T> T getEntity(String jsonString, Class<T> prototype) {

        try {
            return (T) objectMapper.readValue(jsonString, prototype);

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    public static boolean isString(String jsonString) {

        boolean flag = true;


        try {

            objectMapper.readValue(jsonString,String.class);

        } catch (IOException e) {

            flag = false;
        }


        return flag;
    }

    public static boolean isList(String jsonString) {

        boolean flag = true;


        try {

            objectMapper.readValue(jsonString,List.class);

        } catch (IOException e) {

            flag = false;
        }

        return flag;

    }

    public static Object getList(String jsonString,JavaType javaType) {

        Object obj = null;

        try {

            obj = objectMapper.readValue(jsonString,javaType);

        } catch (IOException e) {

            e.printStackTrace();

            obj = false;
        }


        return obj;
    }

    public static TypeFactory getTypeFactory() {

        return objectMapper.getTypeFactory();
    }
}
