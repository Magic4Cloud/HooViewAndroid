package com.easyvaas.common.chat.db;

public class RedPackDao {
    public static final String TABLE_NAME = "redpack";

    public static final String COLUMN_NAME_MSG_ID = "msg_id";
    public static final String COLUMN_NAME_CODE = "code";
    public static final String COLUMN_NAME_VALUE = "value";
    public static final String COLUMN_NAME_COUNT = "count";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_LOGO = "logo";
    public static final String COLUMN_NAME_FROM = "sender";
    public static final String COLUMN_NAME_OPENED = "opened";

    public static final int VALUE_IS_UNOPENED = 0;
    public static final int VALUE_IS_OPENED = 1;
}
