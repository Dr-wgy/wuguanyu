package com.makenv.util;

/**
 * Created by wgy  2016/8/12.
 */
public class MathUtils {


    /**
     *
     * @param a 弧度制不是角度值
     * @return
     */
    public  static double sin(double a){

           if(a % Math.PI == 0) {

               return 0.0;
           }

           else {
               return Math.sin(a);
           }

    }

    /**
     *
     * @param b 弧度制不是角度值
     * @return
     */
    public static double cos(double b){

        if(b % Math.PI/2 == 0) {

            return 0.0;
        }

        else {

            return Math.cos(b);

        }
    }
}
