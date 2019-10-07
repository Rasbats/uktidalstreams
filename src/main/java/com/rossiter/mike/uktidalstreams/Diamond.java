package com.rossiter.mike.uktidalstreams;

/**
 * Created by mike on 17/10/17.
 */

public class Diamond {

        private String lat;
        private String lon;
        private String port_number;
        private String[] minus_plus = new String[13];

        public void setNum(String snum) {
                this.port_number = snum;
        }
        public void setLat(String slat) {
                this.lat = slat;
        }
        public void setLon(String slon) { this.lon = slon; }

        public void setDirRate(int before_after, String sDirRate){
                this.minus_plus[before_after] = sDirRate;
        }

        public String getNum() {
                return port_number;
        }
        public String getLat() {
                return lat;
        }
        public String getLon() { return lon; }
        public String getDirRate(int i) { return minus_plus[i]; }

}
