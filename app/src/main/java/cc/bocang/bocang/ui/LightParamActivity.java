package cc.bocang.bocang.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import cc.bocang.bocang.R;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.UIUtils;

/**
 * 灯光参数
 *
 * created by hardawin 2017-12-14
 */
public class LightParamActivity extends BaseActivity {

    private ImageView iv_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_light_param);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        BitmapWorkerTask task = new BitmapWorkerTask(iv_bg);
        task.execute(R.mipmap.kafu_paramter_img);

    }

    public void back(View view) {
        finish();
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // 使用弱引用
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // 在后台线程压缩图片
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            int width= UIUtils.getScreenWidth(LightParamActivity.this);
            int height=width*2808/976;
            return ImageUtil.compressBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), data, width,height),400);
        }

        // 压缩完成后，将图片设置到控件中
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
