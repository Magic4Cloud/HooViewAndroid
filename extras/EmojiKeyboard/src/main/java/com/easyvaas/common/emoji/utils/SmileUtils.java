package com.easyvaas.common.emoji.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.easyvaas.common.emoji.R;

public class SmileUtils {
    public static final String emoji_1 = "[微笑]";
    public static final String emoji_2 = "[憋嘴]";
    public static final String emoji_3 = "[色]";
    public static final String emoji_4 = "[发呆]";
    public static final String emoji_5 = "[得意]";
    public static final String emoji_6 = "[流泪]";
    public static final String emoji_7 = "[害羞]";
    public static final String emoji_8 = "[闭嘴]";
    public static final String emoji_9 = "[睡]";
    public static final String emoji_10 = "[大哭]";
    public static final String emoji_11 = "[尴尬]";
    public static final String emoji_12 = "[发怒]";
    public static final String emoji_13 = "[调皮]";
    public static final String emoji_14 = "[呲牙]";
    public static final String emoji_15 = "[惊讶]";
    public static final String emoji_16 = "[难过]";
    public static final String emoji_17 = "[酷]";
    public static final String emoji_18 = "[冷汗]";
    public static final String emoji_19 = "[抓狂]";
    public static final String emoji_20 = "[吐]";
    public static final String emoji_21 = "[偷笑]";
    public static final String emoji_22 = "[愉快]";
    public static final String emoji_23 = "[白眼]";
    public static final String emoji_24 = "[傲慢]";
    public static final String emoji_25 = "[饥饿]";
    public static final String emoji_26 = "[困]";
    public static final String emoji_27 = "[惊恐]";
    public static final String emoji_28 = "[流汗]";
    public static final String emoji_29 = "[憨笑]";
    public static final String emoji_30 = "[悠闲]";
    public static final String emoji_31 = "[奋斗]";
    public static final String emoji_32 = "[咒骂]";
    public static final String emoji_33 = "[疑问]";
    public static final String emoji_34 = "[嘘]";
    public static final String emoji_35 = "[晕]";
    public static final String emoji_36 = "[疯了]";
    public static final String emoji_37 = "[衰]";
    public static final String emoji_38 = "[骷髅]";
    public static final String emoji_39 = "[敲打]";
    public static final String emoji_40 = "[再见]";
    public static final String emoji_41 = "[擦汗]";
    public static final String emoji_42 = "[抠鼻]";
    public static final String emoji_43 = "[鼓掌]";
    public static final String emoji_44 = "[糗大了]";
    public static final String emoji_45 = "[坏笑]";
    public static final String emoji_46 = "[左哼哼]";
    public static final String emoji_47 = "[右哼哼]";
    public static final String emoji_48 = "[打哈欠]";
    public static final String emoji_49 = "[鄙视]";
    public static final String emoji_50 = "[委屈]";
    public static final String emoji_51 = "[快哭了]";
    public static final String emoji_52 = "[阴险]";
    public static final String emoji_53 = "[亲亲]";
    public static final String emoji_54 = "[吓]";
    public static final String emoji_55 = "[可怜]";
    public static final String emoji_56 = "[菜刀]";
    public static final String emoji_57 = "[西瓜]";
    public static final String emoji_58 = "[啤酒]";
    public static final String emoji_59 = "[篮球]";
    public static final String emoji_60 = "[乒乓]";
    public static final String emoji_61 = "[咖啡]";
    public static final String emoji_62 = "[饭]";
    public static final String emoji_63 = "[猪头]";
    public static final String emoji_64 = "[玫瑰]";
    public static final String emoji_65 = "[凋谢]";
    public static final String emoji_66 = "[嘴唇]";
    public static final String emoji_67 = "[爱心]";
    public static final String emoji_68 = "[心碎]";
    public static final String emoji_69 = "[蛋糕]";
    public static final String emoji_70 = "[闪电]";
    public static final String emoji_71 = "[炸弹]";
    public static final String emoji_72 = "[刀]";
    public static final String emoji_73 = "[足球]";
    public static final String emoji_74 = "[瓢虫]";
    public static final String emoji_75 = "[便便]";
    public static final String emoji_76 = "[月亮]";
    public static final String emoji_77 = "[太阳]";
    public static final String emoji_78 = "[礼物]";
    public static final String emoji_79 = "[拥抱]";
    public static final String emoji_80 = "[强]";
    public static final String emoji_81 = "[弱]";
    public static final String emoji_82 = "[握手]";
    public static final String emoji_83 = "[胜利]";
    public static final String emoji_84 = "[抱拳]";
    public static final String emoji_85 = "[勾引]";
    public static final String emoji_86 = "[拳头]";
    public static final String emoji_87 = "[差劲]";
    public static final String emoji_88 = "[爱你]";
    public static final String emoji_89 = "[NO]";
    public static final String emoji_90 = "[OK]";
    public static final String emoji_91 = "[爱情]";
    public static final String emoji_92 = "[飞吻]";
    public static final String emoji_93 = "[跳跳]";
    public static final String emoji_94 = "[发抖]";
    public static final String emoji_95 = "[怄火]";
    public static final String emoji_96 = "[转圈]";
    public static final String emoji_97 = "[磕头]";
    public static final String emoji_98 = "[回头]";
    public static final String emoji_99 = "[跳绳]";
    public static final String emoji_100 = "[投降]";
    public static final String emoji_101 = "[激动]";
    public static final String emoji_102 = "[转转]";
    public static final String emoji_103 = "[加油]";
    public static final String emoji_104 = "[美眉]";
    public static final String emoji_105 = "[敬礼]";

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, emoji_1, R.drawable.emoji_1);
        addPattern(emoticons, emoji_2, R.drawable.emoji_2);
        addPattern(emoticons, emoji_3, R.drawable.emoji_3);
        addPattern(emoticons, emoji_4, R.drawable.emoji_4);
        addPattern(emoticons, emoji_5, R.drawable.emoji_5);
        addPattern(emoticons, emoji_6, R.drawable.emoji_6);
        addPattern(emoticons, emoji_7, R.drawable.emoji_7);
        addPattern(emoticons, emoji_8, R.drawable.emoji_8);
        addPattern(emoticons, emoji_9, R.drawable.emoji_9);
        addPattern(emoticons, emoji_10, R.drawable.emoji_10);
        addPattern(emoticons, emoji_11, R.drawable.emoji_11);
        addPattern(emoticons, emoji_12, R.drawable.emoji_12);
        addPattern(emoticons, emoji_13, R.drawable.emoji_13);
        addPattern(emoticons, emoji_14, R.drawable.emoji_14);
        addPattern(emoticons, emoji_15, R.drawable.emoji_15);
        addPattern(emoticons, emoji_16, R.drawable.emoji_16);
        addPattern(emoticons, emoji_17, R.drawable.emoji_17);
        addPattern(emoticons, emoji_18, R.drawable.emoji_18);
        addPattern(emoticons, emoji_19, R.drawable.emoji_19);
        addPattern(emoticons, emoji_20, R.drawable.emoji_20);
        addPattern(emoticons, emoji_21, R.drawable.emoji_21);
        addPattern(emoticons, emoji_22, R.drawable.emoji_22);
        addPattern(emoticons, emoji_23, R.drawable.emoji_23);
        addPattern(emoticons, emoji_24, R.drawable.emoji_24);
        addPattern(emoticons, emoji_25, R.drawable.emoji_25);
        addPattern(emoticons, emoji_26, R.drawable.emoji_26);
        addPattern(emoticons, emoji_27, R.drawable.emoji_27);
        addPattern(emoticons, emoji_28, R.drawable.emoji_28);
        addPattern(emoticons, emoji_29, R.drawable.emoji_29);
        addPattern(emoticons, emoji_30, R.drawable.emoji_30);
        addPattern(emoticons, emoji_31, R.drawable.emoji_31);
        addPattern(emoticons, emoji_32, R.drawable.emoji_32);
        addPattern(emoticons, emoji_33, R.drawable.emoji_33);
        addPattern(emoticons, emoji_34, R.drawable.emoji_34);
        addPattern(emoticons, emoji_35, R.drawable.emoji_35);
        addPattern(emoticons, emoji_36, R.drawable.emoji_36);
        addPattern(emoticons, emoji_37, R.drawable.emoji_37);
        addPattern(emoticons, emoji_38, R.drawable.emoji_38);
        addPattern(emoticons, emoji_39, R.drawable.emoji_39);
        addPattern(emoticons, emoji_40, R.drawable.emoji_40);
        addPattern(emoticons, emoji_41, R.drawable.emoji_41);
        addPattern(emoticons, emoji_42, R.drawable.emoji_42);
        addPattern(emoticons, emoji_43, R.drawable.emoji_43);
        addPattern(emoticons, emoji_44, R.drawable.emoji_44);
        addPattern(emoticons, emoji_45, R.drawable.emoji_45);
        addPattern(emoticons, emoji_46, R.drawable.emoji_46);
        addPattern(emoticons, emoji_47, R.drawable.emoji_47);
        addPattern(emoticons, emoji_48, R.drawable.emoji_48);
        addPattern(emoticons, emoji_49, R.drawable.emoji_49);
        addPattern(emoticons, emoji_50, R.drawable.emoji_50);
        addPattern(emoticons, emoji_51, R.drawable.emoji_51);
        addPattern(emoticons, emoji_52, R.drawable.emoji_52);
        addPattern(emoticons, emoji_53, R.drawable.emoji_53);
        addPattern(emoticons, emoji_54, R.drawable.emoji_54);
        addPattern(emoticons, emoji_55, R.drawable.emoji_55);
        addPattern(emoticons, emoji_56, R.drawable.emoji_56);
        addPattern(emoticons, emoji_57, R.drawable.emoji_57);
        addPattern(emoticons, emoji_58, R.drawable.emoji_58);
        addPattern(emoticons, emoji_59, R.drawable.emoji_59);
        addPattern(emoticons, emoji_60, R.drawable.emoji_60);
        addPattern(emoticons, emoji_61, R.drawable.emoji_61);
        addPattern(emoticons, emoji_62, R.drawable.emoji_62);
        addPattern(emoticons, emoji_63, R.drawable.emoji_63);
        addPattern(emoticons, emoji_64, R.drawable.emoji_64);
        addPattern(emoticons, emoji_65, R.drawable.emoji_65);
        addPattern(emoticons, emoji_66, R.drawable.emoji_66);
        addPattern(emoticons, emoji_67, R.drawable.emoji_67);
        addPattern(emoticons, emoji_68, R.drawable.emoji_68);
        addPattern(emoticons, emoji_69, R.drawable.emoji_69);
        addPattern(emoticons, emoji_70, R.drawable.emoji_70);
        addPattern(emoticons, emoji_71, R.drawable.emoji_71);
        addPattern(emoticons, emoji_72, R.drawable.emoji_72);
        addPattern(emoticons, emoji_73, R.drawable.emoji_73);
        addPattern(emoticons, emoji_74, R.drawable.emoji_74);
        addPattern(emoticons, emoji_75, R.drawable.emoji_75);
        addPattern(emoticons, emoji_76, R.drawable.emoji_76);
        addPattern(emoticons, emoji_77, R.drawable.emoji_77);
        addPattern(emoticons, emoji_78, R.drawable.emoji_78);
        addPattern(emoticons, emoji_79, R.drawable.emoji_79);
        addPattern(emoticons, emoji_80, R.drawable.emoji_80);
        addPattern(emoticons, emoji_81, R.drawable.emoji_81);
        addPattern(emoticons, emoji_82, R.drawable.emoji_82);
        addPattern(emoticons, emoji_83, R.drawable.emoji_83);
        addPattern(emoticons, emoji_84, R.drawable.emoji_84);
        addPattern(emoticons, emoji_85, R.drawable.emoji_85);
        addPattern(emoticons, emoji_86, R.drawable.emoji_86);
        addPattern(emoticons, emoji_87, R.drawable.emoji_87);
        addPattern(emoticons, emoji_88, R.drawable.emoji_88);
        addPattern(emoticons, emoji_89, R.drawable.emoji_89);
        addPattern(emoticons, emoji_90, R.drawable.emoji_90);
        addPattern(emoticons, emoji_91, R.drawable.emoji_91);
        addPattern(emoticons, emoji_92, R.drawable.emoji_92);
        addPattern(emoticons, emoji_93, R.drawable.emoji_93);
        addPattern(emoticons, emoji_94, R.drawable.emoji_94);
        addPattern(emoticons, emoji_95, R.drawable.emoji_95);
        addPattern(emoticons, emoji_96, R.drawable.emoji_96);
        addPattern(emoticons, emoji_97, R.drawable.emoji_97);
        addPattern(emoticons, emoji_98, R.drawable.emoji_97);
        addPattern(emoticons, emoji_99, R.drawable.emoji_99);
        addPattern(emoticons, emoji_100, R.drawable.emoji_100);
        addPattern(emoticons, emoji_101, R.drawable.emoji_101);
        addPattern(emoticons, emoji_102, R.drawable.emoji_102);
        addPattern(emoticons, emoji_103, R.drawable.emoji_103);
        addPattern(emoticons, emoji_104, R.drawable.emoji_104);
        addPattern(emoticons, emoji_105, R.drawable.emoji_105);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile, int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    /**
     * replace existing spannable with smiles
     */
    private static boolean addSmiles(Context context, Spannable spannable, int drawableSize) {
        boolean hasChanges = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end()) {
                        spannable.removeSpan(span);
                    } else {
                        set = false;
                        break;
                    }
                }
                if (set) {
                    hasChanges = true;
                    ImageSpan imageSpan = null;
                    if (drawableSize > 0) {
                        Bitmap bitmap = decodeBitmap(context, entry.getValue(), drawableSize);
                        imageSpan = new ImageSpan(context, bitmap, ImageSpan.ALIGN_BOTTOM);
                    } else {
                        imageSpan = new ImageSpan(context, entry.getValue(), ImageSpan.ALIGN_BOTTOM);
                    }
                    spannable.setSpan(imageSpan, matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text, int emojiSize) {
        Spannable spannable = Factory.getInstance().newSpannable(text);
        addSmiles(context, spannable, emojiSize);
        return spannable;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        return getSmiledText(context, text, 0);
    }

    public static boolean containsKey(String key) {
        boolean b = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }

        return b;
    }

    private static Bitmap decodeBitmap(Context context, int resId, int size) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        return Bitmap.createScaledBitmap(bmp, size, size, true);
    }
}
