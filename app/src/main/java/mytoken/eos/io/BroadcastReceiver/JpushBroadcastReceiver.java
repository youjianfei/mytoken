package mytoken.eos.io.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import mytoken.eos.io.EntityBean.JpushBean;
import mytoken.eos.io.MainActivity;
import mytoken.eos.io.class_.LogUtils;
import mytoken.eos.io.class_.StaticData;

import static mytoken.eos.io.MainActivity.mainActivity;

public class JpushBroadcastReceiver extends BroadcastReceiver {
    JpushBean  jpushBean;
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = "";//判断跳转页面
        String url = "";//判断跳转页面
        Bundle bundle = intent.getExtras();
        String title = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
        if (bundle.getString(JPushInterface.EXTRA_EXTRA) != null) {

        }
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtils.LOG("ceshi", "接收的广播+" + extras, "极光广播接收器");

        if (extras != null && !extras.equals("")) {
            try {
                LogUtils.LOG("ceshi", "接收的广播解析+" , "极光广播接收器");
                JSONObject object = new JSONObject(extras);
                type = (String) object.get("curType");
                url = (String) object.get("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtils.LOG("ceshi","type+"+type,"推送");
            if (type.equals("transfer")) {
                StaticData.JpushURL=url;
                StaticData.isPush=true;
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainActivity.showjpush();
                context.startActivity(mainIntent);
            }

        }







    }

}
