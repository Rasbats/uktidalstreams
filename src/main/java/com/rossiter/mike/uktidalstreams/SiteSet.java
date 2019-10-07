package com.rossiter.mike.uktidalstreams;

/**
 * Created by lutusp on 9/15/17.
 */

final public class SiteSet {
    public String fullName;
    public String name;
    public String shortName;
    int indexNumber;
    int units; // 0 = meters, 1 = feet, 2 = knots, 3 = MPH, 4 = m/s
    int currentDisplayUnits;
    int currentYear;
    boolean daylightInEffect = false;
    boolean current; // site measures current
    boolean needRoot;
    boolean valid;
    double baseHeight;
    double tz;
    double lat;
    double lng;
    double gHiWater;
    double gLoWater;
    double mHiWater;
    double mLoWater;
    int constituentMax;
    int startYear;
    int equMax,nodeMax,endYear;
    int currentLoadedFile;
    long epochTime;
    String dataFileName;
    double constSpeeds[];
    double[][] nodeFacts,equArgs;
    Harmonic harmBase[];
    Harmonic harm[];
    public SiteSet() {
        indexNumber = -1;
        currentYear = -1;
        current = false;
        needRoot = false;
        valid = false;
        gHiWater = 10;
        gLoWater = -2;
        mHiWater = 10;
        mLoWater = -1;
        constituentMax = 0;
        currentLoadedFile = -1;
        currentDisplayUnits = -1;
    }
}
