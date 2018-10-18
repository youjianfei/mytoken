package mytoken.eos.io.class_;

import android.content.Context;
import android.widget.Toast;

/**
 * 静态Toast
 * Created by Administrator on 2016/7/6.
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
}
