/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.utils;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.widget.FullScreenDialogHUD;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.activity.setting.BindPhoneActivity;
import com.hooview.app.adapter.RedPackUserAdapter;
import com.hooview.app.bean.RedPackEntity;
import com.hooview.app.bean.TopicEntity;
import com.hooview.app.bean.chat.ChatRedPackInfo;
import com.hooview.app.bean.chat.ChatRedPackUserEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;

public class DialogUtil {

    public static final int TYPE_RED_PACK_RECEIVE = 0;
    public static final int TYPE_RED_PACK_RESULT_0 = 1;
    public static final int TYPE_RED_PACK_RESULT_1 = 2;

    public static Dialog getProcessDialog(Activity activity, CharSequence message,
            boolean dismissTouchOutside, boolean cancelable) {
        final LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(com.hooview.app.R.layout.progress_hud, null);
        Dialog dialog = getCustomDialog(activity, v, dismissTouchOutside, cancelable, -1);
        if (dismissTouchOutside) {
            dialog.setCanceledOnTouchOutside(true);
        } else {
            dialog.setCanceledOnTouchOutside(false);
        }

        ImageView spinner = (ImageView) v.findViewById(com.hooview.app.R.id.spinnerImageView);
        ((AnimationDrawable) spinner.getBackground()).start();
        TextView messageTv = (TextView) v.findViewById(com.hooview.app.R.id.message);
        if (TextUtils.isEmpty(message)) {
            messageTv.setVisibility(View.GONE);
        } else {
            messageTv.setText(message);
            messageTv.setVisibility(View.VISIBLE);
        }

        return dialog;
    }

    public static Dialog getCustomDialog(final Activity activity, View view, boolean dismissTouchOutside,
            boolean cancelable, int theme) {
        Dialog dialog = theme > 0 ? new FullScreenDialogHUD(activity, theme)
                : new Dialog(activity, com.hooview.app.R.style.Dialog_FullScreen);
        //        Dialog dialog = new Dialog(activity, R.style.Dialog_FullScreen);
        dialog.setContentView(view);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        if (!cancelable) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        activity.finish();
                    }
                    return false;
                }
            });
        }
        return dialog;
    }

    public static Dialog getOneButtonDialog(final Activity activity, int resId,
            boolean dismissTouchOutside, boolean cancelable,
            DialogInterface.OnClickListener confirmOnClickListener) {
        return getOneButtonDialog(activity, activity.getString(resId), dismissTouchOutside, cancelable,
                confirmOnClickListener);
    }

    public static Dialog getOneButtonDialog(final Activity activity, String content,
            boolean dismissTouchOutside, boolean cancelable,
            DialogInterface.OnClickListener confirmOnClickListener) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setPositiveButton(com.hooview.app.R.string.confirm, confirmOnClickListener)
                .setCancelable(cancelable)
                .setMessage(content)
                .create();
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        if (!cancelable) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        activity.finish();
                    }
                    return false;
                }
            });
        }
        return dialog;
    }

    public static Dialog getButtonsDialog(Activity activity, String content,
            DialogInterface.OnClickListener confirmOnClickListener) {
        return getButtonsDialog(activity, content, true, true, confirmOnClickListener, null);
    }

    public static Dialog getButtonsDialog(Activity activity, String content, boolean dismissTouchOutside,
            boolean cancelable, DialogInterface.OnClickListener confirmOnClickListener,
            DialogInterface.OnClickListener cancelOnClickListener) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setNegativeButton(com.hooview.app.R.string.cancel, cancelOnClickListener)
                .setPositiveButton(com.hooview.app.R.string.confirm, confirmOnClickListener)
                .setCancelable(cancelable)
                .setMessage(content)
                .create();
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        return dialog;
    }

    public static Dialog getRedPackDialog(final Activity activity, final ChatRedPackInfo chatRedPackInfo,
            final String vid, final OpenRedPackListener listener) {
        final View rootView = LayoutInflater.from(activity).inflate(com.hooview.app.R.layout.live_red_pack, null);
        final Dialog dialog = getCustomDialog(activity, rootView, false, true, -1);
        ImageView closeIv = (ImageView) rootView.findViewById(com.hooview.app.R.id.close_iv);
        final ImageView pickRedPackIv = (ImageView) rootView.findViewById(com.hooview.app.R.id.pick_red_pack_iv);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextView moreInfoTv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_more_info_tv);

        moreInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assembleRedPackView(activity, chatRedPackInfo, TYPE_RED_PACK_RESULT_1, rootView);
            }
        });
        // TODO Only for test, need to remove when release.
        rootView.findViewById(com.hooview.app.R.id.red_pack_logo_1_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assembleRedPackView(activity, chatRedPackInfo, TYPE_RED_PACK_RESULT_1, rootView);
            }
        });
        rootView.findViewById(com.hooview.app.R.id.red_pack_rank_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assembleRedPackView(activity, chatRedPackInfo, TYPE_RED_PACK_RESULT_0, rootView);
            }
        });

        int type = chatRedPackInfo.isNewRedPack() ? TYPE_RED_PACK_RECEIVE : TYPE_RED_PACK_RESULT_0;
        if (type == TYPE_RED_PACK_RECEIVE) {
            pickRedPackIv.setVisibility(View.VISIBLE);
            pickRedPackIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.setEnabled(false);
                    rootView.setClickable(false);
                    listener.onOpen();
                    ApiHelper.getInstance().openRedPack(chatRedPackInfo.getId(),
                            new MyRequestCallBack<RedPackEntity>() {
                                @Override
                                public void onSuccess(RedPackEntity result) {
                                    rootView.setEnabled(true);
                                    rootView.setClickable(true);
                                    rootView.setOnClickListener(null);
                                    int gotECoin = 0;
                                    String currentUser = Preferences.getInstance(activity).getUserNumber();
                                    for (ChatRedPackUserEntity user : result.getUsers()) {
                                        if (currentUser.equals(user.getName())) {
                                            gotECoin = user.getEcoin();
                                        }
                                    }
                                    chatRedPackInfo.setCurrentUserAmount(gotECoin);
                                    chatRedPackInfo.setNewRedPack(false);
                                    pickRedPackIv.setVisibility(View.GONE);
                                    if (gotECoin > 0) {
                                        chatRedPackInfo.getUsers().clear();
                                        chatRedPackInfo.getUsers().addAll(result.getUsers());
                                        assembleRedPackView(activity, chatRedPackInfo,
                                                TYPE_RED_PACK_RESULT_1, rootView);
                                        chatRedPackInfo.setGetRedPack(true);
                                        Preferences pref = Preferences.getInstance(activity);
                                        long eCount = pref.getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT,
                                                0);
                                        pref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT,
                                                eCount + gotECoin);
                                        GiftManager.setECoinCount(activity, eCount + gotECoin);
                                    } else {
                                        assembleRedPackView(activity, chatRedPackInfo,
                                                TYPE_RED_PACK_RESULT_0, rootView);
                                        chatRedPackInfo.setGetRedPack(false);
                                    }
                                    listener.onOpenSuccess();
                                }

                                @Override
                                public void onError(String errorInfo) {
                                    super.onError(errorInfo);
                                    listener.onOpenFailed();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(String msg) {
                                    listener.onOpenFailed();
                                    dialog.dismiss();
                                }
                            });
                }
            });
        }
        assembleRedPackView(activity, chatRedPackInfo, type, rootView);

        return dialog;
    }

    private static void assembleRedPackView(Activity activity, ChatRedPackInfo chatRedPackInfo,
            int type, View rootView) {
        final ImageView bgIv = (ImageView) rootView.findViewById(com.hooview.app.R.id.red_pack_bg_iv);

        final View view1 = rootView.findViewById(com.hooview.app.R.id.red_pack_receive_rl);
        MyUserPhoto redPackLogo = (MyUserPhoto) rootView.findViewById(com.hooview.app.R.id.red_pack_logo_iv);
        TextView titleTv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_title_tv);
        final TextView infoTv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_info_tv);
        final TextView moreInfoTv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_more_info_tv);

        final View view2 = rootView.findViewById(com.hooview.app.R.id.red_pack_history_rl);
        final MyUserPhoto redPackLogo1 = (MyUserPhoto) rootView.findViewById(com.hooview.app.R.id.red_pack_logo_1_iv);
        final TextView title1Tv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_title_1_tv);
        final TextView amountTv = (TextView) rootView.findViewById(com.hooview.app.R.id.red_pack_amount_tv);
        final ListView listView = (ListView) rootView.findViewById(com.hooview.app.R.id.red_pack_user_lv);

        if (type == TYPE_RED_PACK_RECEIVE) {
            UserUtil.showUserPhoto(activity, chatRedPackInfo.getLogo(), redPackLogo);
            if (!TextUtils.isEmpty(chatRedPackInfo.getName())) {
                titleTv.setText(chatRedPackInfo.getName());
            }
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
        } else if (type == TYPE_RED_PACK_RESULT_0) {
            UserUtil.showUserPhoto(activity, chatRedPackInfo.getLogo(), redPackLogo);
            if (!TextUtils.isEmpty(chatRedPackInfo.getName())) {
                titleTv.setText(chatRedPackInfo.getName());
            }
            if (!chatRedPackInfo.isGetRedPack()) {
                infoTv.setText(activity.getResources().getString(com.hooview.app.R.string.red_pack_open_failed));
            } else {
                infoTv.setText(activity.getResources().getString(com.hooview.app.R.string.red_pack_not_get_amount));
            }
            bgIv.setImageResource(com.hooview.app.R.drawable.living_red_open_0);
            infoTv.setVisibility(View.VISIBLE);
            moreInfoTv.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
        } else if (type == TYPE_RED_PACK_RESULT_1) {
            UserUtil.showUserPhoto(activity, chatRedPackInfo.getLogo(), redPackLogo1);
            if (!TextUtils.isEmpty(chatRedPackInfo.getName())) {
                title1Tv.setText(chatRedPackInfo.getName());
            }
            if (chatRedPackInfo.getCurrentUserAmount() == 0) {
                amountTv.setVisibility(View.INVISIBLE);
            } else {
                amountTv.setVisibility(View.VISIBLE);
                amountTv.setText(activity.getString(com.hooview.app.R.string.e_coin_count_rear,
                        chatRedPackInfo.getCurrentUserAmount()));
            }

            RedPackUserAdapter adapter = new RedPackUserAdapter(activity, chatRedPackInfo.getUsers());
            listView.setAdapter(adapter);
            bgIv.setImageResource(com.hooview.app.R.drawable.living_red_open_1);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
        }
    }

    public static Dialog showReportVideoDialog(Activity activity, String name) {
        return showReportDialog(activity, name, ApiHelper.REPORT_TYPE_VIDEO);
    }

    public static Dialog showReportUserDialog(Activity activity, String userId) {
        return showReportDialog(activity, userId, ApiHelper.REPORT_TYPE_USER);
    }

    public static Dialog showReportGroupDialog(Activity activity, String name) {
        return showReportDialog(activity, name, ApiHelper.REPORT_TYPE_GROUP);
    }

    private static Dialog showReportDialog(final Activity activity, final String name, final int type) {
        String[] reasons = null;
        String title = "";
        switch (type) {
            case ApiHelper.REPORT_TYPE_VIDEO:
                reasons = activity.getResources().getStringArray(com.hooview.app.R.array.report_reason_video);
                title = activity.getResources().getString(com.hooview.app.R.string.title_report_video);
                break;
            case ApiHelper.REPORT_TYPE_USER:
                reasons = activity.getResources().getStringArray(com.hooview.app.R.array.report_reason_video);
                title = activity.getResources().getString(com.hooview.app.R.string.title_report_user);
                break;
            case ApiHelper.REPORT_TYPE_GROUP:
                reasons = activity.getResources().getStringArray(com.hooview.app.R.array.report_reason_group);
                title = activity.getResources().getString(com.hooview.app.R.string.title_report_group);
                break;
        }
        final String[] finalReasons = reasons;
        final Dialog dialog = new AlertDialog.Builder(activity).setTitle(title)
                .setSingleChoiceItems(reasons, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = finalReasons[which];
                        ApiHelper.getInstance().commonReport(name, description, type,
                                new MyRequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        SingleToast.show(activity, com.hooview.app.R.string.msg_report_success);
                                    }

                                    @Override
                                    public void onError(String errorInfo) {
                                        super.onError(errorInfo);
                                    }

                                    @Override
                                    public void onFailure(String msg) {
                                        RequestUtil.handleRequestFailed(msg);
                                    }
                                });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(com.hooview.app.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
        return dialog;
    }

    public static void checkShowFirstRecordDialog(Activity activity) {
        checkShowFirstRecordDialog(activity, null);
    }

    public static void checkShowFirstRecordDialog(Activity activity,
            DialogInterface.OnClickListener onClickListener) {
        final Preferences pref = Preferences.getInstance(activity);
        boolean haveShownDialog = pref.getBoolean(Preferences.KEY_LIVE_GAG_TIPS_DIALOG, false);
        if (haveShownDialog) {
            return;
        }
        DialogInterface.OnClickListener listener = onClickListener;
        if (listener == null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    pref.putBoolean(Preferences.KEY_LIVE_GAG_TIPS_DIALOG, true);
                }
            };
        }
        String content = activity.getString(com.hooview.app.R.string.dl_content_live_gag_tips);
        getOneButtonDialog(activity, content, false, false, listener).show();
    }

    public static Dialog showNetworkRemindDialog(final Activity activity) {
        View view = LayoutInflater.from(activity).inflate(com.hooview.app.R.layout.dialog_network_tips, null, true);
        final Dialog dialog = getCustomDialog(activity, view, true, true, -1);
        dialog.show();
        return dialog;
    }

    public static void showCreateRedPackDialog(final Activity activity, final String vid) {
        final View rootView = LayoutInflater.from(activity)
                .inflate(com.hooview.app.R.layout.live_anchor_create_red_pack, null);
        final Dialog dialog = getCustomDialog(activity, rootView, false, true, -1);
        ImageView closeIv = (ImageView) rootView.findViewById(com.hooview.app.R.id.create_red_pack_close);
        final EditText redPackNumberEt = (EditText) rootView.findViewById(com.hooview.app.R.id.red_pack_number_et);
        final EditText eCoinNumberEt = (EditText) rootView.findViewById(com.hooview.app.R.id.e_coin_number_et);
        final EditText redPackMessageEt = (EditText) rootView.findViewById(com.hooview.app.R.id.red_pack_message_et);
        TextView btnSendRedPackTv = (TextView) rootView.findViewById(com.hooview.app.R.id.btn_send_red_pack_tv);
        redPackNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    redPackNumberEt.setGravity(Gravity.LEFT);
                } else {
                    redPackNumberEt.setGravity(Gravity.RIGHT);
                }
                if (!TextUtils.isEmpty(editable.toString()) && Integer.valueOf(editable.toString()) > 50) {
                    CharSequence charSequence = editable.subSequence(0, 1);
                    redPackNumberEt.setText(charSequence);
                    redPackNumberEt.setSelection(charSequence.length());
                    SingleToast.show(activity, activity.getString(com.hooview.app.R.string.send_red_pack_tip));
                }

            }
        });
        eCoinNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    eCoinNumberEt.setGravity(Gravity.LEFT);
                } else {
                    eCoinNumberEt.setGravity(Gravity.RIGHT);
                }
            }
        });
        btnSendRedPackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(redPackNumberEt.getWindowToken(), 0);
                String redPackNumber = redPackNumberEt.getText().toString();
                final String eCoinNumber = eCoinNumberEt.getText().toString();
                String title = redPackMessageEt.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    title = activity.getString(com.hooview.app.R.string.red_pack_message_default);
                }
                long totalECoin = Preferences.getInstance(activity)
                        .getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, 0);

                if (TextUtils.isEmpty(redPackNumber) || TextUtils.isEmpty(eCoinNumber) || redPackNumber
                        .startsWith("0") || eCoinNumber.startsWith("0")) {
                    SingleToast.show(activity, activity.getString(com.hooview.app.R.string.e_number_null));
                    return;
                }
                if (Integer.valueOf(redPackNumber) > Integer.valueOf(eCoinNumber)) {
                    SingleToast.show(activity, activity.getString(com.hooview.app.R.string.e_number_require_not_accord));
                    return;
                }
                if (Long.valueOf(eCoinNumber) > totalECoin) {
                    SingleToast.show(activity,
                            activity.getString(com.hooview.app.R.string.red_pack_send_tip_not_enough));
                    return;
                }
                if (Integer.valueOf(redPackNumber) > 50) {
                    redPackNumber = "50";
                }
                if (!activity.isFinishing()) {
                    dialog.dismiss();
                }
                ApiHelper.getInstance().sendRedPack(vid, eCoinNumber, redPackNumber, title,
                        new MyRequestCallBack<String >() {
                            @Override
                            public void onSuccess(String result) {
                               SingleToast
                                        .show(activity, activity.getString(com.hooview.app.R.string.red_pack_send_success));
                                Preferences pref = Preferences.getInstance(activity);
                                long eCoin = pref.getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT,
                                        Long.valueOf(eCoinNumber)) - Long.valueOf(eCoinNumber);
                                pref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
                                GiftManager.setECoinCount(activity, eCoin);
                            }

                            @Override
                            public void onFailure(String msg) {
                                RequestUtil.handleRequestFailed(msg);
                                SingleToast.show(activity, activity.getString(com.hooview.app.R.string.request_failed));
                            }
                        });

            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Dialog getBindPhoneDialog(final Activity activity, final int requestCode) {
        return getButtonsDialog(activity, activity.getString(com.hooview.app.R.string.dialog_title_bind_phone), false, false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, BindPhoneActivity.class);
                        activity.startActivityForResult(intent, requestCode);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });
    }

    public static AlertDialog getVideoPermissionSetDialog(final Activity activity,
            DialogInterface.OnClickListener confirmListener) {
        return new AlertDialog.Builder(activity)
                .setTitle(com.hooview.app.R.string.title_set_video_permission)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(com.hooview.app.R.array.video_permission, 0, confirmListener)
                .setNegativeButton(com.hooview.app.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        dialog1.dismiss();
                    }
                })
                .create();
    }

    public static AlertDialog getSetTopicDialog(final Activity activity,
            List<TopicEntity> topicEntities, DialogInterface.OnClickListener confirmListener) {
        String[] topics = null;
        if (topicEntities != null) {
            topics = new String[topicEntities.size()];
            for (int i = 0, s = topicEntities.size(); i < s; i++) {
                topics[i] = topicEntities.get(i).getTitle();
            }
        }
        if (topics == null) {
            return null;
        }
        return new AlertDialog.Builder(activity)
                .setTitle(com.hooview.app.R.string.title_set_video_topic)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(topics, 0, confirmListener)
                .setNegativeButton(com.hooview.app.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public static AlertDialog getSetVideoPayDialog(final Activity activity,
            DialogInterface.OnClickListener confirmListener) {
        final LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(com.hooview.app.R.layout.view_set_video_price, null);
        return new AlertDialog.Builder(activity)
                .setTitle(com.hooview.app.R.string.title_set_video_price)
                .setView(v)
                .setPositiveButton(com.hooview.app.R.string.confirm, confirmListener)
                .setNegativeButton(com.hooview.app.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public interface OpenRedPackListener {
        void onOpen();

        void onOpenSuccess();

        void onOpenFailed();
    }
}
