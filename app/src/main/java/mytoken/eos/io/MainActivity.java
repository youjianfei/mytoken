package mytoken.eos.io;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import mytoken.eos.io.Interface.InterfacePermission;


import mytoken.eos.io.class_.AutoUpdate;
import mytoken.eos.io.class_.LogUtils;
import mytoken.eos.io.class_.Permissionmanage;
import mytoken.eos.io.class_.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.master.permissionhelper.PermissionHelper;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class MainActivity extends Activity {
    //控件
    private WebView webView;
    ProgressBar  mPrigressBer;

    //对象
    PermissionHelper permissionHelper;
    AutoUpdate autoUpdate;

    //URL
//    String  index="http://eoskoreanode.com/t.html";//测试不信任证书 404  500等
    String  index="https://eosmytoken.io/app/index/index.html";
//    String  index="https://eosmytoken.io/app/index/index.html";
    String  erweima ="https://eosmytoken.io/app/index/tout.html?address=";//二维码拼接链接

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initdata();
        initwebview();
        initlinstenner();
    }

    private void initdata() {
        /**
         * 极光推送
         */
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String jpushid = JPushInterface.getRegistrationID(getApplicationContext());
        LogUtils.LOG("ceshi", "JpushId~~" +jpushid, "mainactivity");
        permissionHelper = new PermissionHelper(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA }, 100);
        updata();
    }
    void updata(){

        Permissionmanage permissionmanage = new Permissionmanage(permissionHelper, new InterfacePermission() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//安卓7.0权限 代替了FileProvider方式   https://blog.csdn.net/xiaoyu940601/article/details/54406725
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                    }
                    //检测是否更新
                    autoUpdate = new AutoUpdate(MainActivity.this);
                    autoUpdate.requestVersionData();

                } else {
                    Toast.makeText(MainActivity.this,"请开启存储权限",Toast.LENGTH_SHORT).show();
                }
            }
        });
        permissionmanage.requestpermission();

    }


    private void initwebview() {
        WebSettings settings = webView.getSettings();
        // 设置与Js交互的权限
        settings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缩放级别
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        // 支持缩放
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);//开启DOM storage API功能
        settings.setAllowFileAccess(true);
        // 将网页内容以单列显示
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        final String url = index;
//        final String url = "file:///android_asset/text.html";

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);//错误提示

                LogUtils.LOG("ceshi","dsfa"+error,"断网");
                Intent intent=new Intent(MainActivity.this,KongActivity.class);
                startActivity(intent);
                finish();
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    mPrigressBer.setVisibility(View.GONE);//加载完网页进度条消失
                    LogUtils.LOG("ceshi",webView.getUrl(),"网址");
                    if(webView.getUrl().contains("https://eosmytoken.io/app/index/index.html")||
                            webView.getUrl().contains("https://eosmytoken.io/app/news/index.html")||
                                    webView.getUrl().contains("https://eosmytoken.io/app/user/index.html")
//                    ||webView.getUrl().contains("http://app.ete-coin.com/app/user/index.html")
                            ){
                        webView.clearHistory();
                    }
                }
                else{
                    mPrigressBer.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mPrigressBer.setProgress(newProgress);//设置进度值
                }


            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.LOG("ceshi",title,"网址网址网址");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if(title.contains("找不到")){
                        Intent intent=new Intent(MainActivity.this,KongActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }


            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                LogUtils.LOG("ceshi","图片选择","tupian");
                onenFileChooseImpleForAndroid(filePathCallback);
                return true;

            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Uri uri = Uri.parse(message);
                if ( uri.getScheme().equals("mytoken")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    Log.i("ceshi",uri.getAuthority()+".....2");
//                    webView.stopLoading();
                    if(uri.getAuthority().equals("scan")){
                        erweima();
                    }else {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(uri.getAuthority());
                        ToastUtils.showToast(MainActivity.this,"复制到剪贴板成功");
                    }
//                    result.confirm("js调用了Android的方法成功啦");
//                    if (uri.getAuthority().equals("webview")) {
//                        //
//                        // 执行JS所需要调用的逻辑
//                        Log.i("ceshi","js调用了Android的方法"+".....3");
//                        // 可以在协议上带有参数并传递到Android上
//                        HashMap<String, String> params = new HashMap<>();
//                        Set<String> collection = uri.getQueryParameterNames();
//                        //参数result:代表消息框的返回值(输入值)
////                        result.confirm("js调用了Android的方法成功啦");
////                        webView.loadUrl("https://www.baidu.com/");
//                        Log.i("ceshi",uri.getQueryParameter("arg2")+".....4");
//                        show();
////                        Intent intent=new Intent(MainActivity.this,TextActivity.class);
////                        startActivity(intent);
//                    }
                    result.cancel();
                    return true;
                }
                return true;
            }
        });


    }
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    public ValueCallback<Uri> mUploadMessage;
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }



    private void initview() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.black), 0);//状态栏颜色
        webView=findViewById(R.id.webview);
        mPrigressBer=findViewById(R.id.pb);
//        mtextview=findViewById(R.id.textview);
    }
    public  void show(){
        webView.loadUrl(erweima);
//        LogUtils.LOG("ceshi",erweima,"show");
//        Toast.makeText(MainActivity.this,"调用了安卓", Toast.LENGTH_SHORT).show();
    }
    private  void initlinstenner(){
//        mtextview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                erweima();
//
//            }
//        });
    }
    void erweima(){
        Permissionmanage permissionmanage = new Permissionmanage(permissionHelper, new InterfacePermission() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//安卓7.0权限 代替了FileProvider方式   https://blog.csdn.net/xiaoyu940601/article/details/54406725
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                    }
                    //如果不传 ZxingConfig的话，两行代码就能搞定了
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    startActivityForResult(intent, 111);

                } else {
                    Toast.makeText(MainActivity.this,"请允许开启摄像头，读取本地图片",Toast.LENGTH_SHORT).show();
                }
            }
        });
        permissionmanage.requestpermission();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 再点一次退出
     */
    private long mLastTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastTime > 2000) {
            // 两次返回时间超出两秒
            Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
            mLastTime = System.currentTimeMillis();
        } else {
            // 两次返回时间小于两秒，可以退出
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null: data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null: data.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        // 扫描二维码/条码回传
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                erweima ="https://eosmytoken.io/app/index/tout.html?address=";
                erweima=erweima+content;
                if(content!=null||!content.equals("")){
                    show();
                }

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
