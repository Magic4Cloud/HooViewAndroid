/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

import java.io.File;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;

import com.hooview.app.activity.home.HomeTabActivity;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.net.UploadThumbAsyncTask;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
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
    public static final String EXTRA_KEY_USER_TYPE = "extra_key_user_type";
    public static final int EXTRA_KEY_USER_TYPE_CHANGE_SIGN = 1;
    public static final int EXTRA_KEY_USER_TYPE_CHANGE_NICK = 2;
    public static final int REQUEST_CODE_EDIT_NICKNAME = 3;
    public static final int REQUEST_CODE_EDIT_SIGN = 4;

    private ImageView mPortraitIv;
    private TextView mYzbNameEt;
    private TextView mYzbIdEt;
    private Spinner mGenderSpinner;
    private TextView mBirthdayEt;
    private TextView mConstellationTv;
    private TextView mLocationEt;
    private TextView mSignatureEt;

    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    private BottomSheet mSetThumbPanel;
    private Bundle bundles;
    private DatePickerDialog mDatePickerDialog;
    private boolean mIsSetRegisterInfo;
    private File mTempLogoPic;
    private InputMethodManager imm;

    private String mNickname;
    private String mSignature;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTempLogoPic = new File(Preferences.getInstance(UserInfoActivity.this)
                .getString(Preferences.KEY_REGISTER_USER_IMAGE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsCancelRequestAfterDestroy = false;
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_userinfo);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bundles = getIntent().getExtras();
        mIsSetRegisterInfo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_REGISTER, false);

        mSetThumbPanel = Utils.getSetThumbBottomPanel(this, IMAGE_FILE_NAME,
                REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        mPortraitIv = (ImageView) findViewById(com.hooview.app.R.id.user_info_portrait_iv);
        mYzbNameEt = (TextView) findViewById(com.hooview.app.R.id.user_info_nickname_et);
        mYzbIdEt = (TextView) findViewById(com.hooview.app.R.id.yb_id_et);
        mGenderSpinner = (Spinner) findViewById(com.hooview.app.R.id.ui_sex_spinner);
        mBirthdayEt = (TextView) findViewById(com.hooview.app.R.id.birthday_et);
        mConstellationTv = (TextView) findViewById(com.hooview.app.R.id.constellation_tv);
        mLocationEt = (TextView) findViewById(com.hooview.app.R.id.ui_location_et);

        mSignatureEt = (TextView) findViewById(com.hooview.app.R.id.signature_et);
        Button mommitBtn = (Button) findViewById(com.hooview.app.R.id.user_info_commit_btn);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText(getString(com.hooview.app.R.string.complete));
        mCenterContentTv.setText(getString(com.hooview.app.R.string.user_info_set));
        mCommitLl.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);
        findViewById(com.hooview.app.R.id.user_info_birthday_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.user_info_item_6).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.user_info_item_1).setOnClickListener(this);
        mYzbNameEt.setOnClickListener(this);
        mPortraitIv.setOnClickListener(this);
        mLocationEt.setOnClickListener(this);
        mLocationEt.setText(com.hooview.app.R.string.default_user_location);
        mommitBtn.setOnClickListener(this);
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mBirthdayEt.setText(year + "-" + (month + 1) + "-" + day);
                String constellation = DateTimeUtil.getConstellation(getApplicationContext(), month + 1, day);
                mConstellationTv.setText(constellation);
            }
        }, 1990, 5, 15);
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        if (mIsSetRegisterInfo) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            mommitBtn.setText(com.hooview.app.R.string.next_step);
        } else {
            mommitBtn.setText(com.hooview.app.R.string.complete);
        }

        if (bundles != null && (!mIsSetRegisterInfo)) {
            setUserInfo(bundles);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.hooview.app.R.id.ui_location_et:
                startActivityForResult(new Intent(this, CitySelectListActivity.class), REQUEST_CODE_CITY);
                break;
            case com.hooview.app.R.id.user_info_portrait_iv:
                mSetThumbPanel.show();
                break;
            case com.hooview.app.R.id.user_info_nickname_et:
                //TODO
                Intent editNickNameIntent = new Intent(UserInfoActivity.this,UserInfoEditActivity.class);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_NICKNAME,mNickname);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_NICK);
                startActivityForResult(editNickNameIntent,REQUEST_CODE_EDIT_NICKNAME);
                break;
            case com.hooview.app.R.id.user_info_item_6:
                //TODO
                Intent editSignIntent = new Intent(UserInfoActivity.this,UserInfoEditActivity.class);
                editSignIntent.putExtra(EXTRA_KEY_USER_SIGN,mSignature);
                editSignIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_SIGN);
                startActivityForResult(editSignIntent,REQUEST_CODE_EDIT_SIGN);
                break;
            case com.hooview.app.R.id.user_info_commit_btn:
                if (mIsSetRegisterInfo) {
                    phoneRegistration(bundles);
                } else {
                    editUserInfo();
                }
                break;
            case com.hooview.app.R.id.user_info_birthday_rl:
                String date[] = mBirthdayEt.getText().toString().split("-");
                if (date.length == 3) {
                    mDatePickerDialog.updateDate(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1,
                            Integer.valueOf(date[2]));
                }
                mDatePickerDialog.show();
                break;
            case com.hooview.app.R.id.add_option_view:
                if (mIsSetRegisterInfo) {
                    phoneRegistration(bundles);
                } else {
                    editUserInfo();
                }
                break;
            case com.hooview.app.R.id.close_iv:
                finish();
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
                    mLocationEt.setText(city);
                    break;
                case REQUEST_CODE_IMAGE:
                    mTempLogoPic = Utils.startPhotoZoom(UserInfoActivity.this, data.getData(),
                            HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                    break;
                case REQUEST_CODE_CAMERA:
                    if (android.os.Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        mTempLogoPic = Utils.startPhotoZoom(UserInfoActivity.this, Uri.fromFile(tempFile),
                                HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                        Preferences.getInstance(UserInfoActivity.this)
                                .putString(Preferences.KEY_REGISTER_USER_IMAGE,
                                        mTempLogoPic.getAbsolutePath());
                    } else {
                        SingleToast.show(this, getResources().getString(com.hooview.app.R.string.msg_alert_no_sd_card));
                    }
                    break;
                case REQUEST_CODE_RESULT:
                    if (mTempLogoPic != null && mTempLogoPic.exists()) {
                        mPortraitIv.setImageBitmap(BitmapFactory.decodeFile(mTempLogoPic.getAbsolutePath()));
                    } else {
                        mPortraitIv.setImageBitmap(BitmapFactory.decodeFile(
                                Preferences.getInstance(UserInfoActivity.this).getString("userImage")));
                    }
                    break;
                case REQUEST_CODE_EDIT_NICKNAME:
                    String nickName = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(nickName)) {
                        mYzbNameEt.setText(nickName);
                    }
                    break;
                case REQUEST_CODE_EDIT_SIGN:
                    String sign = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(sign)) {
                        mSignatureEt.setText(sign);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.hooview.app.R.menu.complete, menu);
        if (mIsSetRegisterInfo) {
            menu.findItem(com.hooview.app.R.id.menu_complete).setTitle(com.hooview.app.R.string.next_step);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.hooview.app.R.id.menu_complete:
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
    }

    private void setUserInfo(Bundle userInfo) {
        String logoUrl = userInfo.getString(ShareConstants.PARAMS_IMAGEURL);
        String gender = userInfo.getString(ShareConstants.PARAMS_SEX);
        String city = userInfo.getString(ShareConstants.USER_CITY);
        String birthday = userInfo.getString(ShareConstants.BIRTHDAY);
        mNickname = userInfo.getString(ShareConstants.PARAMS_NICK_NAME);
        mSignature = userInfo.getString(ShareConstants.DESCRIPTION);
        if (User.GENDER_FEMALE.equals(gender)) {
            mGenderSpinner.setSelection(SPINNER_FEMALE_INDEX);
        } else {
            mGenderSpinner.setSelection(SPINNER_MALE_INDEX);
        }
        if (TextUtils.isEmpty(birthday) || birthday.equals(BIRTHDAY_NO_SELECT)) {
            birthday = DEFAULT_BIRTHDAY;
            mConstellationTv.setText(getResources().getString(com.hooview.app.R.string.constellation_cancer));
        }
        mBirthdayEt.setText(birthday);
        int month = Integer.parseInt(birthday.split("-")[1]);
        int day = Integer.parseInt(birthday.split("-")[2]);
        mConstellationTv.setText(DateTimeUtil.getConstellation(this, month, day));
        mYzbNameEt.setText(mNickname);
        mLocationEt.setText(city);

        UserUtil.showUserPhoto(this, logoUrl, mPortraitIv);
        mSignatureEt.setText(mSignature);
    }

    private void phoneRegistration(final Bundle userInfo) {
        String authType = userInfo.getString(ShareConstants.AUTHTYPE);
        String gender = getString(com.hooview.app.R.string.male)
                .equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : "female";
        String nickname = mYzbNameEt.getText().toString();
        nickname = nickname.replace((char) 12288, ' '); // Remove chinese space
        nickname = nickname.trim();
        if (nickname.equals(getResources().getString(com.hooview.app.R.string.nickname))) {
            SingleToast.show(getApplicationContext(), getResources().getString(com.hooview.app.R.string.msg_nickname_empty));
            return;
        }
        String birthday = mBirthdayEt.getText().toString().trim();
        String location = mLocationEt.getText().toString().trim();
        String signature = mSignatureEt.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            location = getString(com.hooview.app.R.string.default_user_location);
        }
        if (birthday.equals(BIRTHDAY_NO_SELECT)) {
            SingleToast.show(getApplicationContext(), getResources().getString(com.hooview.app.R.string.msg_birthday_empty));
            return;
        }
        showLoadingDialog(com.hooview.app.R.string.submit_data, false, false);
        String phone = bundles.getString("token");
        String password = bundles.getString("password");
        ApiHelper.getInstance().registerByPhone(nickname, phone, gender, password, birthday,
                location, signature, authType, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                dismissLoadingDialog();
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_registered_success);

                if (mTempLogoPic != null && mTempLogoPic.exists()) {
                    AsyncTask uploadTask = new UploadThumbAsyncTask(null);
                    String uploadUrl = ApiConstant.USER_UPLOAD_LOGO + "sessionid=" + user.getSessionid();
                    uploadTask.execute(uploadUrl, BitmapFactory.decodeFile(mTempLogoPic.getAbsolutePath()));
                    user.setLogourl(mTempLogoPic.getAbsolutePath());
                }
                startActivity(new Intent(UserInfoActivity.this, HomeTabActivity.class));
                finish();
                UserUtil.handleAfterLogin(getApplicationContext(), user, "RegisterByPhone");
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(String msg) {
                dismissLoadingDialog();
                RequestUtil.handleRequestFailed(msg);
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_registered_error);
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

        final String nickname = mYzbNameEt.getText().toString().trim();
        final String birthday = mBirthdayEt.getText().toString().trim();
        final String location = mLocationEt.getText().toString().trim();
        final String signature = mSignatureEt.getText().toString().trim();
        final String gender = getString(com.hooview.app.R.string.male)
                .equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : "female";

        if (TextUtils.isEmpty(nickname) || getString(com.hooview.app.R.string.nickname).equals(nickname)) {
            SingleToast.show(this, com.hooview.app.R.string.msg_nickname_empty);
            return;
        }

        if (nickname.equals(user.getNickname()) && birthday.equals(user.getBirthday()) && location
                .equals(user.getLocation()) && signature.equals(user.getSignature()) && gender
                .equals(user.getGender())) {
            finish();
            return;
        }

        ApiHelper.getInstance().userEditInfo(nickname, birthday, location, gender,
                signature, new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        user.setNickname(nickname);
                        user.setBirthday(birthday);
                        user.setLocation(location);
                        user.setGender(gender);
                        user.setSignature(signature);
                        Preferences.getInstance(getApplicationContext())
                                .putString(Preferences.KEY_USER_NICKNAME, nickname);
                        finish();
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (UserInfoActivity.this.getCurrentFocus() != null) {
                if (UserInfoActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(UserInfoActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
