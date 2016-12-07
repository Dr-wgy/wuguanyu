package com.makenv.util.aqi;

import com.makenv.util.aqi.model.Aqi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dell
 */
public class AqiUtil {
  private static Map<String, Integer[]> AQI_CONSTANTS = new HashMap<>();
  public final static String SO2 = "SO2";
  public final static String CO = "CO";
  public final static String NO2 = "NO2";
  public final static String PM25 = "PM25";
  public final static String PM10 = "PM10";
  public final static String O3 = "O3";
  public final static String O3_8 = "O3_8";
  public final static String SO2_24 = "SO2_24";
  public final static String CO_24 = "CO_24";
  public final static String NO2_24 = "NO2_24";
  private final static String[] ELEMENTS = new String[] { SO2, CO, NO2, PM25, PM10, O3, O3_8,SO2_24,CO_24,NO2_24 };
  static {
    AQI_CONSTANTS.put("iaqi", new Integer[] { 0, 50, 100, 150, 200, 300, 400, 500 });
    AQI_CONSTANTS.put(SO2_24, new Integer[] { 0, 50, 150, 475, 800, 1600, 2100, 2620 });
    AQI_CONSTANTS.put(SO2, new Integer[] { 0, 150, 500, 650, 800 });
    AQI_CONSTANTS.put(NO2_24, new Integer[] { 0, 40, 80, 180, 280, 565, 750, 940 });
    AQI_CONSTANTS.put(NO2, new Integer[] { 0, 100, 200, 700, 1200, 2340, 3090, 3840 });
    AQI_CONSTANTS.put(PM10, new Integer[] { 0, 50, 150, 250, 350, 420, 500, 600 });
    AQI_CONSTANTS.put(CO_24, new Integer[] { 0, 2, 4, 14, 24, 36, 48, 60 });
    AQI_CONSTANTS.put(CO, new Integer[] { 0, 5, 10, 35, 60, 90, 120, 150 });
    AQI_CONSTANTS.put(O3, new Integer[] { 0, 160, 200, 300, 400, 800, 1000, 1200 });
    AQI_CONSTANTS.put(O3_8, new Integer[] { 0, 100, 160, 215, 265, 800 });
    AQI_CONSTANTS.put(PM25, new Integer[] { 0, 35, 75, 115, 150, 250, 350, 500 });
  }
/*
  public static int getAqi(Map<String,String> value) {
    Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
    double _aqi = 0;
    for (String _element : ELEMENTS) {
      String _key = _element.toUpperCase();
      double _cp = 0;
      if (value.containsKey(_key)) {
        _cp = Double.parseDouble(value.get(_key));
      } else {

        continue;
      }
      int _bps[] = getBPLoAndHi(_element, _cp);
      if (_bps == null) {
        continue;
      }

      int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

      double _iaqip = (_iaqis[1] - _iaqis[0]) / (double) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
      if (_aqi < _iaqip) {
        _aqi = _iaqip;
      }
    }
    return (int) Math.ceil(_aqi);
  }*/

  public static String getPrimElement(Map<String, Double> elementsDatas) {
    String _primElement = null;
    Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
    double _maxAqi = 0;
    for (String _element : ELEMENTS) {
      String _key = _element.toUpperCase();
      double _cp = 0;
      if (elementsDatas.containsKey(_key)) {
        _cp = elementsDatas.get(_key);
      } else {
        // System.out.println(String.format("element %s not found",
        // _element));
        continue;
      }
      int _bps[] = getBPLoAndHi(_element, _cp);
      if (_bps == null) {
        continue;
      }

      int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

      double _iaqip = (_iaqis[1] - _iaqis[0]) / (double) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
      if (_maxAqi < _iaqip) {
        _maxAqi = _iaqip;
        _primElement = _element;
      }
    }
    if (_maxAqi <= 50) {
      return null;
    }
    return _primElement;
  }


  public static Aqi getAqi(Map<String, String> elementsDatas) {
    String _primElement = null;
    Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
    double _maxAqi = 0;
    for (String _element : ELEMENTS) {
      String _key = _element.toUpperCase();
      double _cp = 0;
      if (elementsDatas.containsKey(_key)) {
        Object obj = elementsDatas.get(_key);
        if(double.class.equals(obj.getClass()) || Double.class.equals(obj.getClass()))
        _cp = ((double)obj);
        else {
          _cp = Double.parseDouble((String) obj);
        }
      } else {
        continue;
      }
      int _bps[] = getBPLoAndHi(_element, _cp);
      if (_bps == null) {
        continue;
      }

      int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

      double _iaqip = (_iaqis[1] - _iaqis[0]) / (double) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
      if (_maxAqi < _iaqip) {
        _maxAqi = _iaqip;
        _primElement = _element;
      }
    }
/*    if (_maxAqi <= 50) {
      return null;
    }*/
    return new Aqi((int) Math.ceil(_maxAqi),_primElement);
  }

  private static int[] getBPLoAndHi(String element, double cp) {
    Integer[] _bps = AQI_CONSTANTS.get(element);
    for (int i = 0; i < _bps.length; i++) {
      if (cp <= _bps[i]) {
        if (i == 0) {
          return null;
        }
        int _lowIndex = i - 1;
        return new int[] { _lowIndex, _bps[_lowIndex], i, _bps[i] };
      }
    }
    return null;
  }


  public static Object getAqis(HashMap value) {
    int[] val=new int[24];
    for (int i=0;i<24;i++) {
      Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
      double _aqi = 0;
      for (String _element : ELEMENTS) {
        String _key = _element.toUpperCase();
        double _cp = 0;
        if (value.containsKey(_key)) {
          _cp = ((double[]) value.get(_key))[i];
        } else {
          continue;
        }
        int _bps[] = getBPLoAndHi(_element, _cp);
        if (_bps == null) {
          continue;
        }

        int _iaqis[] = new int[]{_iaqi[_bps[0]], _iaqi[_bps[2]]};

        double _iaqip = (_iaqis[1] - _iaqis[0]) / (double) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
        if (_aqi < _iaqip) {
          _aqi = _iaqip;
        }
      }
      val[i]= (int) Math.ceil(_aqi);
    }
    return val;
  }

  public static String getQuality(int aqi) {

    if(aqi <= 50) return "优";
    else if(aqi >= 51 && aqi <= 100) return "良";
    else if(aqi >= 101 && aqi <= 150) return "轻度污染";
    else if(aqi >= 151 && aqi <= 200) return "轻度污染";
    else if(aqi >= 201 && aqi <= 300) return "轻度污染";
    else return "严重污染";

  }
}