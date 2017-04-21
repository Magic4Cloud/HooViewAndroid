/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.user.newuser.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.activity.user.CitySelectListActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.UploadThumbAsyncTask;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.pop.OperationPopupWindow;
import com.easyvaas.elapp.ui.pop.UserInfoEditPopupWindow;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.UserAddLabelActivity;
import com.easyvaas.elapp.ui.user.UserInfoActivity;
import com.easyvaas.elapp.ui.user.UserInfoEditActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.flowlayout.FlowLayout;
import com.hooview.app.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UserInfoModifyActivity extends MyBaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_LABEL = 102;
    private static final String TAG = "UserInfoActivity";
    private static final int REQUEST_CODE_CITY = 0X10;
    private static final int REQUEST_CODE_IMAGE = 0;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_RESULT = 2;

    private static final int HEAD_PORTRAIT_WIDTH = 320;
    private static final int HEAD_PORTRAIT_HEIGHT = 320;

    private static final int SPINNER_MALE_INDEX = 0;
    private static final int SPINNER_FEMALE_INDEX = 1;
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    private static final String BIRTHDAY_NO_SELECT = UserUtil.BIRTHDAY_EMPTY;
    private static final String DEFAULT_BIRTHDAY = UserUtil.DEFAULT_BIRTHDAY;

    public static final String EXTRA_KEY_USER_NICKNAME = "extra_key_user_nickname";
    public static final String EXTRA_KEY_USER_SIGN = "extra_key_user_sign";
    public static final String EXTRA_KEY_USER_CERTIFICATE = "extra_key_user_certificate";
    public static final String EXTRA_KEY_USER_TYPE = "extra_key_user_type";
    public static final int EXTRA_KEY_USER_TYPE_CHANGE_SIGN = 1;
    public static final int EXTRA_KEY_USER_TYPE_CHANGE_NICK = 2;
    public static final int EXTRA_KEY_USER_TYPE_CERTIFICATE = 3;
    public static final int REQUEST_CODE_EDIT_NICKNAME = 3;
    public static final int REQUEST_CODE_EDIT_SIGN = 4;
    public static final int REQUEST_CODE_EDIT_CERTIFICATE = 5;

    @BindView(R.id.user_info_portrait_iv)
    ImageView mPortraitIv;
    @BindView(R.id.camera_iv)
    ImageView mCameraIv;
    @BindView(R.id.user_info_nickname_et)
    EditText mNicknameEt;
    @BindView(R.id.user_info_sex)
    TextView mSexTv;
    @BindView(R.id.ui_location_et)
    TextView mLocationEt;
    @BindView(R.id.signature_et)
    TextView mSignatureEt;
    @BindView(R.id.user_info_item_certificate)
    RelativeLayout mItemCertificate;
    @BindView(R.id.certificate_et)
    EditText mCertificateEt;
    @BindView(R.id.user_info_item_label)
    RelativeLayout mItemLabel;
    @BindView(R.id.user_label_fl)
    FlowLayout userLabelFl;
    @BindView(R.id.user_info_item_introduce)
    RelativeLayout mItemIntroduce;
    @BindView(R.id.introduce_et)
    TextView mIntroduceTv;

    private BottomSheet mSetThumbPanel;
    private Bundle bundles;
    private DatePickerDialog mDatePickerDialog;
    private boolean mIsSetRegisterInfo;
    private File mTempLogoPic;
    private InputMethodManager imm;

    private String mNickname;
    private String mSignature;
    private String mCertificate;

    private OperationPopupWindow mPopupWindowAvatar;
    private OperationPopupWindow mPopupWindowSex;
    private UserInfoEditPopupWindow mPopupWindowEditSelf;
    private UserInfoEditPopupWindow mPopupWindowEditIntroduce;

    public static void start(Context context, Bundle bundle) {
        if (Preferences.getInstance(context).isLogin() && EVApplication.isLogin()) {
            Intent starter = new Intent(context, UserInfoModifyActivity.class);
            starter.putExtras(bundle);
            context.startActivity(starter);
        } else {
            LoginActivity.start(context);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTempLogoPic = new File(Preferences.getInstance(UserInfoModifyActivity.this)
                .getString(Preferences.KEY_REGISTER_USER_IMAGE));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String getTitleText() {
        return "编辑资料";
    }

    @Override
    protected void initViewAndData() {
        appbarRight();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bundles = getIntent().getExtras();
        mIsSetRegisterInfo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_REGISTER, false);
        mSetThumbPanel = Utils.getSetThumbBottomPanel(this, IMAGE_FILE_NAME,
                REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        mLocationEt.setText(R.string.default_user_location);
        if (bundles != null && (!mIsSetRegisterInfo)) {
            setUserInfo(bundles);
        }
        // 大V身份
        boolean isVip = bundles.getBoolean(Constants.EXTRA_KEY_IS_VIP, false);
        isVip = true;
        if (isVip) {
            mItemCertificate.setVisibility(View.VISIBLE);
            mItemLabel.setVisibility(View.VISIBLE);
            mItemIntroduce.setVisibility(View.VISIBLE);
            mCertificate = bundles.getString(UserInfoActivity.EXTRA_KEY_USER_CERTIFICATE);
            if (!TextUtils.isEmpty(mCertificate)) {
                mCertificateEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                mCertificateEt.setText(mCertificate);
            }
            List<String> tags = bundles.getStringArrayList(Constants.EXTRA_ADD_LABEL);
            addLabels(tags);
        } else {
            mItemCertificate.setVisibility(View.GONE);
            mItemLabel.setVisibility(View.GONE);
            mItemIntroduce.setVisibility(View.GONE);
        }
    }

    /**
     * 完成
     */
    private void appbarRight() {
        mToobarTitleView.setTitleTextRight("完成", R.color.btn_color_secondary_level, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/4/19
                Logger.e("......", "done............");
            }
        });
    }

    /**
     * 头像
     */
    @OnClick(R.id.user_info_avatar)
    public void onAvatarClick() {
        if (mPopupWindowAvatar == null) {
            mPopupWindowAvatar = new OperationPopupWindow(this);
            mPopupWindowAvatar.init("相册", "拍照");
            mPopupWindowAvatar.setOnOperationListener(new OperationPopupWindow.OnOperationListener() {
                @Override
                public void onUp() {
                    Utils.openPhotoAlbum(UserInfoModifyActivity.this, REQUEST_CODE_IMAGE);
                }

                @Override
                public void onDown() {
                    Utils.openCamera(UserInfoModifyActivity.this, IMAGE_FILE_NAME, REQUEST_CODE_CAMERA);
                }
            });
        }
        mPopupWindowAvatar.showAtBottom();
//        mSetThumbPanel.show();
    }

    /**
     * 性别
     */
    @OnClick(R.id.user_info_item_sex)
    public void onSexClick() {
        if (mPopupWindowSex == null) {
            mPopupWindowSex = new OperationPopupWindow(this);
            mPopupWindowSex.init("男", "女");
            mPopupWindowSex.setOnOperationListener(new OperationPopupWindow.OnOperationListener() {
                @Override
                public void onUp() {
                    mSexTv.setText("男");
                }

                @Override
                public void onDown() {
                    mSexTv.setText("女");
                }
            });
        }
        mPopupWindowSex.showAtBottom();
    }

    /**
     * 地区
     */
    @OnClick(R.id.user_info_item_area)
    public void onAreaClick() {
        startActivityForResult(new Intent(this, CitySelectListActivity.class), REQUEST_CODE_CITY);
    }

    /**
     * 介绍自己
     */
    @OnClick(R.id.user_info_item_self)
    public void onSelfClick() {
        if (mPopupWindowEditSelf == null) {
            mPopupWindowEditSelf = new UserInfoEditPopupWindow(this);
            mPopupWindowEditSelf.setOnConfirmListener(new UserInfoEditPopupWindow.OnConfirmListener() {
                @Override
                public void onConfirm(String text) {
                    if (text != null) {
                        if (text.length() > 14) {
                            text = text.substring(0, 14);
                        }
                        mSignatureEt.setText(text);
                    }
                }
            });
        }
        mPopupWindowEditSelf.showWithInputMethod();
    }

    /**
     * 我的标签
     */
    @OnClick(R.id.user_info_item_label)
    public void onUserLabelClick() {
        startActivityForResult(new Intent(this, UserAddLabelActivity.class), REQUEST_CODE_LABEL);
    }

    /**
     * 详细资料
     */
    @OnClick(R.id.user_info_item_introduce)
    public void onIntroduceClick() {
        if (mPopupWindowEditIntroduce == null) {
            mPopupWindowEditIntroduce = new UserInfoEditPopupWindow(this);
            mPopupWindowEditIntroduce.setOnConfirmListener(new UserInfoEditPopupWindow.OnConfirmListener() {
                @Override
                public void onConfirm(String text) {
                    mIntroduceTv.setText(text);
                }
            });
        }
        mPopupWindowEditIntroduce.showWithInputMethod();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_nickname_et:
                //TODO
                Intent editNickNameIntent = new Intent(UserInfoModifyActivity.this, UserInfoEditActivity.class);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_NICKNAME, mNickname);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_NICK);
                startActivityForResult(editNickNameIntent, REQUEST_CODE_EDIT_NICKNAME);
                break;
            case R.id.user_info_item_6:
                //TODO
                Intent editSignIntent = new Intent(UserInfoModifyActivity.this, UserInfoEditActivity.class);
                editSignIntent.putExtra(EXTRA_KEY_USER_SIGN, mSignature);
                editSignIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_SIGN);
                startActivityForResult(editSignIntent, REQUEST_CODE_EDIT_SIGN);
                break;
            case R.id.user_certificate:
                //TODO
                Intent certificateIntent = new Intent(UserInfoModifyActivity.this, UserInfoEditActivity.class);
                certificateIntent.putExtra(EXTRA_KEY_USER_CERTIFICATE, mCertificate);
                certificateIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CERTIFICATE);
                startActivityForResult(certificateIntent, REQUEST_CODE_EDIT_CERTIFICATE);
                break;
            case R.id.user_info_commit_btn:
                if (mIsSetRegisterInfo) {
                    phoneRegistration(bundles);
                } else {
                    editUserInfo();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_CITY:
                    String city = data.getStringExtra(CitySelectListActivity.EXTRA_KEY_SELECT_CITY);
                    mLocationEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                    mLocationEt.setText(city);
                    break;
                case REQUEST_CODE_IMAGE:
                    mTempLogoPic = Utils.startPhotoZoom(UserInfoModifyActivity.this, data.getData(),
                            HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                    break;
                case REQUEST_CODE_CAMERA:
                    if (android.os.Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        mTempLogoPic = Utils.startPhotoZoom(UserInfoModifyActivity.this, Uri.fromFile(tempFile),
                                HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                        Preferences.getInstance(UserInfoModifyActivity.this)
                                .putString(Preferences.KEY_REGISTER_USER_IMAGE,
                                        mTempLogoPic.getAbsolutePath());
                    } else {
                        SingleToast.show(this, getResources().getString(R.string.msg_alert_no_sd_card));
                    }
                    break;
                case REQUEST_CODE_RESULT:
                    if (mTempLogoPic != null && mTempLogoPic.exists()) {
                        mPortraitIv.setImageBitmap(BitmapFactory.decodeFile(mTempLogoPic.getAbsolutePath()));
                    } else {
                        mPortraitIv.setImageBitmap(BitmapFactory.decodeFile(
                                Preferences.getInstance(UserInfoModifyActivity.this).getString("userImage")));
                    }
                    break;
                case REQUEST_CODE_EDIT_NICKNAME:
                    String nickName = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(nickName)) {
                        mNicknameEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                        mNicknameEt.setText(nickName);
                    }
                    break;
                case REQUEST_CODE_EDIT_SIGN:
                    String sign = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(sign)) {
                        mSignatureEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                        mSignatureEt.setText(sign);
                    }
                    break;
                case REQUEST_CODE_EDIT_CERTIFICATE:
                    String ce = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(ce)) {
                        mCertificateEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                        mCertificateEt.setText(ce);
                    }
                    break;
                case REQUEST_CODE_LABEL:
                    List<String> tags = data.getStringArrayListExtra(Constants.EXTRA_ADD_LABEL);
                    addLabels(tags);
                    break;
            }
        }
    }

    private void addLabels(List<String> tags) {
        if (tags != null && tags.size() > 0) {
            userLabelFl.removeAllViews();
            for (int i = 0; i < tags.size(); i++) {
                TextView textView = (TextView) LayoutInflater.from(UserInfoModifyActivity.this).inflate(R.layout.layout_use_tag, null);
                String text = tags.get(i);
                if (i < tags.size() - 1) {
                    text += "，";
                }
                textView.setText(text);
                userLabelFl.addView(textView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        if (mIsSetRegisterInfo) {
            menu.findItem(R.id.menu_complete).setTitle(R.string.next_step);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                if (mIsSetRegisterInfo) {
                    phoneRegistration(bundles);
                } else {
                    editUserInfo();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatePickerDialog != null) {
            mDatePickerDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSetThumbPanel != null && mSetThumbPanel.isShowing()) {
            mSetThumbPanel.dismiss();
        }
        if (mPopupWindowAvatar != null) {
            mPopupWindowAvatar.close();
        }
        if (mPopupWindowSex != null) {
            mPopupWindowSex.close();
        }
    }


    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    private void setUserInfo(Bundle userInfo) {
        String logoUrl = userInfo.getString(ShareConstants.PARAMS_IMAGEURL);
        String gender = userInfo.getString(ShareConstants.PARAMS_SEX);
        String city = userInfo.getString(ShareConstants.USER_CITY);
        mNickname = userInfo.getString(ShareConstants.PARAMS_NICK_NAME);
        mSignature = userInfo.getString(ShareConstants.DESCRIPTION);
        /*if (User.GENDER_FEMALE.equals(gender)) {
            mGenderSpinner.setSelection(SPINNER_FEMALE_INDEX);
        } else {
            mGenderSpinner.setSelection(SPINNER_MALE_INDEX);
        }*/
        if (!TextUtils.isEmpty(mNickname)) {
            mNicknameEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
            mNicknameEt.setText(mNickname);
        }
        if (!TextUtils.isEmpty(city)) {
            mLocationEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
            mLocationEt.setText(city);
        }
        if (!TextUtils.isEmpty(mSignature)) {
            mSignatureEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
            mSignatureEt.setText(mSignature);
        }
        UserUtil.showUserPhoto(this, logoUrl, mPortraitIv);
    }

    private void phoneRegistration(final Bundle userInfo) {
        String authType = userInfo.getString(ShareConstants.AUTHTYPE);
        String gender = /*getString(R.string.male).equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : */"female";
        String nickname = mNicknameEt.getText().toString();
        nickname = nickname.replace((char) 12288, ' '); // Remove chinese space
        nickname = nickname.trim();
        if (nickname.equals(getResources().getString(R.string.nickname))) {
            SingleToast.show(getApplicationContext(), getResources().getString(R.string.msg_nickname_empty));
            return;
        }
        String location = mLocationEt.getText().toString().trim();
        String signature = mSignatureEt.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            location = getString(R.string.default_user_location);
        }
        String phone = bundles.getString("token");
        String password = bundles.getString("password");
        ApiHelper.getInstance().registerByPhone(nickname, phone, gender, password, "",
                location, signature, authType, new MyRequestCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        SingleToast.show(getApplicationContext(), R.string.msg_registered_success);

                        if (mTempLogoPic != null && mTempLogoPic.exists()) {
                            AsyncTask uploadTask = new UploadThumbAsyncTask(null);
                            String uploadUrl = ApiConstant.USER_UPLOAD_LOGO + "sessionid=" + user.getSessionid();
                            uploadTask.execute(uploadUrl, BitmapFactory.decodeFile(mTempLogoPic.getAbsolutePath()));
                            user.setLogourl(mTempLogoPic.getAbsolutePath());
                        }
                        startActivity(new Intent(UserInfoModifyActivity.this, HomeTabActivity.class));
                        finish();
                        UserUtil.handleAfterLogin(getApplicationContext(), user, "RegisterByPhone");
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getApplicationContext(), R.string.msg_registered_error);
                    }
                });
    }

    private void editUserInfo() {
        final User user = EVApplication.getUser();
        if (mTempLogoPic != null && mTempLogoPic.exists()) {
            AsyncTask uploadTask = new UploadThumbAsyncTask(null);
            String uploadUrl = ApiConstant.USER_UPLOAD_LOGO
                    + "sessionid=" + Preferences.getInstance(this).getSessionId();
            uploadTask.execute(uploadUrl, BitmapFactory.decodeFile(mTempLogoPic.getAbsolutePath()));
            user.setLogourl(mTempLogoPic.getAbsolutePath());
        }

        final String nickname = mNicknameEt.getText().toString().trim();
        final String location = mLocationEt.getText().toString().trim();
        final String signature = mSignatureEt.getText().toString().trim();
        final String credentials = mCertificateEt != null ? mCertificateEt.getText().toString().trim() : "";
        final String gender = /*getString(R.string.male).equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : */"female";

        if (TextUtils.isEmpty(nickname) || getString(R.string.nickname).equals(nickname)) {
            SingleToast.show(this, R.string.msg_nickname_empty);
            return;
        }

        if (nickname.equals(user.getNickname()) && location
                .equals(user.getLocation()) && signature.equals(user.getSignature()) && gender
                .equals(user.getGender()) && credentials.equals(user.getCredentials())) {
            finish();
            return;
        }
        Logger.d(TAG, "editUserInfo: " + user.toString());
        ApiHelper.getInstance().userEditInfo(nickname, "", location, gender,
                signature, credentials, new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        user.setNickname(nickname);
                        user.setLocation(location);
                        user.setGender(gender);
                        user.setSignature(signature);
                        user.setCredentials(credentials);
                        Preferences.getInstance(getApplicationContext())
                                .putString(Preferences.KEY_USER_NICKNAME, nickname);
                        finish();
                        EVApplication.updateUserInfo();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(UserInfoModifyActivity.this, errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (UserInfoModifyActivity.this.getCurrentFocus() != null) {
                if (UserInfoModifyActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(UserInfoModifyActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
