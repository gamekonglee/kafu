package cc.bocang.bocang.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.ResponseBody;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Ad;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.parser.ParseGetAdResp;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.response.GetAdResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.ResUtil;
import cc.bocang.bocang.utils.StringUtil;
import cc.bocang.bocang.utils.UIUtils;
import cc.bocang.bocang.utils.UniversalUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static cc.bocang.bocang.view.CompressImageView.decodeSampledBitmapFromResource;

public class FmHome extends StatedFragment implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = FmHome.class.getSimpleName();

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constant.isDebug)
            Log.i(TAG, "onCreate...");
    }
    private HDApiService apiService;
    private int page = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Constant.isDebug)
            Log.i(TAG, "onCreateView...");
        View view = initView(inflater, container);
        mNetwork=new Network();

        apiService = HDRetrofit.create(HDApiService.class);
//        callAd(apiService);
        initAd();
        page = 1;
//        callGoodsList(apiService, IndexActivity.mCId, page, null, "is_best", null);


        return view;
    }

    private List<Goods> goodses;
    private Network mNetwork;

    /** 爆款推荐（这里的首页没有用到） */
    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {

        mNetwork.sendGoodsList(c_id, page, "1", keywords, type, filter_attr, FmHome.this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {

                        List<Goods> goodsList = resp.getGoodses();// 产品列表（推荐）
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses){
                            goodses.addAll(goodsList);
                            if(goodsList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mProAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    if (null != getActivity() && !getActivity().isFinishing())
                        e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                FmHome.this.page--;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
            }
        });
    }

//    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {
//        //重新获取，先清空列表
//        typeGoodsIdList.clear();
//        spaceGoodsIdList.clear();
//        styleGoodsIdList.clear();
//        Call<ResponseBody> call = apiService.getGoodsList(c_id, page,1, null, type, filter_attr);
//        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
//                try {
//                    String json = response.body().string();
//                    Log.i(TAG, json);
//                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
//                    if (null != resp && resp.isSuccess()) {
//                        List<GoodsAllAttr> goodsAllAttrs = resp.getGoodsAllAttrs();
//                        for (int i = 0; i < goodsAllAttrs.size(); i++) {
//                            GoodsAllAttr goodsAllAttr = goodsAllAttrs.get(i);
//                            if (i == 0) {//类型
//                                String attrName = goodsAllAttr.getAttrName();
//                                mTypeTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        typeGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            } else if (i == 1) {//空间
//                                String attrName = goodsAllAttr.getAttrName();
//                                mSpaceTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        spaceGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            } else if (i == 2) {//风格
//                                String attrName = goodsAllAttr.getAttrName();
//                                mStyleTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        styleGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            }
//                        }
//
//                        List<Goods> goodsList = resp.getGoodses();
//                        if (1 == page)
//                            goodses = goodsList;
//                        else if (null != goodses){
//                            goodses.addAll(goodsList);
//                            if(goodsList.isEmpty())
//                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
//                        }
//                        mProAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    if (null != getActivity() && !getActivity().isFinishing())
////                    Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                FmHome.this.page--;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
////                if (null != getActivity() && !getActivity().isFinishing())
////                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * 广告背景，服务器中获取
     */
    private void callAd(HDApiService apiService) {
        Call<ResponseBody> call = apiService.getAd(Constant.AD_PARAM);// 2张广告图
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                try {
                    String json = response.body().string();
                    Log.i(TAG, "广告：" + json);
                    GetAdResp resp = ParseGetAdResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<Ad> ads = resp.getBeans();
                        List<String> imgUrl = new ArrayList<>();
                        for (Ad ad : ads) {
                            imgUrl.add(ad.getPath());
                        }
                        List<String> paths = StringUtil.preToStringArray(Constant.AD_URL, imgUrl);

                        // banner滚动
                        mConvenientBanner.setPages(
                                new CBViewHolderCreator<NetworkImageHolderView>() {
                                    @Override
                                    public NetworkImageHolderView createHolder() {
                                        return new NetworkImageHolderView();//图片加载
                                    }
                                }, paths);
                    }
                } catch (Exception e) {
//                    Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
//                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 网络图片加载 */
    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            imageView.setImageResource(R.mipmap.bg_default);
            ImageLoader.getInstance().displayImage(data, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 点击事件
                    if (Constant.isDebug)
                        Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 设置广告（本地加载）
     */
    private void initAd() {
        List<Integer> localImages = new ArrayList<>();
        //获取本地的图片
        for (int i = 0; i < 3; i++) {
            localImages.add(ResUtil.getResDrawable(getActivity(), "banner" + i));
        }

        // banner设置滚动的页面
        mConvenientBanner.setPages(
            new CBViewHolderCreator() {
                @Override
                public LocalImageHolderView createHolder() {
                    return new LocalImageHolderView();// 图片加载
                }
            }, localImages);
    }

    /** 本地图片加载 */
    class LocalImageHolderView implements CBPageAdapter.Holder<Integer> {

        private ImageView imageView;

        @Override
        public View createView(Context context) {
            // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int i, Integer data) {
            imageView.setImageResource(data);
            ImageLoader.getInstance().displayImage("drawable://" + data, imageView);// 资源图片路径："drawable://" + R.drawable.xxx
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "点击了第" + i + "个", Toast.LENGTH_SHORT).show();
                    if (i == 2) {
                        Intent intent = new Intent(getActivity(), VideoActivity.class);// 点击播放视频
                        startActivity(intent);
                    }
                }
            });
        }
    }

    // 跳转到产品界面
    @Override
    public void onClick(View view) {
        if (view == intelligent) {// 光源体验
            Intent intent = new Intent(getActivity(), BaseLightActivity.class);// 光源体验
            startActivity(intent);
        } else if (view == home) {// 家居照明
            ((IndexActivity) getActivity()).mGoodClazz = "76";
            ((IndexActivity) getActivity()).isClickFmHomeItem = true;// 设置为通过点击首页跳转过来的
            ((IndexActivity) getActivity()).mFragmentPagerAdapter.notifyDataSetChanged();
            ((IndexActivity) getActivity()).mViewPager.setCurrentItem(1, true);
        } else if (view == business) {// 商业照明

            ((IndexActivity) getActivity()).mGoodClazz = "77";
            ((IndexActivity) getActivity()).isClickFmHomeItem = true;// 设置为通过点击首页跳转过来的
            ((IndexActivity) getActivity()).mFragmentPagerAdapter.notifyDataSetChanged();
            ((IndexActivity) getActivity()).mViewPager.setCurrentItem(1, true);
        } else if (view == more) {// 更多
            ((IndexActivity) getActivity()).mGoodClazz = "0";
            ((IndexActivity) getActivity()).isClickFmHomeItem = true;// 设置为通过点击首页跳转过来的
            ((IndexActivity) getActivity()).mFragmentPagerAdapter.notifyDataSetChanged();
            ((IndexActivity) getActivity()).mViewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (parent == mItemGridView) {
//            if (Constant.isDebug)
//                Toast.makeText(getActivity(), ((TextView) view.findViewById(R.id.textView)).getText(), Toast.LENGTH_SHORT).show();
//
//                ((IndexActivity) getActivity()).titlePos = 0;
//                ((IndexActivity) getActivity()).itemPos = position >= typeGoodsIdList.size() - 1 || position == 7 ? 0 : position + 1;
//                ((IndexActivity) getActivity()).goodsId = position >= typeGoodsIdList.size() - 1 || position == 7 ? 0 : typeGoodsIdList.get(position);
//
//            ((IndexActivity) getActivity()).isClickFmHomeItem = true;
////            ((IndexActivity) getActivity()).fragmentsUpdateFlag[1] = true;//FmProduct重新onCreate（界面会有短暂的延迟）
//            ((IndexActivity) getActivity()).mFragmentPagerAdapter.notifyDataSetChanged();
//            ((IndexActivity) getActivity()).mViewPager.setCurrentItem(1, true);
//        }

//        if (parent == mProGridView) {
//            if (Constant.isDebug)
//                Toast.makeText(getActivity(), "点击产品：" + position, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
//            intent.putExtra("id", goodses.get(position).getId());
//            intent.putExtra("imgurl",goodses.get(position).getImg_url());
//            intent.putExtra("goodsname",goodses.get(position).getName());
//
//            startActivity(intent);
//        }
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        callAd(apiService);
        page = 1;
        callGoodsList(apiService, IndexActivity.mCId, page, null, "is_best", null);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        callGoodsList(apiService, IndexActivity.mCId, ++page, null, "is_best", null);
    }

    private class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_home, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    private class ProAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public ProAdapter() {
            options = new DisplayImageOptions.Builder()
                    // 设置图片下载期间显示的图片
                    .showImageOnLoading(R.mipmap.bg_default)
                    // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageForEmptyUri(R.mipmap.bg_default)
                    // 设置图片加载或解码过程中发生错误显示的图片
                    // .showImageOnFail(R.drawable.ic_error)
                    // 设置下载的图片是否缓存在内存中
                    .cacheInMemory(true)
                    // 设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
                    // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                    // .displayer(new FadeInBitmapDisplayer(100))//
                    // 图片加载好后渐入的动画时间
                    .build(); // 构建完成

            // 得到ImageLoader的实例(使用的单例模式)
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            if (null == goodses)
                return 0;
            return goodses.size();
        }

        @Override
        public Goods getItem(int position) {
            if (null == goodses)
                return null;
            return goodses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_product, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(getActivity(), 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(goodses.get(position).getName());

            imageLoader.displayImage(Constant.PRODUCT_URL + goodses.get(position).getImg_url() + "!400X400.png", holder.imageView, options);

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // 开始自动翻页
        mConvenientBanner.startTurning(UniversalUtil.randomA2B(3000, 5000));
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止翻页
        mConvenientBanner.stopTurning();
    }


//    private GridView mProGridView;
    private ProAdapter mProAdapter;
    private ConvenientBanner mConvenientBanner;
    private int mScreenWidth;
    private View mPullToRefreshLayout;
    private LinearLayout intelligent, home, business, more;// 智能灯光，家居照明，商业照明，更多

    private View initView(LayoutInflater inflater, ViewGroup container) {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        View v = inflater.inflate(R.layout.fm_home_new, container, false);

        ScrollView sv = (ScrollView) v.findViewById(R.id.scrollView);
        sv.smoothScrollTo(0, 0);
        ImageView iv_bg=v.findViewById(R.id.iv_bg);
//        iv_bg.setImageBitmap(ImageUtil.compressImage(ImageUtil.getBitmapById(getContext(),R.mipmap.bg_home)));
        BitmapWorkerTask task = new BitmapWorkerTask(iv_bg);
        task.execute(R.mipmap.bg_long);
        mConvenientBanner = (ConvenientBanner) v.findViewById(R.id.convenientBanner);
        LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = (int) (mScreenWidth * (360f / 640f));
        mConvenientBanner.setLayoutParams(rlp);

//        RelativeLayout gridViewRl = (RelativeLayout) v.findViewById(R.id.gridViewRl);
//        rlp = (RelativeLayout.LayoutParams) gridViewRl.getLayoutParams();
//        rlp.height = (int) (mScreenWidth / 4f * 2f + 30);// 设置高度
//        gridViewRl.setLayoutParams(rlp);

//        mProGridView = (GridView) v.findViewById(R.id.priductGridView);
//        mProGridView.setOnItemClickListener(this);
//        mProAdapter = new ProAdapter();
//        mProGridView.setAdapter(mProAdapter);

        mPullToRefreshLayout = ( v.findViewById(R.id.refresh_view));
//        mPullToRefreshLayout.setOnRefreshListener(this);

        intelligent = (LinearLayout) v.findViewById(R.id.intelligent);
        home = (LinearLayout) v.findViewById(R.id.home);
        business = (LinearLayout) v.findViewById(R.id.business);
        more = (LinearLayout) v.findViewById(R.id.more);

        intelligent.setOnClickListener(this);
        home.setOnClickListener(this);
        business.setOnClickListener(this);
        more.setOnClickListener(this);

        return v;
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
            int width=UIUtils.getScreenWidth(getActivity());
            int height=width*5629/750;
            return ImageUtil.compressBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), data, width,height),800);
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
