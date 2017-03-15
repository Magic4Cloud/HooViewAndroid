package com.easyvaas.common.gift.action;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.action.type.Type;

public class Action {
    protected Type type;
    protected FromType fromType;
    protected AnimType animType;
    protected String senderID;
    protected String senderName;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public FromType getFromType() {
        return fromType;
    }

    public void setFromType(FromType fromType) {
        this.fromType = fromType;
    }

    public AnimType getAnimType() {
        return animType;
    }

    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", fromType=" + fromType +
                ", animType=" + animType +
                ", senderName='" + senderName + '\'' +
                '}';
    }
}
