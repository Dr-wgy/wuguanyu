package com.makenv.serializer;

import org.springframework.core.MethodParameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by wgy on 2016/8/23.
 */
public class LocalDateTimeResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {


        if(parameter.getParameterType().equals(LocalDateTime.class)) {

            return  true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        DateTimeFormat dateTimeFormat = parameter.getParameterAnnotation(DateTimeFormat.class);

        String parameterName = parameter.getParameterName();

        String value = webRequest.getParameter(parameterName);

        if(dateTimeFormat.pattern()!= null) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat.pattern());

            LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);

            return dateTime;
        }

        return null;
    }
}
