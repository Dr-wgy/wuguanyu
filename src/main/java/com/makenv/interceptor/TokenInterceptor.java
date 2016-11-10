package com.makenv.interceptor;

import com.makenv.constant.Constants;
import com.makenv.domain.VisitToken;
import com.makenv.mapper.VisitTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.time.LocalDateTime;

/**
 * Created by wgy on 2016/11/2.
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {


    @Autowired
    private VisitTokenMapper visitTokenMapper;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter(Constants.TOKEN);

        if(StringUtils.isEmpty(token)) {

            throw new RuntimeException("token is not be null");
        }

        LocalDateTime now = LocalDateTime.now();

        VisitToken visitToken = visitTokenMapper.selectByTokenIdAndLocalDateTime(token, now);

        if(visitToken == null  && StringUtils.isEmpty(visitToken.getTokenId())) {

            throw new RuntimeException("the pattern of token is not right");

        }

        request.setAttribute(Constants.TOKEN,visitToken);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
