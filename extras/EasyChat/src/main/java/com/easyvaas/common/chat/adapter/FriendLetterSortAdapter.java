package com.easyvaas.common.chat.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.bean.UserEntity;
import com.easyvaas.common.chat.adapter.item.AdapterFriendLetterItem;
import com.easyvaas.common.chat.utils.CharacterParser;

public class FriendLetterSortAdapter extends CommonRcvAdapter implements SectionIndexer, Filterable {
    private List<UserEntity> mListAll;
    private List<UserEntity> mListAllCopy;
    private boolean mIsChoiceList;
    private int mListSelectType;

    public FriendLetterSortAdapter(Context context, List data) {
        super(data);
        mListAll = data;
        mListAllCopy = new ArrayList<>();
    }

    @NonNull @Override
    public AdapterItem getItemView(Object type) {
        AdapterFriendLetterItem adapterFriendLetterItem = new AdapterFriendLetterItem();
        adapterFriendLetterItem.setChoice(mIsChoiceList);
        adapterFriendLetterItem.setListSelectType(mListSelectType);
        adapterFriendLetterItem.setSectionIndexer(this);
        return adapterFriendLetterItem;
    }

    public void setListSelectType(int listSelectType) {
        mListSelectType = listSelectType;
    }

    public void setChoiceList(boolean isChoiceList) {
        this.mIsChoiceList = isChoiceList;
    }

    public void assembleData() {
        CharacterParser characterParser = CharacterParser.getInstance();
        List<UserEntity> specialLetterList = new ArrayList<UserEntity>();
        for (int i = 0; i < mListAll.size(); i++) {
            UserEntity friendsEntity = mListAll.get(i);
            String pinyin = characterParser.getSelling(friendsEntity.getNickname());
            friendsEntity.setPinyin(pinyin);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                friendsEntity.setSortLetter(sortString.toUpperCase());
            } else {
                friendsEntity.setSortLetter("#");
                specialLetterList.add(mListAll.get(i));
                mListAll.remove(i);
                i = i - 1;
            }
        }
        for (int i = 0, n = specialLetterList.size(); i < n; i++) {
            mListAll.add(i, specialLetterList.get(i));
        }
        Collections.sort(mListAll, new Comparator<UserEntity>() {
            public int compare(UserEntity o1, UserEntity o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                if ("@".equals(o1.getSortLetter()) || "#".equals(o2.getSortLetter())) {
                    return 1;
                } else if ("#".equals(o1.getSortLetter()) || "@".equals(o2.getSortLetter())) {
                    return -1;
                } else if (o1.getSortLetter() == null) {
                    return 0;
                }
                return o1.getSortLetter().compareTo(o2.getSortLetter());
            }
        });
        mListAllCopy.addAll(mListAll);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                ArrayList<UserEntity> filterArrayName = new ArrayList<UserEntity>();

                String keyword = charSequence.toString();
                if (TextUtils.isEmpty(keyword)) {
                    filterResults.count = mListAllCopy.size();
                    filterResults.values = mListAllCopy;
                } else {
                    for (int i = 0, n = mListAllCopy.size(); i < n; i++) {
                        if (mListAllCopy.get(i).getNickname().startsWith(keyword)) {
                            filterArrayName.add(mListAllCopy.get(i));
                        }
                    }
                    filterResults.count = filterArrayName.size();
                    filterResults.values = filterArrayName;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListAll.clear();
                mListAll.addAll((List<UserEntity>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * get Char ascii value
     */
    public int getSectionForPosition(int position) {
        return mListAll.get(position).getSortLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < mListAll.size(); i++) {
            String sortStr = mListAll.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     */
    private String getAlpha(String letter) {
        String sortStr = letter.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
