/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.user;

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
import android.view.LayoutInflater;
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
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.activity.user.CitySelectListActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.UploadThumbAsyncTask;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.flowlayout.FlowLayout;
import com.hooview.app.R;

import java.io.File;
import java.util.List;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
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

    private ImageView mPortraitIv;
    private TextView mYzbNameEt;
    private TextView mYzbIdEt;
    private Spinner mGenderSpinner;
    private TextView mBirthdayEt;
    private TextView mConstellationTv;
    private TextView mLocationEt;
    private TextView mSignatureEt;
    private TextView mCertificateEt;

    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;
    private FlowLayout userLabelFl;

    private BottomSheet mSetThumbPanel;
    private Bundle bundles;
    private DatePickerDialog mDatePickerDialog;
    private boolean mIsSetRegisterInfo;
    private File mTempLogoPic;
    private InputMethodManager imm;

    private String mNickname;
    private String mSignature;
    private String mCertificate;

    public static void start(Context context, Bundle bundle) {
        if (Preferences.getInstance(context).isLogin() && EVApplication.isLogin()) {
            Intent starter = new Intent(context, UserInfoActivity.class);
            starter.putExtras(bundle);
            context.startActivity(starter);
        } else {
            LoginActivity.start(context);
        }
    }

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
        setContentView(R.layout.activity_userinfo);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bundles = getIntent().getExtras();
        mIsSetRegisterInfo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_REGISTER, false);

        mSetThumbPanel = Utils.getSetThumbBottomPanel(this, IMAGE_FILE_NAME,
                REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        mPortraitIv = (ImageView) findViewById(R.id.user_info_portrait_iv);
        mYzbNameEt = (TextView) findViewById(R.id.user_info_nickname_et);
        mYzbIdEt = (TextView) findViewById(R.id.yb_id_et);
        mGenderSpinner = (Spinner) findViewById(R.id.ui_sex_spinner);
        mBirthdayEt = (TextView) findViewById(R.id.birthday_et);
        mConstellationTv = (TextView) findViewById(R.id.constellation_tv);
        mLocationEt = (TextView) findViewById(R.id.ui_location_et);
        userLabelFl = (FlowLayout) findViewById(R.id.user_label_fl);

        mSignatureEt = (TextView) findViewById(R.id.signature_et);
        Button mommitBtn = (Button) findViewById(R.id.user_info_commit_btn);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText(getString(R.string.complete));
        mCenterContentTv.setText(getString(R.string.user_info_set));
        mCommitLl.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);
        findViewById(R.id.user_info_birthday_rl).setOnClickListener(this);
        findViewById(R.id.user_info_item_6).setOnClickListener(this);
        findViewById(R.id.user_info_item_1).setOnClickListener(this);
        findViewById(R.id.user_certificate).setOnClickListener(this);
        findViewById(R.id.user_label).setOnClickListener(this);
        mYzbNameEt.setOnClickListener(this);
        mPortraitIv.setOnClickListener(this);
        mLocationEt.setOnClickListener(this);
        mLocationEt.setText(R.string.default_user_location);
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
            mommitBtn.setText(R.string.next_step);
        } else {
            mommitBtn.setText(R.string.complete);
        }

        if (bundles != null && (!mIsSetRegisterInfo)) {
            setUserInfo(bundles);
        }
        boolean isVip = bundles.getBoolean(Constants.EXTRA_KEY_IS_VIP, false);
        if (isVip) {
            findViewById(R.id.user_certificate).setVisibility(View.VISIBLE);
            findViewById(R.id.user_label).setVisibility(View.VISIBLE);
            mCertificateEt = (TextView) findViewById(R.id.certificate_et);
            mCertificate = bundles.getString(UserInfoActivity.EXTRA_KEY_USER_CERTIFICATE);
            Logger.d(TAG, "onCreate: " + mCertificate);
            if (!TextUtils.isEmpty(mCertificate)) {
                mCertificateEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                mCertificateEt.setText(mCertificate);
            }
            List<String> tags = bundles.getStringArrayList(Constants.EXTRA_ADD_LABEL);
            addLabels(tags);
        } else {
            findViewById(R.id.user_certificate).setVisibility(View.INVISIBLE);
            findViewById(R.id.user_label).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ui_location_et:
                startActivityForResult(new Intent(this, CitySelectListActivity.class), REQUEST_CODE_CITY);
                break;
            case R.id.user_info_portrait_iv:
                mSetThumbPanel.show();
                break;
            case R.id.user_info_nickname_et:
                //TODO
                Intent editNickNameIntent = new Intent(UserInfoActivity.this, UserInfoEditActivity.class);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_NICKNAME, mNickname);
                editNickNameIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_NICK);
                startActivityForResult(editNickNameIntent, REQUEST_CODE_EDIT_NICKNAME);
                break;
            case R.id.user_info_item_6:
                //TODO
                Intent editSignIntent = new Intent(UserInfoActivity.this, UserInfoEditActivity.class);
                editSignIntent.putExtra(EXTRA_KEY_USER_SIGN, mSignature);
                editSignIntent.putExtra(EXTRA_KEY_USER_TYPE, EXTRA_KEY_USER_TYPE_CHANGE_SIGN);
                startActivityForResult(editSignIntent, REQUEST_CODE_EDIT_SIGN);
                break;
            case R.id.user_certificate:
                //TODO
                Intent certificateIntent = new Intent(UserInfoActivity.this, UserInfoEditActivity.class);
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
            case R.id.user_info_birthday_rl:
                String date[] = mBirthdayEt.getText().toString().split("-");
                if (date.length == 3) {
                    mDatePickerDialog.updateDate(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1,
                            Integer.valueOf(date[2]));
                }
                mDatePickerDialog.show();
                break;
            case R.id.add_option_view:
                if (mIsSetRegisterInfo) {
                    phoneRegistration(bundles);
                } else {
                    editUserInfo();
                }
                break;
            case R.id.close_iv:
                finish();
                break;
            case R.id.user_label:
                startActivityForResult(new Intent(this, UserAddLabelActivity.class), REQUEST_CODE_LABEL);
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
                        SingleToast.show(this, getResources().getString(R.string.msg_alert_no_sd_card));
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
                        mYzbNameEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
                        mYzbNameEt.setText(nickName);
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

    private void addLabels(List<String> tags){
        if (tags != null && tags.size() > 0){
            userLabelFl.removeAllViews();
            for (int i = 0; i < tags.size(); i++) {
                TextView textView = (TextView) LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.layout_use_tag, null);
                textView.setText(tags.get(i));
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
            mConstellationTv.setText(getResources().getString(R.string.constellation_cancer));
        }
        mBirthdayEt.setText(birthday);
        int month = Integer.parseInt(birthday.split("-")[1]);
        int day = Integer.parseInt(birthday.split("-")[2]);
        mConstellationTv.setText(DateTimeUtil.getConstellation(this, month, day));
        if (!TextUtils.isEmpty(mNickname)) {
            mYzbNameEt.setTextColor(getResources().getColor(R.color.login_text_color_6));
            mYzbNameEt.setText(mNickname);
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
        String gender = getString(R.string.male)
                .equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : "female";
        String nickname = mYzbNameEt.getText().toString();
        nickname = nickname.replace((char) 12288, ' '); // Remove chinese space
        nickname = nickname.trim();
        if (nickname.equals(getResources().getString(R.string.nickname))) {
            SingleToast.show(getApplicationContext(), getResources().getString(R.string.msg_nickname_empty));
            return;
        }
        String birthday = mBirthdayEt.getText().toString().trim();
        String location = mLocationEt.getText().toString().trim();
        String signature = mSignatureEt.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            location = getString(R.string.default_user_location);
        }
        if (birthday.equals(BIRTHDAY_NO_SELECT)) {
            SingleToast.show(getApplicationContext(), getResources().getString(R.string.msg_birthday_empty));
            return;
        }
        showLoadingDialog(R.string.submit_data, false, false);
        String phone = bundles.getString("token");
        String password = bundles.getString("password");
        ApiHelper.getInstance().registerByPhone(nickname, phone, gender, password, birthday,
                location, signature, authType, new MyRequestCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        dismissLoadingDialog();
                        SingleToast.show(getApplicationContext(), R.string.msg_registered_success);

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

        final String nickname = mYzbNameEt.getText().toString().trim();
        final String birthday = mBirthdayEt.getText().toString().trim();
        final String location = mLocationEt.getText().toString().trim();
        final String signature = mSignatureEt.getText().toString().trim();
        final String credentials = mCertificateEt != null ? mCertificateEt.getText().toString().trim() : "";
        final String gender = getString(R.string.male)
                .equals(mGenderSpinner.getSelectedItem().toString().trim()) ? "male" : "female";

        if (TextUtils.isEmpty(nickname) || getString(R.string.nickname).equals(nickname)) {
            SingleToast.show(this, R.string.msg_nickname_empty);
            return;
        }

        if (nickname.equals(user.getNickname()) && birthday.equals(user.getBirthday()) && location
                .equals(user.getLocation()) && signature.equals(user.getSignature()) && gender
                .equals(user.getGender()) && credentials.equals(user.getCredentials())) {
            finish();
            return;
        }
        Logger.d(TAG, "editUserInfo: " + user.toString());
        ApiHelper.getInstance().userEditInfo(nickname, birthday, location, gender,
                signature, credentials, new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        user.setNickname(nickname);
                        user.setBirthday(birthday);
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
                        SingleToast.show(UserInfoActivity.this, errorInfo);
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
