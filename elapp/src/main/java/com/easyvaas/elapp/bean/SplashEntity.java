package com.easyvaas.elapp.bean;

/**
 * Created by yinyongliang on 2017/3/27.
 */

public class SplashEntity {


    /**
     * reterr : OK
     * retinfo : {"id":2,"valid":1,"starttime":"1490580338","endtime":"1490601941","adurl":"http://image-cdn.hooview.com/hooviewportal/upload_e80eb637207bcec7fc01842e91a8d9ee.jpg"}
     * retval : ok
     * timestamp : 1490580400
     */

    private String reterr;
    private RetinfoBean retinfo;
    private String retval;
    private long timestamp;

    public String getReterr() {
        return reterr;
    }

    public void setReterr(String reterr) {
        this.reterr = reterr;
    }

    public RetinfoBean getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(RetinfoBean retinfo) {
        this.retinfo = retinfo;
    }

    public String getRetval() {
        return retval;
    }

    public void setRetval(String retval) {
        this.retval = retval;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class RetinfoBean {
        /**
         * id : 2
         * valid : 1
         * starttime : 1490580338
         * endtime : 1490601941
         * adurl : http://image-cdn.hooview.com/hooviewportal/upload_e80eb637207bcec7fc01842e91a8d9ee.jpg
         */

        private int id;
        private int valid;
        private String starttime;
        private String endtime;
        private String adurl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getAdurl() {
            return adurl;
        }

        public void setAdurl(String adurl) {
            this.adurl = adurl;
        }
    }
}
