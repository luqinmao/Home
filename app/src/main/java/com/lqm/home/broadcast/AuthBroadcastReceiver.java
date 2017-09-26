package com.lqm.home.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lqm.home.activity.LoginOrRegistActivity;
import com.lqm.home.utilslqr.UIUtils;

import static com.netease.nimlib.sdk.StatusCode.FORBIDDEN;
import static com.netease.nimlib.sdk.StatusCode.KICKOUT;
import static com.netease.nimlib.sdk.StatusCode.KICK_BY_OTHER_CLIENT;
import static com.netease.nimlib.sdk.StatusCode.PWD_ERROR;

/**
 * @创建者 CSDN_LQR
 * @描述 云信登录出错广播接收者
 */
public class AuthBroadcastReceiver extends BroadcastReceiver {

    public static String ACTION = AuthBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {

            int status = intent.getIntExtra("status", -1);
            if (status == FORBIDDEN.getValue()) {
                UIUtils.showToast("被服务器禁止登录");
            } else if (status == KICKOUT.getValue()) {
                UIUtils.showToast("被其他端的登录踢掉");
            } else if (status == KICK_BY_OTHER_CLIENT.getValue()) {
                UIUtils.showToast("被同时在线的其他端主动踢掉");
            } else if (status == PWD_ERROR.getValue()) {
                UIUtils.showToast("用户名或密码错误!");
            }
            Intent i = new Intent(context, LoginOrRegistActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }

    }
}
