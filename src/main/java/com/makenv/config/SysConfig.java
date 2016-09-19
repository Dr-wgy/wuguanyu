package com.makenv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * Created by dell on 2015-9-14.
 */
@Configuration
@ConfigurationProperties(locations = "file:etc/system.yml", prefix = "")
public class SysConfig {
  private Boolean isCache,autoBuild;
  private String cacheDir;
  private HashMap<String,String> yearTable;
  private Integer startYear;

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  private String startTime;

  public Boolean getIsCache() {
    return isCache;
  }

  public void setIsCache(Boolean isCache) {
    this.isCache = isCache;
  }

  public String getCacheDir() {
    return cacheDir;
  }

  public void setCacheDir(String cacheDir) {
    this.cacheDir = cacheDir;
  }

  public HashMap<String, String> getYearTable() {
    return yearTable;
  }

  public void setYearTable(HashMap<String, String> yearTable) {
    this.yearTable = yearTable;
  }

  public Boolean getAutoBuild() {
    return autoBuild;
  }

  public void setAutoBuild(Boolean autoBuild) {
    this.autoBuild = autoBuild;
  }

  public Integer getStartYear() {
    return startYear;
  }

  public void setStartYear(Integer startYear) {
    this.startYear = startYear;
  }
}
