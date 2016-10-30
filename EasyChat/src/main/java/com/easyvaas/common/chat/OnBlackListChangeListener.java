package com.easyvaas.common.chat;

public interface OnBlackListChangeListener {
    void onAddUserSuccess();

    void onAddFailed();

    void onRemoveUserSuccess();

    void onRemoveUserFailed();
}
