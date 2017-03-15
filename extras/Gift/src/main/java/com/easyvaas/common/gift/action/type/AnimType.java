package com.easyvaas.common.gift.action.type;

/**
 * 动画类型
 * Created by LiFZhe on 4/20/16.
 */
public enum AnimType {
    NONE("None"),
    NOTIFICATION("notification"),
    EMOJI("emoji"),
    NORMAL("normal"),
    CAR_RACING("CarRightToLeft"),
    CAR_DELUXE("CarLeftToRight"),
    BOAT("AssaultBoat"),
    METEOR("StarRain"),
    CASTLE("Castle"),
    PLANE("Airplane"),
    CAR_RED("redCar");

    public String type;

    AnimType(String type) {
        this.type = type;
    }

    public static AnimType getType(String type) {
        for (AnimType item : values()) {
            if (item.type.equals(type)) {
                return item;
            }
        }
        return NONE;
    }
}
