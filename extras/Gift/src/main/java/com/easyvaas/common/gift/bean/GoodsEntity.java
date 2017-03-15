package com.easyvaas.common.gift.bean;

public class GoodsEntity {
    public static final int TYPE_EMOJI = 0;
    public static final int TYPE_BALERY = 1;
    public static final int TYPE_GIFT = 2;

    public static final int ANI_TYPE_NO = 0;
    public static final int ANI_TYPE_STATIC = 1;
    public static final int ANI_TYPE_GIF = 2;
    public static final int ANI_TYPE_ZIP = 3;
    public static final int ANI_TYPE_RED_PACK = 4;

    public static final int COST_TYPE_BALERY = 0;
    public static final int COST_TYPE_E_COIN = 1;

    private int id;
    private String name;
    private int type;
    private String pic;
    private String ani;
    private int cost;
    private int costtype;
    private int anitype;
    private int exp;

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    private boolean isChecked;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCosttype(int costtype) {
        this.costtype = costtype;
    }

    public void setAnitype(int anitype) {
        this.anitype = anitype;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getPic() {
        return pic;
    }

    public String getAni() {
        return ani;
    }

    public int getCost() {
        return cost;
    }

    public int getCosttype() {
        return costtype;
    }

    public int getAnitype() {
        return anitype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
