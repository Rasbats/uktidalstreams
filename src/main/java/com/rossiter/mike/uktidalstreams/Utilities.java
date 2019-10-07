package com.rossiter.mike.uktidalstreams;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by mike on 25/02/18.
 */

public class Utilities {

    public int i_black;

    double stringToLat(String sLat){

        double value,  decValue;

        String mLat;
        String mBitLat;
        String mDecLat;

        double latF;

        mLat = sLat;
        if(mLat.length()<6){
            return 0.0;
        }
        mBitLat = mLat.substring(0,2);
        mDecLat = mLat.substring(mLat.length() - 4);

        decValue = Double.parseDouble(mDecLat);
        if (decValue == 0.){
            decValue = 0.00001;
        }
        value = Double.parseDouble(mBitLat);

        latF = value + (decValue/100/60);

        return latF;
    }

    double stringToLon(String sLon){

        double value1 = 0.;
        double decValue1 = 0.;

        int m_len = 0;

        String mLon = "0";
        String mBitLon = "0";
        String mDecLon = "0";

        double latF, lonF;

        mLon = sLon;
        m_len = mLon.length();

        if (m_len == 7)
        {
            mBitLon = mLon.substring(0,3);
        }

        if (m_len == 6)
        {
            mBitLon = mLon.substring(0,2);
        }

        if  (m_len == 5)
        {
            mBitLon = mLon.substring(0,1);
        }

        if  (m_len == 4)
        {
            mBitLon = "00.00";
        }

        if (mBitLon.equals("-"))
        {
            value1 = -0.00001;
        }
        else
        {
            value1 = Double.parseDouble(mBitLon);
        }

        if (mLon.length()>= 4) {
            mDecLon = mLon.substring(mLon.length() - 4);
        } else {
            mDecLon = "0";
        }

        decValue1 = Double.parseDouble(mDecLon);

        if (value1 < 0)
        {
            lonF = value1 - decValue1/100/60;
        }
        else
        {
            lonF = value1 + decValue1/100/60;
        }

        return lonF;
    }

    double rangeOnDay(List<Double> myRanges){

        List<Double> myArrayOfRanges = new ArrayList<Double>();

        double range;
        double x;
        for (int i = 0; i < myRanges.size(); i++){
            if (i != 0 ) {
                x = abs(myRanges.get(i) - myRanges.get(i - 1));
                myArrayOfRanges.add(x);
            }
        }

        double total = 0;
        for (int j = 0; j < myArrayOfRanges.size(); j++){
            total = total + myArrayOfRanges.get(j);
        }
        range = total / myArrayOfRanges.size();

        return range;
    }

    double CalcCurrent(double m_spRange, double m_npRange, double m_spRateDiamond, double m_npRateDiamond, double m_rangeOnDay)
    {
        if (m_spRateDiamond == m_npRateDiamond)
            return m_spRateDiamond;
        else {
            // y = mx + c
            double m,c,x;
            m = (m_spRange - m_npRange) / (m_spRateDiamond - m_npRateDiamond);
            c = m_spRange - (m * m_spRateDiamond);
            x = (m_rangeOnDay - c)/m ;

            return x;
        }
    }

    double getSpdDirSpringNeap(String minus_plus, String r ) {

        String m_minus_plus = minus_plus.substring(0, 3);

        double dir;
        dir = Double.parseDouble(m_minus_plus);

        if(minus_plus.length()== 7) {
            double m_spdSpring;
            String m_speed = minus_plus.substring(3, 5);
            m_spdSpring = Double.parseDouble(m_speed);
            if (m_spdSpring == 0) {
                m_spdSpring = 0;
            } else {
                m_spdSpring = m_spdSpring / 10;
            }
            double m_spdNeap;
            String m_speed2 = minus_plus.substring(5);
            m_spdNeap = Double.parseDouble(m_speed2);

            if (m_spdNeap == 0) {
                m_spdNeap = 0;
            } else {
                m_spdNeap = m_spdNeap / 10;
            }

            if (r.equals("d")){
                return dir;
            }
            if (r.equals("s")){
                return m_spdSpring;
            }
            if (r.equals("n")){
                return m_spdNeap;
            }
        }
        return 999.;

    }

    int getSpeedColour(double my_speed){

        int c_grey = Color.rgb(128, 128, 128);
        int c_green = Color.rgb(0, 166, 80);
        int c_yellow_orange = Color.rgb(253, 184, 19);
        int c_orange = Color.rgb(248, 128, 64);
        int c_red = Color.rgb(248, 0, 0);
        i_black = Color.rgb(248, 0, 0);
        if (my_speed < 0.5){ return c_grey;}
        if ((my_speed >= 0.5) && (my_speed < 1.5)){ return c_green;}
        if ((my_speed >= 1.5) && (my_speed < 2.5)){ return c_yellow_orange;}
        if ((my_speed >= 2.5) && (my_speed < 3.5)){ return c_orange;}
        if ((my_speed >= 3.5) ){ return c_red;}

        return Color.rgb(0, 0, 0);
    }

  Integer calcHoursFromHW(List<Long> highWaters)
    {
        double myDiff, myTest;
        myDiff = 0.;
        myTest = 26.;
        int i = 0;

        for (i=0; i<highWaters.size();i++){

            long newTT = highWaters.get(i) * 1000;
            Date dd = new Date(newTT);
            long myHWTime  = dd.getTime();
            long myNowTime = System.currentTimeMillis();
            long d = (myNowTime - myHWTime)/1000;

            myDiff = (double)d/3600;

            if (abs(myDiff) < abs(myTest))
            {
                myTest = myDiff;
            }
        }
        int f = round(myTest);
        return f ;
    }

    int round(double c)
    {
        int a = (int)c;
        int b = 0;
        double input = c;

        if (a == 0)
        {
            if (c < 0)
            {
                c = c + a;   // -0.52
            }
            else
            {
                c = c - a;   //
            }
        }
        else
        {
            c = c - a; //-2.6 --2 c = -0.6
        }

        if ( abs(c) > 0.5)
        {
            b = 1;  //1
        }
        else
        {
            b = 0;
        }

        if ( a > 0) //a -2
        {
            c = a + b;
        }
        else{
            if (a == 0){

                if (input >= 0){
                    c = b;
                }
                else{
                    c -= b;
                }
            }
            else{
                c = a - b;
            }
        }

        return (int) c;
    }

}
