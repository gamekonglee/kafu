package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cc.bocang.bocang.R;

/**
 * 光源体验（基础光源）
 *
 * created by hardawin 2017-11-25
 */
public class BaseLightActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout thumb_1, thumb_2, thumb_3, thumb_4, thumb_5, video;

    private Intent mIntent = null;

    private String base_id = "base_id";

    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_light);
        mIntent = new Intent(BaseLightActivity.this, BaseLightDetailActivity.class);
        initView();
    }

    public void goBack(View v){
        finish();
    }

    private void initView() {
        thumb_1 = (LinearLayout) findViewById(R.id.thumb_1);
        thumb_2 = (LinearLayout) findViewById(R.id.thumb_2);
        thumb_3 = (LinearLayout) findViewById(R.id.thumb_3);
        thumb_4 = (LinearLayout) findViewById(R.id.thumb_4);
        thumb_5 = (LinearLayout) findViewById(R.id.thumb_5);
        video = (LinearLayout) findViewById(R.id.video);

        thumb_1.setOnClickListener(this);
        thumb_2.setOnClickListener(this);
        thumb_3.setOnClickListener(this);
        thumb_4.setOnClickListener(this);
        thumb_5.setOnClickListener(this);
        video.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.thumb_1) {
            num = 1;
            mIntent.putExtra(base_id, num);
            startActivity(mIntent);
        } else if (view.getId() == R.id.thumb_2) {
            num = 2;
            mIntent.putExtra(base_id, num);
            startActivity(mIntent);
        } else if (view.getId() == R.id.thumb_3) {
            num = 3;
            mIntent.putExtra(base_id, num);
            startActivity(mIntent);
        } else if (view.getId() == R.id.thumb_4) {
            num = 4;
            mIntent.putExtra(base_id, num);
            startActivity(mIntent);
        } else if (view.getId() == R.id.thumb_5) {
            num = 5;
            mIntent.putExtra(base_id, num);
            startActivity(mIntent);
        } else if(view == video) {
            Intent intent = new Intent(BaseLightActivity.this, HospitalActivity.class);// 医院房间
            startActivity(intent);
        }

    }
}
