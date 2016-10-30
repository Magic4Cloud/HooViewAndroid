package com.easyvaas.common.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easyvaas.common.chat.applib.controller.HXSDKHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_USER_ID + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INVITE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessageDao.TABLE_NAME + " ("
            + InviteMessageDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessageDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_IS_INVITE_FROM_ME + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_TIME + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private static final String RED_PACK_TABLE_CREATE = "CREATE TABLE "
            + RedPackDao.TABLE_NAME + " ("
            + RedPackDao.COLUMN_NAME_CODE + " TEXT PRIMARY KEY, "
            + RedPackDao.COLUMN_NAME_NAME + " TEXT, "
            + RedPackDao.COLUMN_NAME_LOGO + " TEXT, "
            + RedPackDao.COLUMN_NAME_VALUE + " INTEGER, "
            + RedPackDao.COLUMN_NAME_COUNT + " INTEGER, "
            + RedPackDao.COLUMN_NAME_OPENED + " INTEGER DEFAULT 0, "
            + RedPackDao.COLUMN_NAME_MSG_ID + " TEXT, "
            + RedPackDao.COLUMN_NAME_FROM + " TEXT) ";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return HXSDKHelper.getInstance().getHXId() + "_elapp.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INVITE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);
        db.execSQL(RED_PACK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        } else if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_USER_ID + " TEXT ;");
        }

        if (oldVersion < 3) {
            db.execSQL(CREATE_PREF_TABLE);
        }
        if (oldVersion < 4) {
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if (oldVersion < 6) {
            db.execSQL(RED_PACK_TABLE_CREATE);
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
