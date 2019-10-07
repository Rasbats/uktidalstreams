package com.rossiter.mike.uktidalstreams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.abs;

/**
 * Created by mike on 28/03/18.
 */

public class PreviousNext {

    MapActivity app;
    int button_id;

    PreviousNext(MapActivity m) {
        app = m;//(TidePredictorApplication) main.getApplication();
    }

    public String[] label_array = new String[]{
        "HW-6",
        "HW-5",
        "HW-4",
        "HW-3",
        "HW-2",
        "HW-1",
        "HW",
        "HW+1",
        "HW+2",
        "HW+3",
        "HW+4",
        "HW+5",
        "HW+6"
    };

    public Date calcArrowsDateTime(Date inDate, int inDiffHours)
    {
        long millis = inDiffHours*60*60*1000;
        long newMillies = (inDate.getTime() + millis);

        Date myReturnDate = longToDate(newMillies);
        return myReturnDate;
    }

    public int calcHoursFromHWNow(Date inDate)
    {
        double myDiff, myTest;

        myDiff = 0.;

        myTest = 26;
        int i;
        long millis = System.currentTimeMillis();
        for (i = 0; i<8;i++)
        {
            long diffInMillies = (millis - inDate.getTime());

            myDiff = (double) diffInMillies;
            myDiff  = myDiff /1000/60/60;

            if (abs(myDiff) < abs(myTest))
            {
                myTest = myDiff;
            }
        }

        int f = round( myTest);

        return f ;
    }

    private int round(double c)
    {
        // c = -0.52
        int a = (int) c; //a =  0
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
        //wxString str_countPts =  wxString::Format(wxT("%d"), (int)c);
        // wxMessageBox(str_countPts,_("count_hours"));
        return (int)c;
    }

    public Date stringToDate(String aDate) {

        if(aDate==null) return null;

        String myFormat = "E dd/MM/yyyy HH:mm";

        Date date = null;
        try {
            date = new Date();
            date = new SimpleDateFormat(myFormat).parse(aDate);
        }
        catch (ParseException e) { e.printStackTrace(); }

        return date;
    }

    public Date spinHWStringToDate(String aHWDate) {

        if(aHWDate==null) return null;

        int plus = aHWDate.indexOf("+");
        String myHWDate = aHWDate.substring(0, plus);

        Date date = null;
        date = stringToDate(myHWDate);

        return date;
    }

    public Date longToDate(long millisecondsDate) {

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecondsDate);
        return calendar.getTime();

    }

    public String dateToString(Date aDate) {

        if(aDate==null) return null;

        String myFormat = "E dd/MM/yyyy HH:mm";
        SimpleDateFormat simpledateformat = new SimpleDateFormat(myFormat);
        String reportDate = simpledateformat.format(aDate);

        return reportDate;
    }

    public int hoursDiffFromHWarrayTime(Date inDate){

        int r = 0;
        int hwCount = -1;

        Date myHWDate = null;

        for (int f = 0; f < app.arrayHW.size(); f++) {
            String myHW = app.arrayHW.get(f).toString();
            String testH =  myHW.substring(myHW.length()-1, myHW.length());
            if(testH.equals("H")) {
                hwCount++;
                int plus = myHW.indexOf("+");
                String arrayHWDate = myHW.substring(0, plus);

                myHWDate = stringToDate(arrayHWDate);

                double myDiff, myTest;

                myDiff = 0.;

                myTest = 26;
                int i;

                long millis = myHWDate.getTime();
                for (i = 0; i<8;i++)
                {
                    long diffInMillies = (inDate.getTime()- millis);

                    myDiff = (double) diffInMillies;
                    myDiff  = myDiff /1000/60/60;

                    if (abs(myDiff) < abs(myTest))
                    {
                        myTest = myDiff;
                    }
                }

                r = round( myTest);
                if (r <= 6){
                    app.spinnerHWDate = myHWDate;
                    app.spinHW.setSelection(hwCount);
                    app.m_myChoice = hwCount;
                    return r;
                }

                }

            }



        return 999;
    }

    public int diffFromHighWaterTime() {
        app.spinHW.setSelection(0);
        String myS = String.valueOf(app.spinHW.getSelectedItem());
        //int n = app.spinHW.getSelectedItemPosition();
        int plus = myS.indexOf("+");
        String dateTimePart = myS.substring(0, plus);

        Date myDate = stringToDate(dateTimePart);
        app.spinnerHWDate = myDate;
        int dn = calcHoursFromHWNow(myDate);
        if(dn > 6){
            app.spinHW.setSelection(1);
            myS = String.valueOf(app.spinHW.getSelectedItem());
            //n = app.spinHW.getSelectedItemPosition();
            plus = myS.indexOf("+");
            dateTimePart = myS.substring(0, plus);

            myDate = stringToDate(dateTimePart);
            app.spinnerHWDate = myDate;
            dn = calcHoursFromHWNow(myDate);
        }
        return dn;
    }

    public String getHWSpinnerDate(int selection) {
        int whereIsIt = -1;
        for (int f = 0; f < app.arrayHW.size(); f++) {
            String myHW = app.arrayHW.get(f).toString();
            String testH = myHW.substring(myHW.length() - 1, myHW.length());
            if (testH.equals("H")) {
                whereIsIt++;
                int plus = myHW.indexOf("+");
                String arrayHWDate = myHW.substring(0, plus);
                if (whereIsIt == selection)
                    return arrayHWDate;
            }
        }
        return "999";
    }

    public int getHWSpinnerItemsCount() {
        int itemCount = 0;
        for (int f = 0; f < app.arrayHW.size(); f++) {
            String myHW = app.arrayHW.get(f).toString();
            String testH = myHW.substring(myHW.length() - 1, myHW.length());
            if (testH.equals("H")) {
                itemCount++;
            }
        }
        return itemCount;
    }

    public String getButtonId(int i) {

        int id = 6 + i;


        return label_array[id];
    }

    public int buttonToHW(int inButton){

        int hw;
        if (inButton >= 6) {
            hw = inButton - 6;
            return hw;
        } else {
            hw = -6 + inButton;
            return hw;
        }
    }

}
