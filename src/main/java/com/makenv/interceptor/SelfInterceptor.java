package com.makenv.interceptor;

import com.makenv.annotation.SelfAnnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wgy on 2016/8/8.
 */
@Component
public class SelfInterceptor implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(SelfInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

/*        if(handler instanceof HandlerMethod) {

            SelfAnnnotation annnotation = ((HandlerMethod) handler).getMethodAnnotation(SelfAnnnotation.class);

            if(annnotation != null) {


            }

        }*/

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public static final String getIpAddr(final HttpServletRequest request) throws Exception {

        if (request == null) {
            throw (new Exception("getIpAddr method HttpServletRequest Object is null"));
        }

        String ipString = request.getHeader("x-forwarded-for");

        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {

            ipString = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)){

            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");

        for (final String str : arr) {

            if (!"unknown".equalsIgnoreCase(str))ipString = str;

                break;
            }

        return ipString;
    }
}
