package com.coin.market.jpush;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.coin.market.R;
import com.coin.market.activity.home.feedback.FeedbackActivity;
import com.coin.market.activity.home.news.HomeNewsActivity;
import com.coin.market.activity.main.MainActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

import java.util.Iterator;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.MultiActionsNotificationBuilder;
import cn.jpush.android.helper.Logger;
import teng.wang.comment.utils.log.Log;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            setStyleBasic(context);
            Bundle bundle = intent.getExtras();
            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            // 后台推送返回的解析数据
            String json = printBundle(bundle);
            Log.e("JPush返回Json:" + json + "");

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

                if (!TextUtils.isEmpty(getJson(bundle))) {
                    JPushModel jPushModel = new Gson().fromJson(getJson(bundle), JPushModel.class);
                    if (jPushModel.getType().equals("1")) {
                        //type==1 到问题反馈
                        Intent i = new Intent(context, FeedbackActivity.class);
//						i.putExtras(bundle);
                        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);

                    } else if (jPushModel.getType().equals("2")) {
                        //type==2 广告详情
                        Intent i = new Intent(context, HomeNewsActivity.class);
                        i.putExtra("jPush", jPushModel);
//						i.putExtras(bundle);
                        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            String aaa = key;
            String bb = JPushInterface.EXTRA_EXTRA;
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    // 获取后台定义的 intent extra 数据
    private static String getJson(Bundle bundle) {
        String str = "";
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            String aa = key;
            String bb = JPushInterface.EXTRA_EXTRA;
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    str = json.toString();
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return str;
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic(Context context) {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(999, builder);
        //Toast.makeText(context, "Basic Builder 999", Toast.LENGTH_SHORT).show();


        //setAddActionsStyle
//		MultiActionsNotificationBuilder builder = new MultiActionsNotificationBuilder(context);
//		builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "first", "my_extra1");
//		builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "second", "my_extra2");
//		builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "third", "my_extra3");
//		JPushInterface.setPushNotificationBuilder(999, builder);
    }

}
