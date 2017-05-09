package com.easyvaas.elapp.db;


import android.text.TextUtils;

import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.bean.user.Collection;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.chat.model.ChatRecord;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmHelper {

    public static final String DB_NAME = "hootview.realm";
    private Realm mRealm;
    private static RealmHelper instance;

    private RealmHelper() {
    }

    public static RealmHelper getInstance() {
        if (instance == null) {
            synchronized (RealmHelper.class) {
                if (instance == null)
                    instance = new RealmHelper();
            }
        }
        return instance;
    }


    public Realm getRealm() {
        if (mRealm == null || mRealm.isClosed())
            mRealm = Realm.getDefaultInstance();
        return mRealm;
    }
    //--------------------------------------------------收藏相关----------------------------------------------------

    /**
     * 增加 收藏记录
     *
     * @param bean
     */
    public void insertCollection(Collection bean) {
        getRealm().beginTransaction();
        getRealm().copyToRealm(bean);
        getRealm().commitTransaction();
    }

    /**
     * 删除 收藏记录
     *
     * @param id
     */
    public void deleteCollection(String id) {
        Collection data = getRealm().where(Collection.class).equalTo("id", id).findFirst();
        getRealm().beginTransaction();
        data.deleteFromRealm();
        getRealm().commitTransaction();
    }

    /**
     * 清空收藏
     */
    public void deleteAllCollection() {
        getRealm().beginTransaction();
        getRealm().delete(Collection.class);
        getRealm().commitTransaction();
    }

    /**
     * 查询 收藏记录
     *
     * @param id
     * @return
     */
    public boolean queryCollectionId(String id) {
        RealmResults<Collection> results = getRealm().where(Collection.class).findAll();
        for (Collection item : results) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 收藏列表
     *
     * @return
     */
    public List<Collection> getCollectionList() {
        //使用findAllSort ,先findAll再result.sort排序
        RealmResults<Collection> results = getRealm().where(Collection.class).findAllSorted("time", Sort.DESCENDING);
        return getRealm().copyFromRealm(results);
    }


    //--------------------------------------------------观看记录相关----------------------------------------------------

    /**
     * 增加播放记录
     *
     * @param bean
     * @param maxSize 保存最大数量
     */
    public void insertRecord(Record bean, int maxSize) {
        if (maxSize != 0) {
            RealmResults<Record> results = getRealm().where(Record.class).findAllSorted("time", Sort.DESCENDING);
            if (results.size() >= maxSize) {
                for (int i = maxSize - 1; i < results.size(); i++) {
                    deleteRecord(results.get(i).getId());
                }
            }
        }
        getRealm().beginTransaction();
        getRealm().copyToRealm(bean);
        getRealm().commitTransaction();
    }


    /**
     * 删除 播放记录
     *
     * @param id
     */
    public void deleteRecord(String id) {
        Record data = getRealm().where(Record.class).equalTo("id", id).findFirst();
        getRealm().beginTransaction();
        data.deleteFromRealm();
        getRealm().commitTransaction();
    }

    /**
     * 查询 播放记录
     *
     * @param id
     * @return
     */
    public boolean queryRecordId(String id) {
        RealmResults<Record> results = getRealm().where(Record.class).findAll();
        for (Record item : results) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public List<Record> getRecordList() {
        //使用findAllSort ,先findAll再result.sort排序
        RealmResults<Record> results = getRealm().where(Record.class).findAllSorted("time", Sort.DESCENDING);
        return getRealm().copyFromRealm(results);
    }

    /**
     * 清空历史
     */
    public void deleteAllRecord() {
        getRealm().beginTransaction();
        getRealm().delete(Record.class);
        getRealm().commitTransaction();
    }
    //--------------------------------------------------阅读记录相关----------------------------------------------------

    /**
     * 增加播放记录
     *
     * @param bean
     * @param maxSize 保存最大数量
     */
    public void insertReadRecord(ReadRecord bean, int maxSize) {
        if (maxSize != 0) {
            RealmResults<ReadRecord> results = getRealm().where(ReadRecord.class).findAllSorted("time", Sort.DESCENDING);
            if (results.size() >= maxSize) {
                for (int i = maxSize - 1; i < results.size(); i++) {
                    deleteRecord(results.get(i).getId());
                }
            }
        }
        getRealm().beginTransaction();
        getRealm().copyToRealm(bean);
        getRealm().commitTransaction();
    }


    /**
     * 删除 播放记录
     *
     * @param id
     */
    public void deleteReadRecord(String id) {
        ReadRecord data = getRealm().where(ReadRecord.class).equalTo("id", id).findFirst();
        getRealm().beginTransaction();
        data.deleteFromRealm();
        getRealm().commitTransaction();
    }

    /**
     * 查询 播放记录
     *
     * @param id
     * @return
     */
    public boolean queryReadRecordId(String id) {
        RealmResults<ReadRecord> results = getRealm().where(ReadRecord.class).findAll();
        for (ReadRecord item : results) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public List<ReadRecord> getReadRecordList() {
        //使用findAllSort ,先findAll再result.sort排序
        RealmResults<ReadRecord> results = getRealm().where(ReadRecord.class).findAllSorted("time", Sort.DESCENDING);
        return getRealm().copyFromRealm(results);
    }

    /**
     * 清空历史
     */
    public void deleteReadAllRecord() {
        getRealm().beginTransaction();
        getRealm().delete(ReadRecord.class);
        getRealm().commitTransaction();
    }
    //--------------------------------------------------全球记录相关----------------------------------------------------

    /**
     * 增加播放记录
     *
     * @param bean
     */
    public void insertGlobalRecord(ExponentModel bean) {
        getRealm().beginTransaction();
        getRealm().copyToRealm(bean);
        getRealm().commitTransaction();
    }


    /**
     * 删除 播放记录
     *
     * @param id
     */
    public void deleteGlobalRecord(String id) {
        ExponentModel data = getRealm().where(ExponentModel.class).equalTo("id", id).findFirst();
        getRealm().beginTransaction();
        data.deleteFromRealm();
        getRealm().commitTransaction();
    }

    /**
     * 查询 播放记录
     *
     * @param id
     * @return
     */
    public boolean queryGlobalRecordId(String id) {
        RealmResults<ExponentModel> results = getRealm().where(ExponentModel.class).findAll();
        for (ExponentModel item : results) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public List<ExponentModel> getGlobalRecordList() {
        //使用findAllSort ,先findAll再result.sort排序
        RealmResults<ExponentModel> results = getRealm().where(ExponentModel.class).findAllSorted("time", Sort.DESCENDING);
        return getRealm().copyFromRealm(results);
    }

    /**
     * 清空历史
     */
    public void deleteGlobalAllRecord() {
        getRealm().beginTransaction();
        getRealm().delete(ExponentModel.class);
        getRealm().commitTransaction();
    }

    /**
     * 增加聊天记录
     *
     * @param bean
     */
    public void insertChatRecord(ChatRecord bean) {
        getRealm().beginTransaction();
        getRealm().copyToRealm(bean);
        getRealm().commitTransaction();
    }

    /**
     * 查询聊天记录
     *
     * @param id
     * @return
     */
    public ChatRecord queryChatRecord(String id) {
        RealmResults<ChatRecord> results = getRealm().where(ChatRecord.class).findAll();
        for (ChatRecord item : results) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * 删除聊天记录
     *
     * @param id
     */
    public void deleteChatRecord(String id) {
        ChatRecord data = getRealm().where(ChatRecord.class).equalTo("id", id).findFirst();
        getRealm().beginTransaction();
        data.deleteFromRealm();
        getRealm().commitTransaction();
    }

    public void insertChatRecordSingle(ChatRecord bean) {
        if (bean == null || TextUtils.isEmpty(bean.getId())) {
            return;
        }
        ChatRecord record = queryChatRecord(bean.getId());
        if (record == null) {
            insertChatRecord(bean);
        } else {
            if ((bean.getAvatar() != null && !bean.getAvatar().equals(record.getAvatar()))
            || (bean.getNickname() != null && !bean.getNickname().equals(record.getNickname()))) {
                deleteChatRecord(bean.getId());
                insertChatRecord(bean);
            }
        }
    }

    public String getChatRecordAvatar(String id) {
        RealmResults<ChatRecord> results = getRealm().where(ChatRecord.class).findAll();
        for (ChatRecord item : results) {
            if (item != null) {
                return item.getAvatar();
            }
        }
        return "";
    }

}
