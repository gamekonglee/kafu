package cc.bocang.bocang.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.bocang.bocang.R;
import cc.bocang.bocang.utils.ImageUtil;

/**
 * 医院房间
 *
 * created by hardawin 2017-12-14
 */
public class HospitalActivity extends BaseActivity implements View.OnClickListener {

    private HospitalActivity ct = this;
    private ImageView mSceneBgIv;
    private LinearLayout mLight_mode, mLight_param, shouye, mLight_video;
    private RelativeLayout mContainer;
    private TextView mParametv;// 参数

    private int[] images = new int[] {R.mipmap.yiyuan01, R.mipmap.yiyuan02, R.mipmap.yiyuan03, R.mipmap.yiyuan04, R.mipmap.yiyuan05};
    private Bitmap mBitmap;

    private int index = 0;// 全局索引
    private boolean isFullScreen;// 是否全屏

    private Intent mIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hospital);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        shouye = (LinearLayout) findViewById(R.id.shouye);
        mLight_mode = (LinearLayout) findViewById(R.id.light_mode);
        mSceneBgIv = (ImageView) findViewById(R.id.sceneBgIv);
        mLight_param = (LinearLayout) findViewById(R.id.light_param);
        mParametv = (TextView) findViewById(R.id.parametv);
        mContainer = (RelativeLayout) findViewById(R.id.diyContainer);
        mLight_video = (LinearLayout) findViewById(R.id.light_video);

        shouye.setOnClickListener(this);
        mSceneBgIv.setOnClickListener(this);
        mLight_mode.setOnClickListener(this);
        mLight_param.setOnClickListener(this);
        mParametv.setOnClickListener(this);
        mLight_video.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mSceneBgIv) {// 背景图片
            if (!isFullScreen) {
                mContainer.setVisibility(View.INVISIBLE);
                isFullScreen = true;
            } else {
                mContainer.setVisibility(View.VISIBLE);
                isFullScreen = false;
            }
        } else if (view == mLight_param) {// 灯光参数
            mIntent = new Intent(HospitalActivity.this, LightParamActivity.class);
            startActivity(mIntent);
        } else if (view == mLight_mode) {// 灯光模式
            if (index % 5 == 0) {// 取模 模拟循环的效果
                mBitmap = ImageUtil.getBitmapById(ct, images[1]);
                index++;
            } else if (index % 5 == 1) {
                mBitmap = ImageUtil.getBitmapById(ct, images[2]);
                index++;
            } else if (index % 5 == 2) {
                mBitmap = ImageUtil.getBitmapById(ct, images[3]);
                index++;
            } else if (index % 5 == 3) {
                mBitmap = ImageUtil.getBitmapById(ct, images[4]);
                index++;
            } else if (index % 5 == 4) {
                mBitmap = ImageUtil.getBitmapById(ct, images[0]);
                index = 0;
            }
            mSceneBgIv.setImageBitmap(mBitmap);
        } else if(view == mParametv) {// 最多只能3张图片
            mIntent = new Intent(HospitalActivity.this, ParamterActivity.class);
            startActivity(mIntent);
        } else if (view == shouye) {// 场景视频
            mIntent = new Intent(HospitalActivity.this, HospitalVideoActivity.class);
            startActivity(mIntent);
        } else if (view == mLight_video) {// 筒灯视频
            mIntent = new Intent(HospitalActivity.this, LightVideoActivity.class);
            startActivity(mIntent);
        }
    }


}
