package mytoken.eos.io.class_;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import mytoken.eos.io.EntityBean.UpdataBean;
import mytoken.eos.io.Interface.Interface_volley_respose;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/7.
 */

public class AutoUpdate {

    private ProgressDialog mProgress;
    private int progress; //apk下载的进度
    private Activity activity;

    public AutoUpdate(Activity activity) {
        this.activity = activity;
    }

    /**
     * 下载的apk地址
     */
    private final int DOWNLOAD = 1;
    private final int DOWNLOAD_FINISH = 2;
    private String mSavePath;
    private Handler downHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
            }

        }

    };


    void quanxian(){
        /**
         * 判断是否有存储权限
         */
        int checkWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);//获取系统是否被授予该种权限
        if (checkWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {//如果没有被授予
            mProgress.dismiss();
            ToastUtils.showToast(activity,"请打开应用的存储权限");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x123);
            //请求获取该种权限
        }
    }
    UpdataBean checkVistionBean;
    /**
     * 联网查询版本
     */
    String newdownurl = "https://www.eosmytoken.io/api/index/update";//下载apk网址
    public  void requestVersionData() {

        new Volley_Utils(new Interface_volley_respose() {
            @Override
            public void onSuccesses(String respose) {

                    checkVistionBean=new Gson().fromJson(respose,UpdataBean.class);
                    newdownurl = checkVistionBean.getUrl();//下载新版本的网址
                    int newVersion = (Integer) checkVistionBean.getVersion();//新的版本号
                    LogUtils.LOG("ceshi","联网查询的版本号是"+newVersion+"地址"+newdownurl,"updataapp");
                    int curVersionCode = getVersionInfo();
                    if (curVersionCode == Integer.MAX_VALUE) {

                        LogUtils.LOG("ceshi", "获得当前版本号出错","updataapp");
                        return;
                    } else {

                        if (curVersionCode < newVersion) {//有新版本

                            showUpdateDialog(newVersion, newdownurl);

                        } else {//没有新版本
                            return;
                        }
                    }

            }

            @Override
            public void onError(int error) {

            }
        }).Http("https://www.eosmytoken.io/api/index/update", activity, 0);
    }
    /**
     * 得到当前版本号
     *
     * @return
     */
    private int getVersionInfo() {

        PackageManager pm =activity. getPackageManager();
        // flag写一个0就是全部拿到封装在PackageInfo对象中
        try {
            PackageInfo info = pm.getPackageInfo(activity.getPackageName(), 0);

            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    private void showUpdateDialog(final int code, final String apkurl) {
       activity. runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("升级提示");
                builder.setMessage("有新版本，请更新!");
                builder.setCancelable(false);

                builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mProgress = new ProgressDialog(activity);
                        mProgress.setMax(100);
                        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgress.setMessage("正在下载...");
                        // 对话框显示出来
                        mProgress.setCancelable(false);
                        mProgress.show();

                        /**
                         * 判断是否有存储权限
                         */
                        int checkWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);//获取系统是否被授予该种权限
                        if (checkWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {//如果没有被授予
                            mProgress.dismiss();
                            ToastUtils.showToast(activity,"请打开应用的存储权限");
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x123);
                            //请求获取该种权限
                        }else{
                            // 直接下载
                            new downloadApkThread().start();
                        }
                    }

                });

                builder.show();

            }
        });

    }



    /**
     * 下载apk
     */
    private boolean cancelUpdate = false;

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(newdownurl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "version.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;

                        progress = (int) (((float) count / length) * 100);
                        downHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            downHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            cancelUpdate = true;
                            mProgress.dismiss();
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 安装APK
     */
    private void installApk() {

        File newFile = new File(mSavePath, "version.apk");


        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(activity, "mytoken.eos.io.FileProvider", newFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        } else {
            intent.setDataAndType(Uri.fromFile(newFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
