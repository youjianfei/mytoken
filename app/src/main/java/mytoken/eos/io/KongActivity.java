package mytoken.eos.io;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;

public class KongActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kong);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.black), 0);//状态栏颜色

    }
}
