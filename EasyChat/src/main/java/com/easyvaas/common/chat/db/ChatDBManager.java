package com.easyvaas.common.chat.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.easemob.chat.EMMessage;
import com.easemob.util.HanziToPinyin;

import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.bean.InviteMessage;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.bean.ChatRedPackEntity;

public class ChatDBManager {
    public static final String TAG = ChatDBManager.class.getSimpleName();

    private static ChatDBManager dbMgr = new ChatDBManager();

    private DbOpenHelper dbHelper;

    void onInit(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    public static synchronized ChatDBManager getInstance() {
        return dbMgr;
    }

    synchronized public String getAvatar(String imUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String avatar = "";
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME
                    + " where " + UserDao.COLUMN_NAME_ID
                    + " like '" + imUser + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
            }
            cursor.close();
        }
        return avatar;
    }

    synchronized public String getNickname(String imUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nickname = imUser;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME
                    + " where " + UserDao.COLUMN_NAME_ID
                    + " like '" + imUser + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                nickname = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
            }
            cursor.close();
        }
        return nickname;
    }

    synchronized public String getUserId(String imUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = "";
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME
                    + " where " + UserDao.COLUMN_NAME_ID
                    + " like '" + imUser + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USER_ID));
            }
            cursor.close();
        }
        return userId;
    }

    /**
     * 保存好友list
     */
    synchronized public void saveContactList(List<ChatUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (ChatUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null) {
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                }
                if (user.getAvatar() != null) {
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                }
                if (user.getUserId() != null) {
                    values.put(UserDao.COLUMN_NAME_USER_ID, user.getUserId());
                }
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取好友list
     * @param context
     */

    synchronized public Map<String, ChatUser> getContactList(Context context) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, ChatUser> users = new HashMap<String, ChatUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            if (cursor.getCount() == 0) {
                return users;
            }
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String userId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_USER_ID));
                ChatUser user = new ChatUser();
                user.setUsername(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                user.setUserId(userId);

                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = ChatUserUtil.getNickName(user.getUsername(), context);
                }
                if (username.equals(ChatConstants.NEW_FRIENDS_USERNAME)
                        || username.equals(ChatConstants.GROUP_USERNAME)
                        || username.equals(ChatConstants.CHAT_ROOM)
                        || username.equals(ChatConstants.CHAT_ROBOT)) {
                    user.setHeader("");
                } else if (Character.isDigit(headerName.charAt(0))) {
                    user.setHeader("#");
                } else if (TextUtils.isEmpty(headerName)) {
                    user.setHeader("");
                } else {
                    ArrayList<HanziToPinyin.Token> list = HanziToPinyin.getInstance()
                            .get(headerName.substring(0, 1));
                    if (list.size() > 0) {
                        user.setHeader(list.get(0).target.substring(0, 1).toUpperCase());
                        char header = user.getHeader().toLowerCase().charAt(0);
                        if (header < 'a' || header > 'z') {
                            user.setHeader("#");
                        }
                    } else {
                        ChatLogger.w(TAG, "parse user name header failed !");
                        user.setHeader("");
                    }
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除一个联系人
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[] { username });
        }
    }

    /**
     * 保存一个联系人
     */
    synchronized public void saveContact(ChatUser user) {
        if (user == null || TextUtils.isEmpty(user.getUsername())) {
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null) {
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        }
        if (user.getAvatar() != null) {
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (user.getUserId() != null) {
            values.put(UserDao.COLUMN_NAME_USER_ID, user.getUserId());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array.length > 0) {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, array);
            return list;
        }

        return null;
    }

    /**
     * 保存message
     *
     * @return 返回这条messaged在db中的id
     */
    public synchronized Integer saveMessage(InviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessageDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessageDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            db.insert(InviteMessageDao.TABLE_NAME, null, values);

            Cursor cursor = db
                    .rawQuery("select last_insert_rowid() from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * 更新message
     */
    synchronized public void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessageDao.TABLE_NAME, values, InviteMessageDao.COLUMN_NAME_ID + " = ?",
                    new String[] {
                            String.valueOf(msgId)
                    });
        }
    }

    /**
     * 获取messges
     */
    synchronized public List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext()) {
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                String groupid = cursor
                        .getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor
                        .getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                if (status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
                else if (status == InviteMessage.InviteMesageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
                else if (status == InviteMessage.InviteMesageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
                else if (status == InviteMessage.InviteMesageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                else if (status == InviteMessage.InviteMesageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
                else if (status == InviteMessage.InviteMesageStatus.BEAPPLYED.ordinal()) {
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
                }
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_FROM + " = ?",
                    new String[] { from });
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }

    synchronized public Integer saveRedPack(EMMessage message) {
        return saveRedPack(CommonUtils.getRedPackEntity(message));
    }

    synchronized public Integer saveRedPack(ChatRedPackEntity redPackEntity) {
        if (redPackEntity == null) {
            return -1;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(RedPackDao.COLUMN_NAME_MSG_ID, redPackEntity.getMsgId());
            values.put(RedPackDao.COLUMN_NAME_FROM, redPackEntity.getFrom());
            values.put(RedPackDao.COLUMN_NAME_CODE, redPackEntity.getCode());
            values.put(RedPackDao.COLUMN_NAME_COUNT, redPackEntity.getCount());
            values.put(RedPackDao.COLUMN_NAME_VALUE, redPackEntity.getValue());
            values.put(RedPackDao.COLUMN_NAME_NAME, redPackEntity.getName());
            values.put(RedPackDao.COLUMN_NAME_LOGO, redPackEntity.getLogo());

            int flag = (int) db.insertWithOnConflict(RedPackDao.TABLE_NAME, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
            if (flag == -1) {
                db.update(RedPackDao.TABLE_NAME, values, RedPackDao.COLUMN_NAME_CODE + " = ?",
                        new String[] { redPackEntity.getCode() });
            }

            Cursor cursor = db
                    .rawQuery("select last_insert_rowid() from " + RedPackDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    synchronized public void updateRedPack(String code, boolean isOpened) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RedPackDao.COLUMN_NAME_OPENED, isOpened ? RedPackDao.VALUE_IS_OPENED
                : RedPackDao.VALUE_IS_UNOPENED);
        if (db.isOpen()) {
            db.update(RedPackDao.TABLE_NAME, values, RedPackDao.COLUMN_NAME_CODE + " = ?",
                    new String[] { code });
        }
    }

    synchronized public ChatRedPackEntity getRedPack(EMMessage message) {
        String code = CommonUtils.getRedPackCode(message);
        ChatRedPackEntity entity = getRedPack(code);
        if (!TextUtils.isEmpty(code) && entity == null) {
            entity = CommonUtils.getRedPackEntity(message);
            saveRedPack(entity);
        }
        return entity;
    }

    synchronized public ChatRedPackEntity getRedPack(String code) {
        if (TextUtils.isEmpty(code)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ChatRedPackEntity entity = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + RedPackDao.TABLE_NAME
                            + " where " + RedPackDao.COLUMN_NAME_CODE + " = ?",
                    new String[] { code }
            );
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                entity = new ChatRedPackEntity();
                entity.setMsgId(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_MSG_ID)));
                entity.setFrom(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_FROM)));
                entity.setCode(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_CODE)));
                entity.setName(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_NAME)));
                entity.setCount(cursor.getInt(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_COUNT)));
                entity.setValue(cursor.getInt(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_VALUE)));
                entity.setLogo(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_LOGO)));
                entity.setCode(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_CODE)));
                entity.setCode(cursor.getString(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_CODE)));
                entity.setOpened(cursor.getInt(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_OPENED))
                        == RedPackDao.VALUE_IS_OPENED);
            }
            cursor.close();
        }
        return entity;
    }

    synchronized public boolean isOpenedRedPack(String code) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + RedPackDao.COLUMN_NAME_OPENED + " from "
                            + RedPackDao.TABLE_NAME + " where " + RedPackDao.COLUMN_NAME_CODE + " = ?",
                    new String[] { code }
            );
            if (cursor.getCount() > 0) {
                return cursor.getInt(cursor.getColumnIndex(RedPackDao.COLUMN_NAME_OPENED))
                        == RedPackDao.VALUE_IS_OPENED;
            }
            cursor.close();
        }
        return false;
    }
}
