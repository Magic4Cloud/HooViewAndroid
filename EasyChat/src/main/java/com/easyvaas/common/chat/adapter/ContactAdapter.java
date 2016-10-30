package com.easyvaas.common.chat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.widget.MyUserPhoto;

public class ContactAdapter extends ArrayAdapter<ChatUser> implements SectionIndexer {
    private static final String TAG = ContactAdapter.class.getSimpleName();

    private List<ChatUser> userList;
    private List<ChatUser> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notifyByFilter;

    public ContactAdapter(Context context, int resource, List<ChatUser> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<ChatUser>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        MyUserPhoto avatar;
        TextView unreadMsgView;
        TextView nameTv;
        TextView tvHeader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatUser user = getItem(position);
        if (user == null) {
            return convertView;
        }
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(res, null);
            holder.avatar = (MyUserPhoto) convertView.findViewById(R.id.avatar);
            holder.unreadMsgView = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.nameTv = (TextView) convertView.findViewById(R.id.name);
            holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String username = user.getUsername();
        String header = user.getHeader();
        if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
            if (TextUtils.isEmpty(header)) {
                holder.tvHeader.setVisibility(View.GONE);
            } else {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {
            holder.tvHeader.setVisibility(View.GONE);
        }
        if (username.equals(ChatConstants.NEW_FRIENDS_USERNAME)) {
            holder.nameTv.setText(user.getNick());
            holder.avatar.getRoundImageView().setImageResource(R.drawable.new_friends_icon);
            if (user.getUnreadMsgCount() > 0) {
                holder.unreadMsgView.setVisibility(View.VISIBLE);
            } else {
                holder.unreadMsgView.setVisibility(View.INVISIBLE);
            }
        } else if (username.equals(ChatConstants.GROUP_USERNAME)) {
            holder.nameTv.setText(user.getNick());
            holder.avatar.getRoundImageView().setImageResource(R.drawable.somebody);
        } else if (username.equals(ChatConstants.CHAT_ROOM)) {
            holder.nameTv.setText(user.getNick());
            holder.avatar.getRoundImageView().setImageResource(R.drawable.somebody);
        } else if (username.equals(ChatConstants.CHAT_ROBOT)) {
            holder.nameTv.setText(user.getNick());
            holder.avatar.getRoundImageView().setImageResource(R.drawable.somebody);
        } else {
            holder.nameTv.setText(user.getNick());
            ChatUserUtil.setUserAvatar(getContext(), username, holder.avatar.getRoundImageView());
            if (holder.unreadMsgView != null) {
                holder.unreadMsgView.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    @Override
    public ChatUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            String letter = getItem(i).getHeader();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    private class MyFilter extends Filter {
        List<ChatUser> mOriginalList = null;

        public MyFilter(List<ChatUser> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<ChatUser>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<ChatUser> newValues = new ArrayList<ChatUser>();
                for (int i = 0; i < count; i++) {
                    final ChatUser user = mOriginalList.get(i);
                    String username = user.getNick();
                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        for (int k = 0, wordCount = words.length; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List<ChatUser>) results.values);
            ChatLogger.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notifyByFilter = true;
                notifyDataSetChanged();
                notifyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notifyByFilter) {
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }
}
