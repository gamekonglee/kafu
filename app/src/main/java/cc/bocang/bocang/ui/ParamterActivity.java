package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import cc.bocang.bocang.R;

/**
 * 查看参数
 *
 * created by hardawin 2017-12-14
 */
public class ParamterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paramter);
    }

    public void back(View view) {
        finish();
    }
}
