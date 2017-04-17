package com.easyvaas.elapp.bean.market;

import java.util.List;

/**
 * Created by guoliuya on 2017/2/28.
 */

public class MarketExponentModel {

    /**
     * data : {"cn":[{"datetime":"2017-02-28 14:26:24","amount":15227.9,"change":0.18,"close":3234.39,"symbol":"SH000001","name":"上证","high":3240.77,"low":3225.97,"open":3225.97,"preclose":3228.66,"volume":124064000},{"datetime":"2017-02-28 14:26:24","amount":19348.8,"change":0.08,"close":10361.9,"symbol":"SZ399001","name":"深成","high":10402.3,"low":10340.1,"open":10354.9,"preclose":10353.5,"volume":128895000},{"datetime":"2017-02-28 14:26:24","amount":5432.05,"change":-0.23,"close":1922.28,"symbol":"SZ399006","name":"创业板","high":1931.82,"low":1918.32,"open":1926.14,"preclose":1926.77,"volume":26175100}],"hk":[{"datetime":"2017-02-28 14:26:22","amount":423.163,"change":-0.08,"close":3868.36,"symbol":"HKHSCCI","name":"红筹","high":3883.44,"low":3866.31,"open":3875.52,"preclose":3871.46,"volume":0},{"datetime":"2017-02-28 14:26:24","amount":1435.95,"change":-0.14,"close":10316.3,"symbol":"HKHSCEI","name":"国企","high":10403.2,"low":10291.8,"open":10337.2,"preclose":10330.8,"volume":0},{"datetime":"2017-02-28 14:26:26","amount":7447.65,"change":-0.43,"close":23822.5,"symbol":"HKHSI","name":"恒生","high":24007.8,"low":23791.8,"open":23952.6,"preclose":23925.1,"volume":0}]}
     */

    private DataEntity data;

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public class DataEntity {
        private List<ExponentEntity> cn;
        private List<ExponentEntity> hk;

        public List<ExponentEntity> getCn() {
            return cn;
        }

        public void setCn(List<ExponentEntity> cn) {
            this.cn = cn;
        }

        public List<ExponentEntity> getHk() {
            return hk;
        }

        public void setHk(List<ExponentEntity> hk) {
            this.hk = hk;
        }

    }

    public class ExponentEntity {
        /**
         * datetime : 2017-02-28 14:26:24
         * amount : 15227.9
         * change : 0.18
         * close : 3234.39
         * symbol : SH000001
         * name : 上证
         * high : 3240.77
         * low : 3225.97
         * open : 3225.97
         * preclose : 3228.66
         * volume : 124064000
         */

        private String datetime;
        private double amount;
        private double changepercent;
        private double close;
        private String symbol;
        private String name;
        private double high;
        private double low;
        private double open;
        private double preclose;
        private int volume;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public double getPreclose() {
            return preclose;
        }

        public void setPreclose(double preclose) {
            this.preclose = preclose;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public double getChangepercent() {
            return changepercent;
        }

        public void setChangepercent(double changepercent) {
            this.changepercent = changepercent;
        }
    }

}
