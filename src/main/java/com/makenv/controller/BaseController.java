package com.makenv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 */
public class BaseController {

    private final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected final String RESULT = "result";

    protected final String SUCCESS = "success";

    protected final String FAILED = "failed";

    protected final String DATA = "data";

    protected final String INFO = "info";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> ajaxError(Throwable error, HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> map = new HashMap<String, Object>();

        logger.info("Exception", error);

        error.printStackTrace();

        map.put(INFO, error.getMessage()+" : "+error.getCause());

        map.put(RESULT, FAILED);

        return map;
    }


}
