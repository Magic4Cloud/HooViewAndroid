/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easyvaas.common.widget.stickylistview.StickyListHeadersAdapter;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.CountryCodeEntity;

public class CountryCodeAdapter extends BaseAdapter
        implements StickyListHeadersAdapter, SectionIndexer, Filterable {
    private List<CountryCodeEntity> mListAll = new ArrayList<CountryCodeEntity>();
    private List<CountryCodeEntity> mList = null;
    private Context mContext;

    public CountryCodeAdapter(Context mContext, List<CountryCodeEntity> list) {
        this.mContext = mContext;
        this.mList = list;
        mListAll.clear();
        this.mListAll.addAll(list);
    }

    public void updateListView(List<CountryCodeEntity> list) {
        this.mList = list;
        this.mListAll = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        final CountryCodeEntity mContent = mList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_city_letter_sort, viewGroup, false);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvCode = (TextView) view.findViewById(R.id.name_code_txv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvTitle.setText(mContent.getName());
        viewHolder.tvCode.setText(mContent.getCode());
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                ArrayList<CountryCodeEntity> filterArrayName = new ArrayList<CountryCodeEntity>();

                String search = charSequence.toString().toLowerCase();
                for (int i = 0, n = mListAll.size(); i < n; i++) {
                    String name = mListAll.get(i).getPinyin();
                    if (name.toLowerCase().startsWith(search)) {
                        filterArrayName.add(mListAll.get(i));
                    }
                }

                filterResults.count = filterArrayName.size();
                filterResults.values = filterArrayName;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mList.clear();
                mList.addAll((List<CountryCodeEntity>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    final static class HeaderViewHolder {
        TextView tvLetter;
    }

    final static class ViewHolder {
        TextView tvTitle;
        TextView tvCode;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        HeaderViewHolder hViewHolder;
        if (view == null) {
            hViewHolder = new HeaderViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_sticky_header, viewGroup, false);
            hViewHolder.tvLetter = (TextView) view.findViewById(R.id.sticky_header_letter_tv);
            view.setTag(hViewHolder);
        } else {
            hViewHolder = (HeaderViewHolder) view.getTag();
        }
        hViewHolder.tvLetter.setText(mList.get(position).getSortLetter());
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return mList.get(position).getSortLetter().subSequence(0, 1).charAt(0);
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
