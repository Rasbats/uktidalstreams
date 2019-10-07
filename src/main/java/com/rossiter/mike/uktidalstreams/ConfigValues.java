package com.rossiter.mike.uktidalstreams;

import java.io.Serializable;

/**
 * Created by lutusp on 9/15/17.
 */

final public class ConfigValues implements Serializable {

    // opened if no other site is opened
    public int lastDisplayedSite = -1;
    public String favorite_sites = "";
    public int currentTab = 0;

    // height units 0 = meters, 1 = feet
    // velocity units 2 = knots, 3 = mph, 4 = m/s
    public int displayUnits = 0;

    public int heightUnits = 0;
    public int velocityUnits = 2;

    // 0 = standard, 1 = compute, 2 = daylight
    public int daylightTime = 1;

    public double timeZone = -14; // -14 means use system timezone

    public boolean ampmFlag = true;
    public boolean thickLine = false;
    public boolean chartGrid = true;
    public boolean gridNums = true;
    public boolean listBackground = false;
    public boolean boldText = false;
    public boolean sunText = true;
    public boolean dateText = true;
    public boolean sunGraphic = true;
    public boolean sunGraphicDark = true;
    public boolean siteLabel = true;
    public boolean tideList = true;
    public boolean timeLine = true;
    public boolean dataFileCreation = false;
}
