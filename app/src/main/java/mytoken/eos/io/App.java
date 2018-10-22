package mytoken.eos.io;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;
import mytoken.eos.io.class_.LogUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
/**
// * 极光推送
// */
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);
//        String jpushid = JPushInterface.getRegistrationID(getApplicationContext());
////        Staticdata.JpushID = jpushid;
//        LogUtils.LOG("ceshi", "JpushId@@@" + jpushid, "APP");

    }
}
