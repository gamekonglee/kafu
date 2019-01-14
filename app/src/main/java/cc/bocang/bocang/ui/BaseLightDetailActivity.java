package cc.bocang.bocang.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baiiu.filter.util.UIUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import cc.bocang.bocang.R;
import cc.bocang.bocang.utils.AppUtils;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.view.StickerView;

/**
 * 光源体验
 * created by hardawin 2017-11-27
 */
public class BaseLightDetailActivity extends BaseActivity implements View.OnClickListener {

    private BaseLightDetailActivity ct = this;
    private int num = 0;// 用来确定体验场景以及该场景的灯光效果
    private boolean isTurnOn;// 打开灯槽/关闭灯槽
    private boolean isTongdeng;// 打开筒灯/关闭筒灯

    private ImageView mSceneBgIv, mTongdengIv, mLianduIv;
    private FrameLayout mContainer, mFrameLayout;
    private LinearLayout mSeekbar_ll;
    private ImageView mSixthIv, mFourthIv, mThirdIv, mOpen;// 关灯：open
    private TextView mTongdengTv, mTurnOn;// 灯槽：turn on
    private SeekBar mSeekbar;
    private ProgressBar mPb;

    private int mSeekNum = 50;
    private Bitmap mSceneBg;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base_light_detail);
        initData();
        initView();
        initImageLoader();
        displayDefaultSceneBg();
    }

    private void initData() {
        Intent intent = getIntent();
        num = intent.getIntExtra("base_id", num);
    }

    private void initView() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mSceneBgIv = (ImageView) findViewById(R.id.sceneBgIv);
        mTongdengIv = (ImageView) findViewById(R.id.tongdengIv);
        mLianduIv = (ImageView) findViewById(R.id.lianduIv);

        mContainer = (FrameLayout) findViewById(R.id.container);
        mFrameLayout = (FrameLayout) findViewById(R.id.sceneFrameLayout);
        mSeekbar_ll = (LinearLayout) findViewById(R.id.seekbar_ll);

        mSixthIv = (ImageView) findViewById(R.id.sixthIv);
        mFourthIv = (ImageView) findViewById(R.id.fourthIv);
        mThirdIv = (ImageView) findViewById(R.id.thirdIv);
        mOpen = (ImageView) findViewById(R.id.open);

        mTongdengTv = (TextView) findViewById(R.id.tongdengTv);
        mTurnOn = (TextView) findViewById(R.id.turnOn);

        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        mPb = (ProgressBar) findViewById(R.id.pb);

        mSceneBgIv.setOnClickListener(this);// 隐藏操作栏
        mTongdengIv.setOnClickListener(this);
        mLianduIv.setOnClickListener(this);

        mSixthIv.setOnClickListener(this);
        mFourthIv.setOnClickListener(this);
        mThirdIv.setOnClickListener(this);
        mOpen.setOnClickListener(this);

        mTongdengTv.setOnClickListener(this);
        mTurnOn.setOnClickListener(this);
        isTongdeng=false;

        // 调节亮度
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekNum = progress;
                if (!AppUtils.isEmpty(mSceneBg))
                    ImageUtil.changeLight02(mSceneBgIv, mSceneBg, mSeekNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void back(View v) {
        finish();
    }

//    String path= "drawable://" + R.drawable.image;// 图片路径

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        mSeekbar_ll.setVisibility(View.GONE);
        if (view == mSceneBgIv) {
            if (mCurrentView != null) {
                mCurrentView.setInEdit(false);// 退出贴纸编辑状态
            }
        } else if(view == mSixthIv) {// 白色光
            selectedBg(1);
            if (num == 1) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_1);
            } else if (num == 2) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_2);
            } else if (num == 3) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_3);
            } else if (num == 4) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_4);
            } else if (num == 5) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_5);
            }
            mSceneBgIv.setImageBitmap(mSceneBg);
        } else if(view == mFourthIv) {// 淡黄色
            selectedBg(2);
            if (num == 1) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellowy_1);
            } else if (num == 2) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellowy_2);
            } else if (num == 3) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellowy_3);
            } else if (num == 4) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellowy_4);
            } else if (num == 5) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellowy_5);
            }
            mSceneBgIv.setImageBitmap(mSceneBg);
        } else if(view == mThirdIv) {// 黄色
            selectedBg(3);
            if (num == 1) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellow_1);
            } else if (num == 2) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellow_2);
            } else if (num == 3) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellow_3);
            } else if (num == 4) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellow_4);
            } else if (num == 5) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.yellow_5);
            }
            mSceneBgIv.setImageBitmap(mSceneBg);
        } else if (view == mOpen) {// 关灯
            selectedBg(4);
            if (num == 1) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.close_1);
            } else if (num == 2) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.close_2);
            } else if (num == 3) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.close_3);
            } else if (num == 4) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.close_4);
            } else if (num == 5) {
                mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.close_5);
            }
            mSceneBgIv.setImageBitmap(mSceneBg);
        } else if(view == mLianduIv) {// 亮度
            selectedBg(0);
            mSeekbar_ll.setVisibility(View.VISIBLE);
            return;
        } else if (view == mTongdengTv) {// 是否打开筒灯
            if (!isTongdeng) {
                mTongdengTv.setText("关闭筒灯");
                isTongdeng = true;
            } else {
                mTongdengTv.setText("打开筒灯");
                isTongdeng = false;
            }
            int res;
            if (isTongdeng) {
//                mBitmap = resizeBitMapImage1(R.drawable.open_light,UIUtil.dp(this,150),UIUtil.dp(this,250));
                mBitmap = ( (BitmapDrawable) getResources().getDrawable(R.drawable.open_light)).getBitmap();
                res=R.drawable.open_light;

            } else {
//                mBitmap = resizeBitMapImage1(R.drawable.close_light,UIUtil.dp(this,150),UIUtil.dp(this,250));
                mBitmap = ( (BitmapDrawable) getResources().getDrawable(R.drawable.close_lights_new)).getBitmap();
                res=R.drawable.close_lights_new;
            }
//            mBitmap.setWidth(UIUtil.dp(this,150));
//            mBitmap.setHeight(UIUtil.dp(this,250));
            if(mViews==null||mViews.size()<=0)return;
            for(int i=0;i<mViews.size();i++){

                ((StickerView)mViews.get(i)).setBitmapBySize(mBitmap,UIUtil.dp(this,150),UIUtil.dp(this,250),res);
//                ((StickerView)mViews.get(i)).setLayoutParams(new FrameLayout.LayoutParams(UIUtil.dp(this,150),UIUtil.dp(this,250)));
                ((StickerView)mViews.get(i)).setInEdit(false);
            }
//            mFrameLayout.requestLayout();
//            for(int j=0;j<mFrameLayout.getChildCount();j++){
//                View temp=mFrameLayout.getChildAt(j);
//                if(temp.getTag().equals(mLightNumber)){
//                    ((StickerView)temp).setBitmap(mBitmap);
//                }
//            }
        } else if (view == mTurnOn) {// 是否打开灯槽
            selectedBg(0);
            if (isTurnOn) {
                if (num == 1) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_1);
                } else if (num == 2) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_2);
                } else if (num == 3) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_3);
                } else if (num == 4) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_4);
                } else if (num == 5) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_5);
                }
                mContainer.setVisibility(View.VISIBLE);// 打开筒灯布局
                mSceneBgIv.setImageBitmap(mSceneBg);
                mTurnOn.setText("关闭灯槽");
                isTongdeng=false;
                isTurnOn = false;
            } else {
                if (num == 1) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.turnoff_1);
                } else if (num == 2) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.turnoff_2);
                } else if (num == 3) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.turnoff_3);
                } else if (num == 4) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.turnoff_4);
                } else if (num == 5) {
                    mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.turnoff_5);
                }
                mContainer.setVisibility(View.GONE);// 隐藏筒灯布局
                mSceneBgIv.setImageBitmap(mSceneBg);
                mTurnOn.setText("打开灯槽");
                isTongdeng=true;
                isTurnOn = true;
            }
        } else if (view == mTongdengIv) {
//            if (!isTongdeng) {
////                mBitmap = resizeBitMapImage1(R.drawable.open_light,UIUtil.dp(this,150),UIUtil.dp(this,250));
//
//            } else {
////                mBitmap = resizeBitMapImage1(R.drawable.close_light,UIUtil.dp(this,150),UIUtil.dp(this,250));
//                mBitmap = ( (BitmapDrawable) getResources().getDrawable(R.drawable.close_lights_new)).getBitmap();
//                displayCheckedGoods(mBitmap,R.drawable.close_lights_new);
//            }
            mBitmap = ( (BitmapDrawable) getResources().getDrawable(R.drawable.open_light)).getBitmap();

            displayCheckedGoods(mBitmap,R.drawable.open_light);
        }
    }

    /**
     * 切换选中状态
     */
    public void selectedBg(int position) {
        if (position == 1) {
            mSixthIv.setImageResource(R.drawable.white_current);
            mFourthIv.setImageResource(R.drawable.yellowy);
            mThirdIv.setImageResource(R.drawable.yellow);
            mOpen.setImageResource(R.drawable.turnoff);
        } else if (position == 2) {
            mFourthIv.setImageResource(R.drawable.yellowy_current);
            mSixthIv.setImageResource(R.drawable.white);
            mThirdIv.setImageResource(R.drawable.yellow);
            mOpen.setImageResource(R.drawable.turnoff);
        } else if (position == 3) {
            mThirdIv.setImageResource(R.drawable.yellow_current);
            mSixthIv.setImageResource(R.drawable.white);
            mFourthIv.setImageResource(R.drawable.yellowy);
            mOpen.setImageResource(R.drawable.turnoff);
        } else if (position == 4) {
            mOpen.setImageResource(R.drawable.turnoff_current);
            mSixthIv.setImageResource(R.drawable.white);
            mFourthIv.setImageResource(R.drawable.yellowy);
            mThirdIv.setImageResource(R.drawable.yellow);
        } else {
            mSixthIv.setImageResource(R.drawable.white);
            mFourthIv.setImageResource(R.drawable.yellowy);
            mThirdIv.setImageResource(R.drawable.yellow);
            mOpen.setImageResource(R.drawable.turnoff);
        }
    }

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private int mScreenWidth;
    private int leftMargin;

    /**
     * 显示筒灯（关灯或开灯）
     */
    private void displayCheckedGoods(Bitmap bitmap,int res) {
        addStickerView(bitmap,res);// 方案1，下面是方案2

//        String pic_url;
//        if (!isTongdeng) {
//            pic_url = "drawable://" + R.drawable.open_light;
//        } else {
//            pic_url = "drawable://" + R.drawable.close_light;
//        }
//        imageLoader.loadImage(pic_url, options,
//                new SimpleImageLoadingListener() {
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//
//                        // 被点击的灯的编号加1
//                        mLightNumber++;
//
//                        // 设置灯图的ImageView的初始宽高和位置
//                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                                mScreenWidth / 3 * 2 / 3,
//                                (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
//                        // 设置灯点击出来的位置
//                        leftMargin = mScreenWidth / 3 * 2 / 3 * 2;// mScreenWidth / 3 * 2 / 3
//
//                        lp.setMargins(leftMargin, 0, 0, 0);
//
//                        TouchView touchView = new TouchView(ct);
//                        touchView.setLayoutParams(lp);
//                        touchView.setImageBitmap(loadedImage);// 设置被点击的灯的图片
//                        touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
//                        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
//                                FrameLayout.LayoutParams.MATCH_PARENT,
//                                FrameLayout.LayoutParams.MATCH_PARENT);
//                        FrameLayout newFrameLayout = new FrameLayout(ct);
//                        newFrameLayout.setLayoutParams(newLp);
//                        newFrameLayout.addView(touchView);
//                        mFrameLayout.addView(newFrameLayout);
//                        touchView.setContainer(mFrameLayout, newFrameLayout);
//                    }
//                });
    }

    /**
     * 加载默认光源体验场景
     */
    private void displayDefaultSceneBg() {
        if (num == 1){
            mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_1);
        } else if (num == 2) {
            mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_2);
        }  else if (num == 3) {
            mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_3);
        }  else if (num == 4) {
            mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_4);
        }  else if (num == 5) {
            mSceneBg = ImageUtil.getBitmapById(ct, R.mipmap.white_5);
        }
        mSceneBgIv.setImageBitmap(mSceneBg);
    }

    //当前处于编辑状态的贴纸
    private StickerView mCurrentView;
    //存储贴纸列表
    private ArrayList<View> mViews = new ArrayList<>();

    private int mLightNumber = -1;// 点出来的灯的编号

    /** 添加表情 */
    private void addStickerView(Bitmap bitmap,int res) {

        final StickerView stickerView = new StickerView(ct);
//        stickerView.setScaleType(ImageView.ScaleType.CENTER);
        stickerView.setBitmap(bitmap);
//        stickerView.setBackgroundResource(res);
        stickerView.mLightCount = mLightNumber;
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mViews.remove(stickerView);
                mFrameLayout.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;// 这里作出替换
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }
        });
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(UIUtil.dp(this,150),UIUtil.dp(this,250));
        stickerView.setLayoutParams(lp2);
        stickerView.setTag(mLightNumber);
        mFrameLayout.addView(stickerView);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
    }

    /**
     * 设置当前处于编辑模式的贴纸
     */
    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    private void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                // 设置图片下载期间显示的图片
                .showImageOnLoading(R.mipmap.bg_default)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.bg_default)
                        // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnFail(R.mipmap.bg_default)
                        // 设置下载的图片是否缓存在内存中
                .cacheInMemory(false)
                        //设置图片的质量
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                        // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                        //                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                        // 图片加载好后渐入的动画时间
                        // .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成

        // 得到ImageLoader的实例(使用的单例模式)
        imageLoader = ImageLoader.getInstance();
    }
    public static Bitmap resizeBitMapImage1(int id, int targetWidth,
                                            int targetHeight) {
        Bitmap bitMapImage = null;
        // First, get the dimensions of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
        BitmapFactory.decodeResource(Resources.getSystem(),id,options);
        double sampleSize = 0;
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
                .abs(options.outWidth - targetWidth);
        if (options.outHeight * options.outWidth * 2 >= 1638) {
            // Load, scaling to smallest power of 2 that'll get it <= desired
            // dimensions
            sampleSize = scaleByHeight ? options.outHeight / targetHeight
                    : options.outWidth / targetWidth;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitMapImage = BitmapFactory.decodeResource(Resources.getSystem(),id,options);
                break;
            } catch (Exception ex) {
                try {
                    sampleSize = sampleSize * 2;
                } catch (Exception ex1) {
                }
            }
        }
        return bitMapImage;
    }
}
