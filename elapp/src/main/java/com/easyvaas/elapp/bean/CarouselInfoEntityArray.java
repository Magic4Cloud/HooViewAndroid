/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean;

import java.util.List;

public class CarouselInfoEntityArray {

    private int count;
    private List<CarouselInfoEntity> objects;

    public void setCount(int count) {
        this.count = count;
    }

    public void setObjects(List<CarouselInfoEntity> objects) {
        this.objects = objects;
    }

    public int getCount() {
        return count;
    }

    public List<CarouselInfoEntity> getObjects() {
        return objects;
    }
}
