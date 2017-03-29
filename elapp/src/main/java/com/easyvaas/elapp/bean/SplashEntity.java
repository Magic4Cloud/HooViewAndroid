package com.easyvaas.elapp.bean;

/**
 * Created by yinyongliang on 2017/3/27.
 */

public class SplashEntity {


    private int id;

    private int valid;

    private String starttime;

    private String endtime;

    private String adurl;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setValid(int valid){
        this.valid = valid;
    }
    public int getValid(){
        return this.valid;
    }
    public void setStarttime(String starttime){
        this.starttime = starttime;
    }
    public String getStarttime(){
        return this.starttime;
    }
    public void setEndtime(String endtime){
        this.endtime = endtime;
    }
    public String getEndtime(){
        return this.endtime;
    }
    public void setAdurl(String adurl){
        this.adurl = adurl;
    }
    public String getAdurl(){
        return this.adurl;
    }
}
