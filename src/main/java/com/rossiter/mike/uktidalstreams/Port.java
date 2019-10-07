package com.rossiter.mike.uktidalstreams;

/**
 * Created by mike on 17/10/17.
 */

public class Port {

    private String port_number;
    private String port_name;
    private String mean_spring_range;
    private String mean_neap_range;

    public void setNumber(String snumber) {
        this.port_number = snumber;
    }
    public void setName(String sname) {
        this.port_name = sname;
    }
    public void setMeanSR(String smsr) {
        this.mean_spring_range = smsr;
    }
    public void setMeanNR(String smnr) {
        this.mean_neap_range = smnr;
    }


    public String getNumber() {
        return port_number;
    }
    public String getName() {
        return port_name;
    }
    public String getMean_spring_range() { return mean_spring_range; }
    public String getMean_neap_range() { return mean_neap_range; }

}
