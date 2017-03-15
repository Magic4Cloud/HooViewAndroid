package com.easyvaas.common.gift.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.action.type.Type;
import com.easyvaas.common.gift.utils.GiftUtility;

public class ZipAnimAction extends Action {
    public static Map<Long, AnimType> typeNames = new HashMap<>();
    public static Map<AnimType, File> resMap = new HashMap<>();

    private File animResDir;
    private String giftName;

    public ZipAnimAction(GiftEntity entity, FromType fromType) throws FileNotFoundException {
        this.type = Type.ANIMATION;
        this.fromType = fromType;
        this.senderName = entity.getNickname();
        this.giftName = entity.getGiftName();
        this.senderID = entity.getName();

        if (typeNames.containsKey(entity.getGiftId())) {
            this.animType = typeNames.get(entity.getGiftId());
        } else {
            this.animType = AnimType.getType(getZipAnimationFlag((int) entity.getGiftId()));
            typeNames.put(entity.getGiftId(), animType);
        }

        if (resMap.containsKey(animType)) {
            this.animResDir = resMap.get(animType);
        } else {
            if (animType == AnimType.NONE) {
                resMap.put(animType, null);
                return;
            }
            this.animResDir = getZipAnimationResDir((int) entity.getGiftId());
            resMap.put(animType, animResDir);
        }
    }

    public File getAnimResDir() {
        return animResDir;
    }

    public String getGiftName() {
        return giftName;
    }

    private String getZipAnimationFlag(int giftId) throws FileNotFoundException {
        File dir = null;
        try {
            dir = getZipAnimationResDir(giftId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (dir == null) {
            return AnimType.NONE.type;
        }

        String animName = null;
        for (File jsonFile : dir.listFiles()) {
            if (jsonFile.isFile() && "animation.json".equals(jsonFile.getName())) {
                animName = GiftUtility.getAnimName(jsonFile.getAbsolutePath());
            }
        }
        return animName;
    }

    private File getZipAnimationResDir(int giftId) throws FileNotFoundException {
        File animationDir = null;
        for (File dir : new File(GiftUtility.getGiftIdDir(giftId)).listFiles()) {
            if (dir.isDirectory() && dir.getName().startsWith("Animation")) {
                animationDir = dir;
                break;
            }
        }
        if (animationDir == null) {
            throw new FileNotFoundException("动画目录未找到, 检查是否网络不畅没有加载成功");
        }
        if (animationDir.isFile()) {
            throw new FileNotFoundException("动画目录查找出错, 检查是否服务器返回数据有误");
        }
        return animationDir;
    }
}