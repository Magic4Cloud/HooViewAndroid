package com.easyvaas.elapp.bean.imageTextLive;


import java.io.Serializable;

public class CheckImageTextLiveModel implements Serializable {

    private ImageTextLiveRoomModel data;

    public ImageTextLiveRoomModel getData() {
        return data;
    }

    public void setData(ImageTextLiveRoomModel data) {
        this.data = data;
    }

}
