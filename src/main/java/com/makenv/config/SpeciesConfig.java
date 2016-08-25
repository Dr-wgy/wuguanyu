package com.makenv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dell on 2015-9-14.
 */
@Configuration
@ConfigurationProperties(locations = "file:etc/species.yml")
public class SpeciesConfig {
  private List<String> species,speciesNoAQI,AQIFilter,AQIName;
  private HashMap<String,String>speciesFilter;


  public List<String> getSpecies() {
    return species;
  }

  public void setSpecies(List<String> species) {
    this.species = species;
  }

  public List<String> getSpeciesNoAQI() {
    return speciesNoAQI;
  }

  public void setSpeciesNoAQI(List<String> speciesNoAQI) {
    this.speciesNoAQI = speciesNoAQI;
  }

  public HashMap<String, String> getSpeciesFilter() {
    return speciesFilter;
  }

  public void setSpeciesFilter(HashMap<String, String> speciesFilter) {
    this.speciesFilter = speciesFilter;
  }

  public List<String> getAQIFilter() {
    return AQIFilter;
  }

  public void setAQIFilter(List<String> AQIFilter) {
    this.AQIFilter = AQIFilter;
  }

  public List<String> getAQIName() {
    return AQIName;
  }

  public void setAQIName(List<String> AQIName) {
    this.AQIName = AQIName;
  }
}
