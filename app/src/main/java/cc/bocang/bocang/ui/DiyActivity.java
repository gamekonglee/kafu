package cc.bocang.bocang.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.api.OtherApi;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.GoodsAttr;
import cc.bocang.bocang.data.model.GoodsClass;
import cc.bocang.bocang.data.model.GoodsPro;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.model.SceneAllAttr;
import cc.bocang.bocang.data.model.SceneAttr;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.parser.ParseGetSceneListResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.data.response.GetSceneListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.AppUtils;
import cc.bocang.bocang.utils.FileUtil;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.LoadingDailog;
import cc.bocang.bocang.utils.PermissionUtils;
import cc.bocang.bocang.utils.ShareUtil;
import cc.bocang.bocang.utils.UIUtils;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;
import cc.bocang.bocang.view.SceneParamePopWindow;
import cc.bocang.bocang.view.StickerView;
import cc.bocang.bocang.view.StickerView2;
import cc.bocang.bocang.view.TouchView02;
import it.sephiroth.android.library.picasso.Picasso;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DiyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = DiyActivity.class.getSimpleName();
    private DiyActivity ct = this;

    private final int PHOTO_WITH_DATA = 1; // 从SD卡中得到图片
    private final int PHOTO_WITH_CAMERA = 2;// 拍摄照片

    private String photoName;// 拍照保存的相片名称（不包含后缀名）
    private File cameraPath;// 拍照保存的相片路径

    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private HDApiService apiService;
    private boolean displayFirstScene;
    private UserInfo mInfo;
    private LoadingDailog mLodingDailog;
    private String screePath;
    private boolean isShare = false;
    private boolean isCreate = true;
    private int goodsclassId;
    private Network mNetwork;
    private String mSceneDesc;
    private SeekBar seekbar;
    private LinearLayout seekbar_ll;
    private String sceneTitle;
    private LinearLayout ll_cart;
    private List<Goods> goodsList=new ArrayList<>();
    private SeekBar seekBar2;
    private LinearLayout seekbar_ll2;
    private LinearLayout ll_night_liangdu;
    private Bitmap mCurrentBitmap;
    private int mCurrentNumber;
    private int nightlevel;
    private int curentLevel;
    private float lastx;
    private float lasty;
    private float oldDis;
    private boolean isPointerDown;
    private float lastLength;
    private boolean toRefresh;
    private LinearLayout ll_canshu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_diy);
        mNetwork = new Network();
        if (isCreate) {
            apiService = HDRetrofit.create(HDApiService.class);
            mInfo = ((MyApplication) getApplication()).mUserInfo;
            Intent intent = getIntent();
            initView();
            initImageLoader();
            mLodingDailog = new LoadingDailog(this, R.style.CustomDialog);
            String from = intent.getStringExtra("from");
            if ("scene".equals(from)) {
                String path = intent.getStringExtra("path");
                mSceneDesc = intent.getStringExtra("scene_desc");
                sceneTitle = intent.getStringExtra("title");
//                displaySceneBg(Constant.SCENE_URL + path);
                if (sceneTitle.equals("混合空间")){
                    displaySceneBg(Constant.SCENE_URL2 + path);
                } else {
                    displaySceneBg(Constant.SCENE_URL + path);
                }
                mProIv.performClick();
            } else if ("goods".equals(from)) {
                displayFirstScene = true;
                mSceneIv.performClick();
                Goods temp=(Goods) intent.getSerializableExtra("goods");
                displayCheckedGoods(temp);
            }
        }

        Log.v("520it", "出发到A" + isCreate);

    }

    private boolean isFullScreen;
    private int mSelectedTab;
    private int page = 1;

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        if (mSelectedTab == 1) {
            callGoodsClass(false);
            //callGoodsListItem(apiService, 76, page);
            callCateGoodsList(apiService, 76, 1, page);
            //callGoodsList(apiService, 0, page, null, null, fitterStr);

        } else if (mSelectedTab == 2)
            callSceneList(apiService, 0, page, null, null, fitterStr);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        if (mSelectedTab == 1) {
            callGoodsList(apiService, IndexActivity.mCId, ++page, null, null, fitterStr);
            //callGoodsListItem(apiService, 76, ++page);
            callCateGoodsList(apiService, 76, 1, ++page);
            //callGoodsClass(apiService);
        } else if (mSelectedTab == 2)
            callSceneList(apiService, 0, ++page, null, null, fitterStr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                takePhoto();
            }
        });
    }

    @Override
    public void onClick(View view) {
        seekbar_ll.setVisibility(View.GONE);
        seekbar_ll2.setVisibility(View.GONE);
        if (view == mFrameLayout) {
            if (!isFullScreen) {
                mDiyContainerRl.setVisibility(View.INVISIBLE);
                if (mCurrentView != null) {
                    mCurrentView.setInEdit(false);// 取消编辑状态
                }
                for(int i=0;i<mFrameLayout.getChildCount();i++){
                    View childView=mFrameLayout.getChildAt(i);
                    if(childView.getTag()!=null&&childView.getTag().equals("param")){
                        childView.setVisibility(View.INVISIBLE);
                    }
                }
                isFullScreen = true;
            } else {
                mDiyContainerRl.setVisibility(View.VISIBLE);

                for(int i=0;i<mFrameLayout.getChildCount();i++){
                    View childView=mFrameLayout.getChildAt(i);
                    if(childView.getTag()!=null&&childView.getTag().equals("param")){
                        childView.setVisibility(View.VISIBLE);
                    }
                }
                isFullScreen = false;
            }
        } else if (view == mProIv) {
            setTabBg(mProIv);
            mListViewAdapter.setSelection(0);
            page = 1;
            callGoodsClass(true);

            mSelectedTab = 1;
            mOtherRl.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else if (view == mSceneIv) {
            setTabBg(mSceneIv);
            mListViewAdapter.setSelection(0);
            page = 1;
            callSceneList(apiService, 0, 1, null, null, "0.0.0");
            mSelectedTab = 2;
            mOtherRl.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else if (view == mOtherIv) {
            setTabBg(mOtherIv);
            mOtherRl.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else if (view == mCameraIv) {
            PermissionUtils.requestPermission(DiyActivity.this, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    takePhoto();
                }
            });
        } else if (view == mAlbumIv) {
            pickPhoto();
        } else if (view == lianduIv) {//亮度
            seekbar_ll.setVisibility(View.VISIBLE);
            return;
        } else if(view==ll_night_liangdu){
            seekbar_ll2.setVisibility(View.VISIBLE);
        }else {
            if (view == mShareIv) {//分享
                if (isShare == true)
                    return;
                isShare = true;
                //                产品ID的累加
                StringBuffer goodsid = new StringBuffer();
                for (int i = 0; i < mSelectedLightSA.size(); i++) {
                    goodsid.append(mSelectedLightSA.get(i).getId() + "");
                    if (i < mSelectedLightSA.size() - 1) {
                        goodsid.append(",");
                    }
                }

                mDiyContainerRl.setVisibility(View.INVISIBLE);
                Log.v("520", "前时间：" + System.currentTimeMillis());
                //截图
                final Bitmap imageData = ImageUtil.takeScreenShot(this);
                mDiyContainerRl.setVisibility(View.VISIBLE);
                mLodingDailog.show();
                Log.v("520", "后时间：" + System.currentTimeMillis());
                final String url = Constant.SUBMITPLAN;//地址
                final Map<String, String> params = new HashMap<String, String>();
                params.put("goods_id", goodsid.toString());
                params.put("phone", "android");
                params.put("title", "share");
                params.put("user_id", mInfo.getId() + "");
                params.put("village", "unknown");

                final String imageName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".png";
                new Thread(new Runnable() { //开启线程上传文件
                    @Override
                    public void run() {
                        final String resultJson = uploadFile(imageData, url, params, imageName);
                        final Result result = JSON.parseObject(resultJson, Result.class);
                        Log.v("520", "上传时间：" + System.currentTimeMillis());
                        //分享的操作
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLodingDailog.dismiss();
                                Log.v("520it", "分享成功!");
                                if (TextUtils.isEmpty(result.getResult()) || result.getResult() == "0") {
                                    return;
                                }
                                showShare(result.getResult(), Constant.SHAPE_SCEEN + result.getPath());
                            }
                        });

                    }
                }).start();
                //                showShare("84");
                isShare = false;

            } else if (view == ll_canshu) {//参数
                if(mCurrentNumber>=goodsList.size()){
                        Toast.makeText(this,"请选择产品",Toast.LENGTH_LONG);
                        return;
                }
                    Goods goods=goodsList.get(mCurrentNumber);
                StringBuffer sbf=new StringBuffer();
                sbf.append("名称："+goods.getName()+"\n");
                sbf.append("价格："+goods.getShop_price()+"\n");
                for(GoodsPro temp:goods.getGoodsPros()){
                    sbf.append(temp.getName()+":"+temp.getValue()+"\n");
                }
                String msg=sbf.toString();
                addImageView(ImageUtil.textAsBitmap(msg),mCurrentNumber);
//                if (TextUtils.isEmpty(mSceneDesc)){
//                    Toast.makeText(this,"该场景没有相关参数!",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SceneParamePopWindow popWindow = new SceneParamePopWindow(this);
//                popWindow.getWebViewData(mSceneDesc);
//                popWindow.onShow(mDiyContainerRl);
            }else if(view==ll_cart){
                //加入购物车
                CartDao dao = new CartDao(ct);
                if(goodsList==null||goodsList.size()==0)return;
                for(Goods temp:goodsList){
                if (-1 != dao.replaceOne(temp)) {
                    Toast.makeText(ct, "已添加到购物车", Toast.LENGTH_LONG).show();
                    Broad.sendLocalBroadcast(ct, Broad.CART_CHANGE_ACTION, null);//发送广播
                }
                }
            }
        }

    }

    /**
     * 添加图片
     *
     * @param bitmap
     */
    private void addImageView(Bitmap bitmap,int number) {
        pd2.setVisibility(View.GONE);
        // 被点击的灯的编号加1
//        mLightNumber++;
        MyApplication.mParamIndex = number;
        // 设置灯图的ImageView的初始宽高和位置
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                mScreenWidth / 3 * 2 / 3,
                (mScreenWidth / 3 * 2 / 3 * bitmap.getHeight()) / bitmap.getWidth());
        // 设置灯点击出来的位置
        if (mSelectedLightSA.size() == 1) {
            leftMargin = mScreenWidth / 3 * 2 / 3;
        } else if (mSelectedLightSA.size() == 2) {
            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
        } else if (mSelectedLightSA.size() == 3) {
            leftMargin = 0;
        }
        lp.setMargins(mScreenWidth / 2 - (mScreenWidth / 3 * 2 / 6), 20, 0, 0);


        TouchView02 touchView = new TouchView02(this);
        touchView.setLayoutParams(lp);
        touchView.setImageBitmap(bitmap);// 设置被点击的灯的图片
        touchView.setmLightCount(number);// 设置被点击的灯的编号
        touchView.setTag(number);
        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        FrameLayout newFrameLayout = new FrameLayout(this);
        newFrameLayout.setLayoutParams(newLp);
        newFrameLayout.addView(touchView);
        newFrameLayout.setTag("param");
        mFrameLayout.addView(newFrameLayout);

        touchView.setContainer(mFrameLayout, newFrameLayout);
    }

    /**
     * 截屏
     *
     * @param v 视图
     */
    private Bitmap getScreenHot(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        v.draw(canvas);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
    }

    //    /**
    //     * 提交方案
    //     * @param apiService
    //     * @param goodsid
    //     * @param phone
    //     * @param title
    //     * @param userID
    //     * @param village
    //     * @param file
    //     */
    //    private void getSubmitPlan(HDApiService  apiService,String goodsid,String phone,String title,String userID,String village, Image file) {
    //        Call<ResponseBody> call = apiService.submitPlan(file,goodsid,phone,title,userID,village);
    //        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
    //            @Override
    //            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
    //                if (null == DiyActivity.this || DiyActivity.this.isFinishing())
    //                    return;
    //
    //                try {
    //                    String json = response.body().string();
    //                    Result result= JSON.parseObject(json,Result.class);
    //                    Log.v("520it","result"+result.getResult());
    //                    if(result.getResult().equals("0")){
    //                        tip("上传方案失败!");
    //                    }else{
    //                        //分享操作
    ////                        showShare(result.getResult());
    //
    //                        tip("上传成功!");
    //
    //                    }
    //                } catch (Exception e) {
    //                    if (null != DiyActivity.this && ! DiyActivity.this.isFinishing())
    //                        tip("上传方案失败!");
    //                    e.printStackTrace();
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Throwable t) {
    //                if (null ==  DiyActivity.this ||  DiyActivity.this.isFinishing())
    //                    return;
    //                tip("上传方案失败");
    //            }
    //        });
    //    }


    /**
     * 分享操作
     */
    private void showShare(final String id,final String sceenpath) {

        if(TextUtils.isEmpty(id)){
            return;
        }
        final Dialog dialog=UIUtils.showBottomInDialog(this,R.layout.share_dialog,UIUtils.dip2PX(150));
        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx=dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq=dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq=dialog.findViewById(R.id.ll_qq);
        final String mGoodsname="来自"+getString(R.string.app_name)+"配灯的分享";
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final String url=Constant.SHAREPLAN+"id="+id;
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(DiyActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(DiyActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQ(DiyActivity.this, mGoodsname, url,sceenpath);
                dialog.dismiss();
            }
        });
    }



    private String fitterStr;


    /**
     * 弹出提示
     */
    private void showInstallDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入密码查看此分类!").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String urlPath = Constant.SETUPPROUCT + inputServer.getText().toString();
                        String json = OtherApi.doGet(urlPath);
                        final Result result = JSON.parseObject(json, Result.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.getCode().equals("1")) {
                                    callGoodsListItem(apiService, goodsclassId, 1);
                                } else {
                                    Toast.makeText(DiyActivity.this, "密码错误，请重新输入!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();

            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("520it", "ffff");
        if (parent == mListView) {
            mListViewAdapter.setSelection(position);

            mListViewAdapter.notifyDataSetChanged();
            if (mSelectedTab == 1) {
                if(goodsTypeList.get(position).getLevel()==2){
                    mListViewAdapter.setChildVisible(goodsTypeList.get(position).getId());
                }
//                if (null != goodsAllAttrs)
                    //fitterStr = goodsAllAttrs.get(0).getGoodsAttrs().get(position).getGoods_id() + ".0.0";
                    //fitterStr = 33 + ".0.0";
//                    Log.v("520it", fitterStr);
                page = 1;
                goodsclassId = goodsTypeList.get(position).getId();
//                Log.v("520it", "goodsclassId:" + goodsclassId);
//                Log.v("520it", "xx:" + IndexActivity.mCId);
                if (TextUtils.isEmpty(IndexActivity.mCId)) {
                    //callGoodsListItem(apiService, goodsclassId, 1);
                    callCateGoodsList(apiService, goodsclassId, 1, 1);
                    return;
                }

                String[] cIdArrys = IndexActivity.mCId.split(",");
//                Log.v("520it", "cIdArrys:" + cIdArrys.toString());
                if (cIdArrys.length > 0) {
                    if (Arrays.binarySearch(cIdArrys, goodsclassId + "") >= 0) {
                        showInstallDialog();
                    }
                } else {
                    //callGoodsListItem(apiService, goodsclassId, 1);
                }
                //callGoodsListItem(apiService, goodsclassId, 1);
            } else if (mSelectedTab == 2) {
                if (null != sceneAllAttrs)
                    fitterStr = sceneAllAttrs.get(0).getSceneAttrs().get(position).getScene_id() + ".0";
                page = 1;
                callSceneList(apiService, 0, 1, null, null, fitterStr);
            }
        } else if (parent == mGridView) {
            if (mSelectedTab == 1) {
                Log.v("520it", "出发到B");
                displayCheckedGoods(goodses.get(position));
            } else if (mSelectedTab == 2) {
                if(TextUtils.isEmpty(scenes.get(position).getScene_desc())){
                    mSceneDesc="";
                }else{
                    mSceneDesc=scenes.get(position).getScene_desc();
                }

                displaySceneBg(Constant.SCENE_URL + scenes.get(position).getPath());
            }
        }
    }

    private List<GoodsAllAttr> goodsAllAttrs;

    private List<Goods> goodses;
    private List<GoodsClass> goodsTypeList = new ArrayList<>();
    private List<GoodsClass> goodsClassList = new ArrayList<>();

    /**
     * 用的是 Network 里面的方法，apiService可以去掉，方法不同
     * @param apiService
     * @param c_id
     * @param page
     * @param keywords
     * @param type
     * @param filter_attr
     */
    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendGoodsList(c_id, page, 1 + "", keywords, type, filter_attr, this, new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == goodsAllAttrs)
                            goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (goodsTypeList.isEmpty())
                            for (GoodsAttr goodsAttr : goodsAllAttrs.get(0).getGoodsAttrs()) {
                                GoodsClass goods=new GoodsClass();
                                goods.setName(goodsAttr.getAttr_value());
                                goodsTypeList.add(goods);
                            }
                        mListViewAdapter.setData(goodsTypeList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }


    /**
     * 根据大类获取下面的产品列表（不管二级或者是三级目录）
     */
    private void callCateGoodsList(HDApiService apiService, int c_id, int down_query, final int page) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getCateGoodsList(c_id, down_query, page);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callGoodsListItem(HDApiService apiService, int c_id, final int page) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getGoodsListItem(c_id, page);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        //                        if (null == goodsAllAttrs)
                        //                            goodsAllAttrs = resp.getGoodsAllAttrs();
                        //                        if (goodsTypeList.isEmpty())
                        //                            for (GoodsAttr goodsAttr : goodsAllAttrs.get(0).getGoodsAttrs()) {
                        //                                goodsTypeList.add(goodsAttr.getAttr_value());
                        //                            }
                        //                        mListViewAdapter.setData(goodsTypeList);
                        //                        mListViewAdapter.notifyDataSetChanged();
                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                //                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取产品列表
     *
     * @param isClick 判断是否是点击按钮触发的事件
     */
    private void callGoodsClass(final boolean isClick) {
        Log.v("520it", "刷新");
        pd.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String json = OtherApi.getAppGoodsClass();
                Log.i(TAG, json);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goodsClassList = JSON.parseArray(
                                json, GoodsClass.class);
                        if (goodsTypeList.isEmpty()) {
                            for (GoodsClass goodsClass : goodsClassList) {
                                if (goodsClass.getLevel()==1&&goodsClass.getId() == 76) {
                                    GoodsClass goods=new GoodsClass();
                                    goods.setName(goodsClass.getName());
                                    goodsTypeList.add(goods);
                                    for(int i=0;i<goodsClassList.size();i++){
                                        if(goodsClassList.get(i).getPid()==76&&goodsClassList.get(i).getLevel()==2){
                                            goodsTypeList.add(goodsClassList.get(i));
                                            for(int j=0;j<goodsClassList.size();j++){
                                                if(goodsClassList.get(j).getLevel()==3&&goodsClassList.get(j).getPid()==goodsTypeList.get(i).getId()){
                                                    goodsTypeList.add(goodsClassList.get(j));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
//                            for (GoodsClass goodsClass : goodsClassList) {
//                                if (goodsClass.getLevel()==2&&goodsClass.getPid()==76) {
//                                    goodsTypeList.add(goodsClass);
//                                    Log.e("76_2",goodsClass.getName()+","+goodsClass.getId());
//                                }
//                            }
//                            for (GoodsClass goodsClass : goodsClassList) {
//                                for(int i=0;i<goodsTypeList.size();i++){
//                                if (goodsClass.getLevel()==3&&goodsClass.getPid()==goodsTypeList.get(i).getId() &&goodsTypeList.get(i).getLevel()==2) {
////                                    Log.e("level",goodsClass.getLevel()+"");
////                                    Log.e("pLevel",goodsClassList.get(i).getLevel()+"");
////                                    Log.e("pid",goodsClass.getPid()+"");
////                                    Log.e("id", goodsClassList.get(i).getId()+"");
////                                    Log.e("ppid", goodsClassList.get(i).getPid()+"");
////                                    Log.e("name",goodsClass.getName());
//                                    goodsTypeList.add(goodsClass);
//                                }
//                                }
//                            }
                        }
                        mListViewAdapter.setData(goodsTypeList);
                        mListViewAdapter.notifyDataSetChanged();
                        if (isClick) {
                            //callGoodsListItem(apiService, 76, 1);
                            callCateGoodsList(apiService, 76, 1, 1);
                        }
                    }
                });
            }
        }).start();

    }


    private List<SceneAllAttr> sceneAllAttrs;
    private List<Scene> scenes;
    private List<GoodsClass> sceneSpaceList = new ArrayList<>();

    private void callSceneList(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr) {
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendSceneList(c_id + "", page, keywords, type, filter_attr, this, new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == sceneAllAttrs)
                            sceneAllAttrs = resp.getSceneAllAttrs();
                        if (sceneSpaceList.isEmpty())
                            for (SceneAttr sceneAttr : sceneAllAttrs.get(0).getSceneAttrs()) {
                            GoodsClass goodsClass=new GoodsClass();
                            goodsClass.setName(sceneAttr.getAttr_value());
                                sceneSpaceList.add(goodsClass);
                            }
                        mListViewAdapter.setData(sceneSpaceList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Scene> sceneList = resp.getScenes();
                        if (1 == page)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        if (displayFirstScene && null != scenes && !scenes.isEmpty()) {//点击产品进来第一次展示背景
                            displaySceneBg(Constant.SCENE_URL + scenes.get(new Random().nextInt(scenes.size())).getPath());
                            if(TextUtils.isEmpty(scenes.get(new Random().nextInt(scenes.size())).getScene_desc())){
                                mSceneDesc="";
                            }else{
                                mSceneDesc=scenes.get(new Random().nextInt(scenes.size())).getScene_desc();
                            }

                            displayFirstScene = false;
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Scene scene : scenes) {
                            names.add(scene.getName());
                            paths.add(scene.getPath());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(2);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                ct.page--;
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    private void setTabBg(ImageView imageView) {
        mProIv.setBackgroundResource(android.R.color.transparent);
        mSceneIv.setBackgroundResource(android.R.color.transparent);
        mOtherIv.setBackgroundResource(android.R.color.transparent);
        imageView.setBackgroundResource(R.color.colorPrimary);
    }

    private int mLightNumber = -1;// 点出来的灯的编号
    public SparseArray<Goods> mSelectedLightSA = new SparseArray<>();// key为自增编号，value为点出来的灯
    private int leftMargin = 0;

    private void displayCheckedGoods(final Goods goods) {
        if (mSelectedLightSA.size() >= 3) {
            Toast.makeText(DiyActivity.this, "调出灯超数，长按可删除", Toast.LENGTH_LONG).show();
            return;
        }
        goodsList.add(goods);
        imageLoader.loadImage(Constant.PRODUCT_URL + goods.getImg_url() + "!400X400.png", options,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);

                        // 被点击的灯的编号加1
                        mLightNumber++;
//                        mLightNumber=mViews.size();
                        // 把点击的灯放到集合里
                        mSelectedLightSA.put(mLightNumber, goods);
                        // 设置灯图的ImageView的初始宽高和位置
//                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                                mScreenWidth / 3 * 2 / 3,
//                                (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
                        // 设置灯点击出来的位置
//                        if (mSelectedLightSA.size() == 1) {
//                            leftMargin = mScreenWidth / 3 * 2 / 3;
//                        } else if (mSelectedLightSA.size() == 2) {
//                            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
//                        } else if (mSelectedLightSA.size() == 3) {
//                            leftMargin = 0;
//                        }
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
                        addStickerView(loadedImage);
                    }

                });
    }

    //当前处于编辑状态的贴纸
    private StickerView mCurrentView;
    //存储贴纸列表
    private ArrayList<StickerView> mViews = new ArrayList<>();
    List<Bitmap> bitmaps=new ArrayList<>();
    List<Integer> progresses=new ArrayList<>();
    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.2f;
    //手指移动距离必须超过这个数值

    private final float pointerLimitDis = 20f;
    private final float pointerZoomCoeff = 0.09f;
    /** 添加表情 */
    @SuppressLint("ClickableViewAccessibility")
    private void addStickerView(final Bitmap bitmap) {
        final StickerView stickerView=new StickerView(ct);
        stickerView.setBitmap(bitmap);
//        final View view=View.inflate(this,R.layout.stickerview,null);
//        final StickerView2 stickerView =  view.findViewById(R.id.sv);
//        ImageView iv_delete = view.findViewById(R.id.iv_delete);
//        final ImageView iv_flip = view.findViewById(R.id.iv_flip);
//        final ImageView iv_resize = view.findViewById(R.id.iv_resize);
//        ImageView iv_top = view.findViewById(R.id.iv_top);
//        ImageView imageView = view.findViewById(R.id.iv_bitmap);
//        iv_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        iv_flip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        iv_top.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        imageView.setImageBitmap(bitmap);
//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = MotionEventCompat.getActionMasked(event);
//        boolean handled = true;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                lastx = event.getX(0);
//                lasty = event.getY(0);
//                Constant.mLightIndex = mLightNumber;// TODO
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                if (spacing(event) > pointerLimitDis) {
//                    oldDis = spacing(event);
//                    isPointerDown = true;
//                    midPointToStartPoint(event);
//                } else {
//                    isPointerDown = false;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isPointerDown) {
//                    float scale;
//                    float disNew = spacing(event);
//                    if (disNew == 0 || disNew < pointerLimitDis) {
//                        scale = 1;
//                    } else {
//                        scale = disNew / oldDis;
//                        //缩放缓慢
//                        scale = (scale - 1) * pointerZoomCoeff + 1;
//                    }
//                    float scaleTemp = (scale * Math.abs(iv_flip.getLeft() - iv_resize.getLeft())) / 200;
//                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
//                            (scaleTemp >= MAX_SCALE) && scale > 1) {
//                        scale = 1;
//                    } else {
//                        lastLength = diagonalLength(event);
//                    }
////                                matrix.postScale(scale, scale, mid.x, mid.y);
//                    view.setScaleX(scale);
//                    view.setScaleY(scale);
////                                invalidate();
//                    Log.e("move","isPosionterDown");
//                }else {
//                    float x = event.getX(0);
//                    float y = event.getY(0);
//                    //TODO 移动区域判断 不能超出屏幕
////                    view.setTranslationX(x- lastx);
////                    view.setTranslationY(y- lasty);
////                    view.layout((((int)(view.getLeft()+x))),((int)(view.getTop()+y)),((int)(view.getRight()+x)),((int)(v.getBottom()+y)));
//                    view.layout((int)x,(int)y,(int)(x+view.getWidth()),(int)(y+view.getHeight()));
////                                matrix.postTranslate(x - lastX, y - lastY);
//                    lastx = x;
//                    lasty = y;
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                isPointerDown = false;
//                break;
//
//        }
//        if (handled && operationListener != null) {
//            operationListener.onEdit(StickerView2.this);
//        }
//        return handled;
//    }
//});
//        stickerView.setBitmap(bitmap);
//        ((ImageView)stickerView.findViewById(R.id.iv_bitmap)).setImageBitmap(bitmap);
        stickerView.mLightCount = mLightNumber;
        bitmaps.add(bitmap);
        progresses.add(63);
        mCurrentBitmap=bitmap;
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                Log.e("mLightCount",stickerView.mLightCount+"");
                mViews.remove(stickerView);
                bitmaps.get(stickerView.mLightCount).recycle();
                bitmaps.remove(stickerView.mLightCount);
                progresses.remove(stickerView.mLightCount);
                mFrameLayout.removeView(stickerView);
                ct.mSelectedLightSA.remove(Constant.mLightIndex);
                goodsList.remove(stickerView.mLightCount);
                mLightNumber--;
                for(int i=stickerView.mLightCount ;i<mViews.size();i++){
                    int temp= (int) mViews.get(i).getTag();
                    temp--;
                    mViews.get(i).setTag(temp);
                    ((StickerView)mViews.get(i)).mLightCount=temp;
                }
                if(mViews.size()>0){
                    ((StickerView)mViews.get(0)).setInEdit(true);
                }else {

                }
                toRefresh = true;
            }

            @Override
            public void onEdit(StickerView stickerView) {
                for(int i=0;i<mViews.size();i++){
                    if(mViews.get(i).mLightCount!=stickerView.mLightCount){
                        mViews.get(i).setInEdit(false);
                    }
                }
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;// 这里是新旧交换
                mCurrentView.setInEdit(true);
                mCurrentView.setDrawingCacheEnabled(true);
//                mCurrentBitmap = mCurrentView.getDrawingCache();
                int tag=(int)stickerView.getTag();
                Log.e("tag",tag+"");
                if(tag<bitmaps.size()){
//                    bitmaps.get(tag).recycle();
                    bitmaps.set(tag,null);
//                    System.gc();
                    bitmaps.set(tag,stickerView.getBitmap());
                }
                mCurrentView.isInEdit=true;
//                mCurrentView.setDrawingCacheEnabled(false);
                mCurrentNumber = stickerView.mLightCount;
                if(mCurrentNumber>=progresses.size()){
//                    progresses.add(64);

                    mCurrentNumber=0;
                }
                if(progresses.size()==0)return;
                seekBar2.setProgress(progresses.get(mCurrentNumber));
                if(toRefresh){
                    toRefresh=false;
                    Log.e("refresh",mCurrentNumber+"");
                    stickerView.refresh(true);
                }
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                Bitmap temp=bitmaps.remove(position);
                if(position<progresses.size())
                {
                    int tempProgress=progresses.remove(position);
                    progresses.add(progresses.size(),tempProgress);
                }
                bitmaps.add(bitmaps.size(),temp);
                mViews.add(mViews.size(), stickerTemp);

            }
        });
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        stickerView.setTag(mLightNumber);
        mFrameLayout.addView(stickerView, lp2);
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
        mCurrentNumber= (int) stickerView.getTag();
        stickerView.setInEdit(true);
    }

    /**
     * 加载场景背景图
     *
     * @param path 场景img_url
     */
    private void displaySceneBg(String path) {
        screePath = path;
        imageLoader.displayImage(path, mSceneBgIv, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        Toast.makeText(DiyActivity.this, failReason.getCause() + "请重试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        pd2.setVisibility(View.GONE);
                        // 若场景图片为空，则循环输出场景
                        if (!AppUtils.isEmpty(mSceneBg))
                            mSceneBg.recycle();
                        mSceneBg = ImageUtil.drawable2Bitmap(mSceneBgIv.getDrawable());
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
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

    /**
     * 拍照获取相片
     **/
    private void takePhoto() {
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        photoName = format.format(date);
        cameraPath = FileUtil.getOwnFilesDir(this, Constant.CAMERA_PATH);

        Uri imageUri = Uri.fromFile(new File(cameraPath, photoName + ".jpg"));
        System.out.println("imageUri" + imageUri.toString());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
        // 指定照片保存路径（SD卡）
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, PHOTO_WITH_CAMERA); // 用户点击了从相机获取
    }

    /**
     * 从相册获取图片
     **/
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); // 使用Intent.ACTION_PICK这个Action则是直接打开系统图库

        startActivityForResult(intent, PHOTO_WITH_DATA); // 取得相片后返回到本画面
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) { // 返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡

                        File imageFile = new File(cameraPath, photoName + ".jpg");

                        if (imageFile.exists()) {
                            String imagePath = "file://" + imageFile.toString();

                            displaySceneBg(imagePath);
                            mSceneDesc="";
                        } else {
                            Toast.makeText(this, "读取图片失败！", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    displaySceneBg(originalUri.toString());
                    mSceneDesc="";
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void back(View v) {
        finish();
    }

    public void close(View v) {
        mDiyContainerRl.setVisibility(View.INVISIBLE);
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);// 取消编辑状态
        }
        isFullScreen = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private int mScreenWidth;
    private ProgressBar pd, pd2;
    private ImageView mSceneBgIv;
    private TextView parametv;
    private FrameLayout mFrameLayout, mDiyGridViewContainer;
    private RelativeLayout mDiyContainerRl, mDiyMenuContainerRl, mOtherRl;
    private LinearLayout mDiyTabLl;
    public ImageView mProIv, mSceneIv, mOtherIv, mCameraIv, mAlbumIv, mShareIv, lianduIv;
    private ListView mListView;
    private DiyListViewAdapter mListViewAdapter;
    private GridView mGridView;
    private DiyGridViewAdapter mGridViewAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    private void initView() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mFrameLayout = (FrameLayout) findViewById(R.id.sceneFrameLayout);
        //        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        //        flp.width = mScreenWidth;
        //        flp.height = (int) (mScreenWidth / 4.0f * 3.0f);
        //        mFrameLayout.setLayoutParams(flp);
        mFrameLayout.setOnClickListener(ct);

        pd = (ProgressBar) findViewById(R.id.pd);
        mSceneBgIv = (ImageView) findViewById(R.id.sceneBgIv);
        parametv = (TextView) findViewById(R.id.parametv);
        mDiyContainerRl = (RelativeLayout) findViewById(R.id.diyContainerRl);

        // 三分之一屏幕的RelativeLayout
        mDiyMenuContainerRl = (RelativeLayout) findViewById(R.id.diyMenuContainerRl);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDiyMenuContainerRl.getLayoutParams();
        lp.width = (int) (mScreenWidth / 2.7f);
        mDiyMenuContainerRl.setLayoutParams(lp);

        mDiyTabLl = (LinearLayout) findViewById(R.id.diyTabLl);
        lp = (RelativeLayout.LayoutParams) mDiyTabLl.getLayoutParams();
        lp.height = (int) (80.0f / 1200.0f * mScreenWidth);
        mDiyTabLl.setLayoutParams(lp);

        mProIv = (ImageView) findViewById(R.id.diyProIv);
        mSceneIv = (ImageView) findViewById(R.id.diySceneIv);
        mOtherIv = (ImageView) findViewById(R.id.diyOtherIv);
        mCameraIv = (ImageView) findViewById(R.id.cameraIv);
        mAlbumIv = (ImageView) findViewById(R.id.albumIv);
        mShareIv = (ImageView) findViewById(R.id.diyShareIv);
        lianduIv = (ImageView) findViewById(R.id.lianduIv);
        ll_night_liangdu = (LinearLayout) findViewById(R.id.ll_night_liangdu);
        seekbar_ll = (LinearLayout)findViewById(R.id.seekbar_ll);
        seekbar_ll2 = (LinearLayout) findViewById(R.id.seekbar_ll2);
        ll_cart = (LinearLayout) findViewById(R.id.ll_cart);
        ll_canshu = (LinearLayout) findViewById(R.id.ll_canshu);
        mProIv.setOnClickListener(this);
        mSceneIv.setOnClickListener(this);
        mOtherIv.setOnClickListener(this);
        mCameraIv.setOnClickListener(this);
        mAlbumIv.setOnClickListener(this);
        mShareIv.setOnClickListener(this);
        parametv.setOnClickListener(this);
        lianduIv.setOnClickListener(this);
        ll_night_liangdu.setOnClickListener(this);
        ll_cart.setOnClickListener(this);
        ll_canshu.setOnClickListener(this);
        apiService = HDRetrofit.create(HDApiService.class);

        // ListView
        mListView = (ListView) findViewById(R.id.listView);
        mListViewAdapter = new DiyListViewAdapter(ct);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        mOtherRl = (RelativeLayout) findViewById(R.id.otherRl);

        // 包裹GridView的FrameLayout
        mDiyGridViewContainer = (FrameLayout) findViewById(R.id.diyGridViewContainer);
        lp = (RelativeLayout.LayoutParams) mDiyGridViewContainer
                .getLayoutParams();
        lp.width = (int) (mScreenWidth / 3f / 4f * 3f);
        mDiyGridViewContainer.setPadding((int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth));
        mDiyGridViewContainer.setLayoutParams(lp);

        // GridView
        mGridView = (GridView) findViewById(R.id.diyGridView);
        mGridView.setVerticalSpacing((int) (8.0f / 1200.0f * mScreenWidth));
        mGridViewAdapter = new DiyGridViewAdapter(ct);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(this);

        pd2 = (ProgressBar) findViewById(R.id.pd2);

        mPullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setOnRefreshListener(this);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekBar2 = (SeekBar) findViewById(R.id.seekbar2);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        nightlevel = 0;
        curentLevel = 0;
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mCurrentNumber>=progresses.size()){
                        progresses.add(64);
                    }
                        progresses.set(mCurrentNumber, progress);
//                    ImageUtil.changeLight02(mCurrentView, mCurrentBitmap, mSeekNum2);
//                int progress=seekBar.getProgress();
                if(progress>0&&progress<20){
                    nightlevel=1;
                }else if(progress>=20&&progress<50){
                    nightlevel=2;
                }else if(progress>=50&&progress<70){
                    nightlevel=3;
                }else if(progress>=70&&progress<90){
                    nightlevel=4;
                }else {
                    nightlevel=5;
                }
//                if(curentLevel==nightlevel)return;
                curentLevel=nightlevel;
                if(mCurrentNumber>=bitmaps.size())return;
                if (!AppUtils.isEmpty(bitmaps.get(mCurrentNumber)))

//                    Log.e("progress",progress+"");
                    for(int i=0;i<mViews.size();i++){
                        View stickerView=mViews.get(i);
                        if(stickerView.getTag()!=null&&(int)stickerView.getTag()==mCurrentNumber){
                            System.out.println("current:"+mCurrentNumber+"");
                            if(bitmaps.get(mCurrentNumber).isRecycled()){
                                stickerView.setDrawingCacheEnabled(true);
                                bitmaps.set(mCurrentNumber,((StickerView)stickerView).getBitmap());
                                stickerView.setDrawingCacheEnabled(false);
                            }
                            Log.e("isOnSee",""+MyApplication.isOnSeekBar);
                            if(!MyApplication.isOnSeekBar){
                                return;
                            }
                            Log.e("change","change");
                            ImageUtil.changeLight03(((StickerView)stickerView), bitmaps.get(mCurrentNumber), progresses.get(mCurrentNumber));
//                            new Thread(){
//                                @Override
//                                public void run() {
//                                    super.run();
//                                    SystemClock.sleep(500);
//
//                                }
//                            }.start();
//                            MyApplication.isOnSeekBar=false;
                        }
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyApplication.isOnSeekBar=true;
                System.out.println("onstartTracking");


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyApplication.isOnSeekBar=false;
                System.out.println("onStopTrackingTouch"+seekBar.getProgress());

            }
        });
        seekBar2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("seebar touch",""+event.getAction());
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("seebar2","up or cancel");
//                        MyApplication.isOnSeekBar=false;
                        break;
                }
                return false;
            }
        });


    }

    //    上传图片

    private int TIME_OUT = 10 * 1000;   //超时时间
    private String CHARSET = "utf-8"; //设置编码
    private int mSeekNum = 50;
    private int mSeekNum2 = 50;
    private Bitmap mSceneBg;

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    private String uploadFile(Bitmap file, String RequestURL, Map<String, String> param, String imageName) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
        //      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                String params = "";
                if (param != null && param.size() > 0) {
                    Iterator<String> it = param.keySet().iterator();
                    while (it.hasNext()) {
                        sb = null;
                        sb = new StringBuffer();
                        String key = it.next();
                        String value = param.get(key);
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        sb.append(value).append(LINE_END);
                        params = sb.toString();
                        dos.write(params.getBytes());
                    }
                }
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"").append("file").append("\"")
                        .append(";filename=\"").append(imageName).append("\"\n");
                sb.append("Content-Type: image/png");
                sb.append(LINE_END).append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = ImageUtil.Bitmap2InputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }


                is.close();
                //                dos.write(file);
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);

                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                System.out.println("res=========" + res);
                if (res == 200) {
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                } else {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //
//    public void setInEdit(boolean inEdit) {
//        this.inEdit = inEdit;
//        if(inEdit){
//            iv_top.setVisibility(VISIBLE);
//            iv_flip.setVisibility(VISIBLE);
//            iv_delete.setVisibility(VISIBLE);
//            iv_resize.setVisibility(VISIBLE);
//        }else {
//            iv_resize.setVisibility(INVISIBLE);
//            iv_top.setVisibility(INVISIBLE);
//            iv_flip.setVisibility(INVISIBLE);
//            iv_delete.setVisibility(INVISIBLE);
//        }
//    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);
//        boolean handled = true;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//        }
//        if (handled && operationListener != null) {
//            operationListener.onEdit(this);
//        }
//        return handled;
//    }


    /**
     * 计算双指之间的距离
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }
    /**
     * 触摸的位置和图片左上角位置的中点
     *
     * @param event
     */
    private PointF mid = new PointF();
    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
//        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = mViews.get(mCurrentNumber).getLeft() + event.getX(0);
        float f4 = mViews.get(mCurrentNumber).getTop() + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float diagonalLength(MotionEvent event) {
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
    }

}
