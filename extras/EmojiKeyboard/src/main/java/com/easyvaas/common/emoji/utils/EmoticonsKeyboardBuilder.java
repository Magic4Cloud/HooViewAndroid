package com.easyvaas.common.emoji.utils;

import java.util.ArrayList;

import com.easyvaas.common.emoji.bean.EmoticonSetBean;

public class EmoticonsKeyboardBuilder {

    public Builder builder;

    public EmoticonsKeyboardBuilder(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {

        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = new ArrayList<EmoticonSetBean>();

        public Builder() {
        }

        public ArrayList<EmoticonSetBean> getEmoticonSetBeanList() {
            return mEmoticonSetBeanList;
        }

        public Builder setEmoticonSetBeanList(ArrayList<EmoticonSetBean> mEmoticonSetBeanList) {
            this.mEmoticonSetBeanList = mEmoticonSetBeanList;
            return this;
        }

        public EmoticonsKeyboardBuilder build() {
            return new EmoticonsKeyboardBuilder(this);
        }
    }
}
