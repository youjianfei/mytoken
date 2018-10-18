package mytoken.eos.io.class_;

import android.util.Log;

/**
 * Created by PC on 2017/1/10.
 */

public   class LogUtils {
    static boolean log =true ;

   public static void LOG(String tag, String message, String position){
       if(log){
           Log.i(tag,message+"所在位置："+position);
       }else{
//           Log.i("ceshi","log日志关闭");
       }

   }

}
