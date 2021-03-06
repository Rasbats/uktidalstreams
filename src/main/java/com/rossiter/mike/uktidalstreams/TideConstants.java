package com.rossiter.mike.uktidalstreams;

/**
 * Created by lutusp on 9/15/17.
 */

final public class TideConstants {
    // prevent instantiation
    private TideConstants() {
    }
    static final String SYSTEM_EOL = System.getProperty("line.separator");

    static final String SYSTEM_FILESEP = System.getProperty("file.separator");

    static final char DEGREE_CHAR = (char)176;

    static final double M_PI = 3.141592653589793;

    static final double M_PI2 = 6.283185307179586;

    static final double M_PID2 = 1.570796326794897;

    static final double RADIANS = 0.01745329251994329;

    static final double DEGREES = 57.29577951308232;

    static final double SECONDS_TO_HOURS = 2.7777777777777778e-4;

    static final double FEET_TO_METERS = .3048;
    static final double METERS_TO_FEET = 3.280839895013123;

    static final double KNOTS_TO_MPH = 1.15078;
    static final double KNOTS_TO_MS = 0.514444;

    static final double  convday = 4.16666666666667E-02;
    static final double  convhour = 6.66666666666667E-02;
    static final double  convmin = 1.66666666666667E-02;
    static final double  convsec = 2.77777777777778E-04;
    static final double  convcircle = 2.77777777777778E-03;

    static final double  SUNSET =  -.833333333333333;
    static final double  CIVIL = -6.0;
    static final double  NAUTICAL = -12.0;
    static final double  ASTRONOMICAL  = -18.0;

    static final String dowNames[] = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    static final String monthNames[] = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    //static String[] barPositions = {
    //        BorderLayout.NORTH
    //        ,BorderLayout.EAST
    //        ,BorderLayout.SOUTH
    //        ,BorderLayout.WEST
    //};
}
