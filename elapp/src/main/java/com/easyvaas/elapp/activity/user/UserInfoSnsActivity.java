/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.UploadThumbAsyncTask;
import com.easyvaas.elapp.ui.MainActivity;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.user.UserInfoActivity;
import com.easyvaas.elapp.ui.user.UserInfoEditActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;

import java.io.File;

public class UserInfoSnsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = UserInfoSnsActivity.class.getSimpleName();

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
    public static final int REQUEST_CODE_EDIT_NICKNAME = 3;
    public static final int REQUEST_CODE_EDIT_SIGN = 4;

    private ImageView mPortraitIv;
    private TextView mYzbNameTv;
    private EditText mYzbIdEt;
    private Spinner mGenderSpinner;
    private TextView mBirthdayEt;
    private TextView mConstellationTv;
    private TextView mLocationTv;
    private TextView mSignatureTv;

    private BottomSheet mSetThumbPanel;
    private Bundle bundles;
    private DatePickerDialog mDatePickerDialog;
    private String mAuthType;
    private boolean mIsSetRegisterInfo;
    private File sdcardTempPic;
    private String mNickname;
    private String mSignature;
    private TextView mCommonTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        bundles = getIntent().getExtras();
        mIsSetRegisterInfo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_REGISTER, false);
        mAuthType = getIntent().getStringExtra(Constants.EXTRA_KEY_ACCOUNT_TYPE);
        if (TextUtils.isEmpty(mAuthType)) {
            mAuthType = User.AUTH_TYPE_PHONE;
        }
        mCommonTitleTv = (TextView) findViewById(R.id.common_custom_title_tv);
        findViewById(R.id.close_iv).setVisibility(View.INVISIBLE);
        findViewById(R.id.user_info_item_6).setOnClickListener(this);
        TextView addOptionTv = (TextView) findViewById(R.id.add_option_iv);
        addOptionTv.setText(R.string.complete);
        mCommonTitleTv.setText(R.string.user_info_set);
        findViewById(R.id.camera_iv).setVisibility(View.GONE);
        mPortraitIv = (ImageView) findViewById(R.id.user_info_portrait_iv);
        mYzbNameTv = (TextView) findViewById(R.id.user_info_nickname_et);
        mYzbNameTv.setOnClickListener(this);
        mYzbIdEt = (EditText) findViewById(R.id.yb_id_et);
        mGenderSpinner = (Spinner) findViewById(R.id.ui_sex_spinner);
        mBirthdayEt = (TextView) findViewById(R.id.birthday_et);
        mConstellationTv = (TextView) findViewById(R.id.constellation_tv);
        mLocationTv = (TextView) findViewById(R.id.ui_location_et);
        mLocationTv.setText(R.string.default_user_location);
        mSignatureTv = (TextView) findViewById(R.id.signature_et);
        findViewById(R.id.user_info_birthday_rl).setOnClickListener(this);
        mPortraitIv.setOnClickListener(this);
        mLocationTv.setOnClickListener(this);
        mSetThumbPanel = Utils.getSetThumbBottomPanel(this, IMAGE_FILE_NAME,
                REQUEST_CODE_CAMERA, REQUEST_CODE_IMAGE);
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mBirthdayEt.setText(year + "-" + (month + 1) + "-" + day);
                mConstellationTv
                        .setText(DateTimeUtil.getConstellation(getApplicationContext(), month + 1, day));
            }
        }, 1990, 5, 15);

        if (mIsSetRegisterInfo) {
            addOptionTv.setText(R.string.next_step);
        } else {
            addOptionTv.setText(R.string.complete);
        }
        addOptionTv.setVisibility(View.VISIBLE);
        addOptionTv.setOnClickListener(this);
        if (bundles != null && (!mIsSetRegisterInfo || !mAuthType.equals(User.AUTH_TYPE_PHONE))) {
            setUserInfo(bundles);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_nickname_et:
                Intent editNickNameIntent = new Intent(this, UserInfoEditActivity.class);
                editNickNameIntent.putExtra(UserInfoActivity.EXTRA_KEY_USER_NICKNAME, mNickname);
                editNickNameIntent.putExtra(UserInfoActivity.EXTRA_KEY_USER_TYPE, UserInfoActivity.EXTRA_KEY_USER_TYPE_CHANGE_NICK);
                startActivityForResult(editNickNameIntent, REQUEST_CODE_EDIT_NICKNAME);
                break;
            case R.id.user_info_portrait_iv:
                mSetThumbPanel.show();
                break;
            case R.id.add_option_iv:
                //userRegistration(bundles);
                snsUserRegistration(bundles);
                break;
            case R.id.user_info_birthday_rl:
                String date[] = mBirthdayEt.getText().toString().split("-");
                if (date.length == 3) {
                    mDatePickerDialog.updateDate(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1,
                            Integer.valueOf(date[2]));
                }
                mDatePickerDialog.show();
                break;
            case R.id.ui_location_et:
                startActivityForResult(new Intent(this, CitySelectListActivity.class), REQUEST_CODE_CITY);
                break;
            case R.id.user_info_item_6:
                //TODO
                Intent editSignIntent = new Intent(this, UserInfoEditActivity.class);
                editSignIntent.putExtra(UserInfoActivity.EXTRA_KEY_USER_SIGN, mSignature);
                editSignIntent.putExtra(UserInfoActivity.EXTRA_KEY_USER_TYPE, UserInfoActivity.EXTRA_KEY_USER_TYPE_CHANGE_SIGN);
                startActivityForResult(editSignIntent, REQUEST_CODE_EDIT_SIGN);
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
                    mLocationTv.setText(city);
                    break;
                case REQUEST_CODE_IMAGE:
                    sdcardTempPic = Utils.startPhotoZoom(UserInfoSnsActivity.this, data.getData(),
                            HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                    break;
                case REQUEST_CODE_CAMERA:
                    if (Environment.getExternalStorageState()
                            .equals(Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        sdcardTempPic = Utils.startPhotoZoom(UserInfoSnsActivity.this, Uri.fromFile(tempFile),
                                HEAD_PORTRAIT_WIDTH, HEAD_PORTRAIT_HEIGHT, REQUEST_CODE_RESULT);
                    } else {
                        SingleToast.show(this, getResources().getString(R.string.msg_alert_no_sd_card));
                    }
                    break;
                case REQUEST_CODE_RESULT:
                    if (sdcardTempPic != null && sdcardTempPic.exists()) {
                        mPortraitIv.setImageBitmap(BitmapFactory.decodeFile(sdcardTempPic.getAbsolutePath()));
                    }
                    break;
                case REQUEST_CODE_EDIT_NICKNAME:
                    String nickName = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(nickName)) {
                        mYzbNameTv.setText(nickName);
                        mNickname = nickName;
                    }
                    break;
                case REQUEST_CODE_EDIT_SIGN:
                    String sign = data.getStringExtra("content");
                    if (!TextUtils.isEmpty(sign)) {
                        mSignatureTv.setText(sign);
                        mSignature = sign;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatePickerDialog != null) {
            mDatePickerDialog.dismiss();
        }
    }

    private void setUserInfo(Bundle userInfo) {
        mNickname = userInfo.getString(ShareConstants.PARAMS_NICK_NAME);
        String logoUrl = userInfo.getString(ShareConstants.PARAMS_IMAGEURL);
        String gender = userInfo.getString(ShareConstants.PARAMS_SEX);
        String city = userInfo.getString(ShareConstants.USER_CITY);
        String signature = userInfo.getString(ShareConstants.DESCRIPTION);
        String birthday = userInfo.getString(ShareConstants.BIRTHDAY);
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
        mYzbNameTv.setText(mNickname);
        mLocationTv.setText(city);
        UserUtil.showUserPhoto(this, logoUrl, mPortraitIv);
        if (!TextUtils.isEmpty(signature)) {
            mSignatureTv.setText(signature);
        }
    }

    private void snsUserRegistration(final Bundle userInfo) {
        String openid = userInfo.getString(ShareConstants.PARAMS_OPENID);
        final String refreshToken = userInfo.getString(ShareConstants.PARAMS_REFRESH_TOKEN);
        String accessToken = userInfo.getString(ShareConstants.PARAMS_ACCESS_TOKEN);
        long expiresIn = userInfo.getLong(ShareConstants.PARAMS_EXPIRES_IN);
        String authType = userInfo.getString(ShareConstants.AUTHTYPE);
        final String logoUrl = userInfo.getString(ShareConstants.PARAMS_IMAGEURL);
        String gender = userInfo.getString(ShareConstants.PARAMS_SEX);

        String birthday = mBirthdayEt.getText().toString().trim();
        String location = mLocationTv.getText().toString().trim();
        String signature = mSignatureTv.getText().toString().trim();
        String nickname = mYzbNameTv.getText().toString().trim();
        String unionid = "";

        nickname = nickname.replace((char) 12288, ' '); // Remove chinese space
        nickname = nickname.trim();
        if (nickname.equals(getResources().getString(R.string.nickname))) {
            mYzbNameTv.setText("");
            SingleToast.show(getApplicationContext(), R.string.msg_nickname_empty);
            return;
        }

        if (User.AUTH_TYPE_WEIXIN.equals(authType)) {
            unionid = userInfo.getString(ShareConstants.PARAMS_UNIONID);
        } else if (User.AUTH_TYPE_SINA.equals(authType)) {
        } else if (User.AUTH_TYPE_QQ.equals(authType)) {
        }
        if (birthday.equals(BIRTHDAY_NO_SELECT)) {
            SingleToast.show(getApplicationContext(), getResources().getString(R.string.msg_birthday_empty));
            return;
        }
        String expires_in = (System.currentTimeMillis() - expiresIn / 1000) + "";
        showLoadingDialog(R.string.submit_data, false, true);
        ApiHelper.getInstance()
                .registerByAuth(nickname, openid, gender, accessToken, birthday,
                        location, signature, authType, expires_in, unionid,
                        refreshToken, logoUrl, new MyRequestCallBack<User>() {
                            @Override
                            public void onSuccess(User user) {
                                if (user != null && !isFinishing()) {
                                    dismissLoadingDialog();
                                    SingleToast
                                            .show(getApplicationContext(), R.string.msg_registered_success);

                                    if (sdcardTempPic != null && sdcardTempPic.exists()) {
                                        AsyncTask uploadTask = new UploadThumbAsyncTask(null);
                                        String uploadUrl = ApiConstant.USER_UPLOAD_LOGO + "sessionid=" + user
                                                .getSessionid();
                                        uploadTask.execute(uploadUrl,
                                                BitmapFactory.decodeFile(sdcardTempPic.getAbsolutePath()));
                                        user.setLogourl(sdcardTempPic.getAbsolutePath());
                                    }
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                    UserUtil.handleAfterLogin(getApplicationContext(), user,
                                            "RegisterByAuth");
                                }
                            }

                            @Override
                            public void onError(String errorInfo) {
                                super.onError(errorInfo);
                                if (ApiConstant.E_USER_EXISTS.equals(errorInfo)) {
                                    SingleToast.show(getApplicationContext(), R.string.msg_phone_registered);
                                }
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
}
