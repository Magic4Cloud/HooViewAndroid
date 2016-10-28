/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hooview.app.R;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.utils.Constants;

public class TextActivity extends BaseActivity {
    public static final String EXTRA_TYPE = "extra_type";
    public static final int TYPE_AGREEMENT = 0;
    public static final int TYPE_PHONE_BIND_ISSUE = 1;

    private String mAgreementText =
            "一、注册协议条款的确认与接受" +
                    "\n" +
                    "在注册成为用户之前，请审慎阅读本《用户注册协议》（以下简称《协议》），确保您充分理解本协议中各条款。您的注册、登录、使用等行为将视为同意接受本协议的全部条款，申请人应当按照应用页面上的提示完成全部的注册程序。\n" +
                    "本《协议》由火眼财经随时更新，更新后的协议条款一旦公布即代替原来的协议条款, 恕不再另行通知, 用户可在应用上查阅最新版协议条款。在修改《协议》条款后, 如果用户不接受修改后的条款, 请立即停止使用本应用服务, 继续使用的用户将被视为已接受了修改后的协议。\n" +
                    "二、服务内容 " +
                    "\n" +
                    "1、火眼财经是移动、直播、分享、交互的全新社交平台，具有互动、分享、社交等功能。" +
                    "\n" +
                    "2、用户了解本公司提供的相关的网络服务, 除此之外与相关网络服务有关的设备(如手机或其他与接入互联网或移动网有关的装置)及所需的费用(如为接入互联网而支付的上网费、为使用移动网而支付的手机费)均应由用户自行负担。\n" +
                    "3、用户不得干扰我们正常地提供产品和服务，包括但不限于" +
                    "：\n" +
                    "（1）攻击、侵入我们的服务器或使服务器过载；" +
                    "\n" +
                    "（2）破解、修改我们提供的客户端程序；\n" +
                    "（3）利用程序的漏洞和错误（Bug）破坏服务的正常进行；\n" +
                    "（4）不合理地干扰或阻碍他人使用我们所提供的产品和服务。 \n" +
                    "三、用户说明 \n" +
                    "1、完成注册程序的用户均为火眼财经用户\n" +
                    "2、用户有义务保证密码和账号的安全, 用户利用该账号所进行的一切活动引起的任何损失或损害, 由用户自行承担全部责任。如用户发现账号遭到未授权的使用或发生其他任何安全问题, 应立即修改账号密码并妥善保管。因黑客行为或用户的保管疏忽导致账号非法使用, 火眼财经不承担任何责任。\n" +
                    "3、用户账号在丢失、遗忘密码后, 须遵照本应用程序的密码找回程序找回账号密码。对用户因被他人冒名申请而致的任何损失, 火眼财经不承担任何责任。 \n" +
                    "4、用户的言行不得违反《计算机信息网络国际联网安全保护管理办法》、《互联网信息服务管理办法》、《互联网电子公告服务管理规定》、《维护互联网安全的决定》、《互联网新闻信息服务管理规定》等相关法律规定，不得在平台上发布、传播或以其它方式传送含有下列内容之一的信息：\n" +
                    "（1）反对宪法所确定的基本原则的；\n" +
                    "（2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；\n" +
                    "（3）损害国家荣誉和利益的；\n" +
                    "（4）煽动民族仇恨、民族歧视、破坏民族团结的；\n" +
                    "（5）破坏国家宗教政策，宣扬邪教和封建迷信的；\n" +
                    "（6）散布谣言，扰乱社会秩序，破坏社会稳定的；\n" +
                    "（7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；\n" +
                    "（8）侮辱或者诽谤他人，侵害他人合法权利的；\n" +
                    "（9）煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；\n" +
                    "（10）以非法民间组织名义活动的；\n" +
                    "（11）含有虚假、有害、胁迫、侵害他人隐私、骚扰、侵害、中伤、粗俗、猥亵、或其它道德上令人反感的内容；\n" +
                    "（12）含有中国法律、法规、规章、条例以及任何具有法律效力之规范所限制或禁止的其它内容的。\n" +
                    "5、用户将其在火眼财经上发表的全部内容，授予火眼财经免费的、非独家使用许可，火眼财经有权将该内容用于火眼财经各种形态的产品和服务上，包括但不限于网站以及发表的应用或其他互联网产品。第三方若出于非商业目的，将用户在火眼财经上发表的内容转载在火眼财经之外的地方，应当注明”转自火眼财经”，并不得对内容进行修改演绎。若需要进行修改，或用于商业目的，第三方应当联系用户获得单独授权，按照用户规定的方式使用该内容。\n" +
                    "6、任何用户发现火眼财经上有内容涉嫌侮辱或者诽谤他人、侵害他人合法权益的或违反本协议的，均有权直接举报。\n" +
                    "7、为了给广大用户提供一个优质社交平台，确保良性健康发展，火眼财经将对涉及反动、色情和发布不良内容的用户，进行严厉处理。一经发现，将给予永久封禁账号并清空所有内容的处理。\n" +
                    "四、隐私保护 \n" +
                    "火眼财经不对外公开或向第三方提供用户的注册资料及及用户在使用网络服务时存储在本平台的非公开内容，但下列情况除外: \n" +
                    "1、事先获得用户的明确授权;\n" +
                    "2、根据有关的法律法规要求;\n" +
                    "3、按照相关政府主管部门的要求;\n" +
                    "4、第三方同意承担与火眼财经同等的保护用户隐私的责任；在不透露单个用户隐私资料的前提下，火眼财经有权对整个用户数据库进行分析并对用户数据库进行商业上的利用。\n" +
                    "五、免责申明\n" +
                    "1、平台上发布的内容仅代表用户观点，与火眼财经无关。作为内容的发表者，需自行对所发表内容负责，因所发表内容引发的一切纠纷，由该内容的发表者承担全部法律及连带责任。火眼财经不承担任何法律及连带责任。\n" +
                    "2、火眼财经不保证网络服务一定能满足用户的要求，也不保证网络服务不会中断，对网络服务的及时性、安全性、准确性也都不作保证。\n" +
                    "3、火眼财经如因系统维护或升级等而需暂停服务时，将事先公告。对于因不可抗力或火眼财经不能控制的原因造成的网络服务中断或信息丢失等其它问题，火眼财经不承担任何责任，但将尽力减少因此而给用户造成的损失和影响。\n" +
                    "\n" +
                    "本协议未涉及的问题参见国家有关法律法规，当本协议与国家法律法规冲突时，以国家法律法规为准。\n" +
                    "\n";
    private String mPhoneAappeal = "一、您好，针对您“手机号不能正常使用”解绑手机号的情况,请您提供相应申诉材料并且发邮件到客服邮箱进行申诉，我们会尽快帮您解决问题。\n"
            + "\n客服邮箱：service@cloudfocus.cn\n\n"
            + "二、所需材料如下：\n"
            + "1、个人中心截图和账号绑定界面截图\n"
            + "2、个人页中出现的人物正面照+相关证件照对比\n"
            + "3、常用登录火眼财经的手机型号\n"
            + "4、注册时间\n"
            + "5、相互关注好友佐证（建立关系越早、佐证好友越多则佐证效果越强\n"
            + "6、带有身份证的销号证明截图7、其他可证明材料"
            + "三、如您完整提供上述材料，我们将会在10个工作日之内核实您的情况，通过后即刻帮您解除绑定的手机，\n"
            + "如未通过说明，您的资料提供的不完整，或者材料含有虚假成分，请您继续联系客服邮箱。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra(Constants.EXTRA_KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        setContentView(com.hooview.app.R.layout.activity_privacy_service);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView textView = (TextView) findViewById(com.hooview.app.R.id.content_txv);

        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_AGREEMENT);

        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);


        if (type == TYPE_PHONE_BIND_ISSUE) {//申诉说明
            textView.setText(mPhoneAappeal);
            tv.setText(R.string.verify_appeals_phone_error);

        } else if (type == TYPE_AGREEMENT) {//用户协议
            textView.setText(mAgreementText);
            tv.setText(R.string.msg_login_user_agreement);
        }
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.hooview.app.R.menu.complete, menu);
        menu.findItem(com.hooview.app.R.id.menu_complete).setTitle(com.hooview.app.R.string.to_appeal);
        if (getIntent().getIntExtra(EXTRA_TYPE, TYPE_AGREEMENT) == TYPE_PHONE_BIND_ISSUE) {
            menu.findItem(com.hooview.app.R.id.menu_complete).setVisible(true);
        } else {
            menu.findItem(com.hooview.app.R.id.menu_complete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.hooview.app.R.id.menu_complete:
                Intent it = new Intent(Intent.ACTION_SEND);
                it.setType("text/plain");
                it.putExtra(Intent.EXTRA_EMAIL, new String[]{"service@cloudfocus.cn"});
                try {
                    startActivity(it);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(TextActivity.this, getString(com.hooview.app.R.string.no_email_applications_installed),
                            Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
