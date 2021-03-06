package cc.bocang.bocang.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.adapter.DropMenuAdapter;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;

public class FmProduct extends StatedFragment implements OnFilterDoneListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = FmProduct.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean isScrollToTop=false;

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

    static DropDownMenu dropDownMenu;
    private HDApiService apiService;
    private List<Integer> itemPosList = new ArrayList<>();//有选中值的itemPos列表，长度为3
    private Integer[] goodsIds = new Integer[]{0, 0, 0};
    private String fitterStr;
    private int page = 1;
    private Network mNetwork;

    private String goodsType = "";
    private String goodsClassId = "";

    private DropMenuAdapter mDropMenuAdapter;


    // 商业照明
    private String[] business = new String[]{"全部", "COB筒灯", "SMD筒灯", "明装筒灯",
            "LED天花射灯", "酒店射灯", "象鼻灯", "LED格栅射灯", "LED轨道灯", "橱柜射灯", "地脚灯", "LED户外投光灯",
            "LED净化灯", "观片灯", "直发光", "侧发光", "吸顶灯", "T5支架"};// 75, 79,81,58,62,63,64,65,66,91,89,67,68,69,86,87,88,92

    String[] keyStrs = new String[]{"75", "79", "81", "58", "62", "63", "64",
            "65", "66", "91", "89", "67", "68", "69", "86", "87", "88", "92"};
    final List<String> businessKeys = Arrays.asList(keyStrs);// 分类ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Constant.isDebug)
            Log.i(TAG, "onCreateView....");
        View view = initView(inflater, container);
        mNetwork=new Network();
        int titlePos = ((IndexActivity) getActivity()).titlePos;
        int goodsId = ((IndexActivity) getActivity()).goodsId;
        if (((IndexActivity) getActivity()).isClickFmHomeItem) {//如果是通过点击首页跳转过来的
            goodsIds[titlePos] = goodsId;
            ((IndexActivity) getActivity()).isClickFmHomeItem = false;
        }
        fitterStr = goodsIds[0] + "." + goodsIds[1] + "." + goodsIds[2];
        Log.v("520","fitterStr"+fitterStr);
        apiService = HDRetrofit.create(HDApiService.class);
        page = 1;
        //callGoodsList(apiService, "0", page, null, null, fitterStr, true);
        //getGoodsList("0", page, null, null, fitterStr,true);
        goodsType = ((IndexActivity) getActivity()).mGoodClazz;
        if (goodsType == "77") {// 商业照明
            getCateBusinessGoodsList("77", 1, page, null, null, fitterStr);// 初始化产品列表
            initDropDownMenu();// 初始化下拉菜单

        } else if (goodsType == "76") {// 家居照明
            //getGoodsList("76", page, null, null, fitterStr,true);
            getCateGoodsList("76", 1, page, null, null, fitterStr, true);
        } else if (goodsType == "0") {// 全部
            getGoodsList("0", page, null, null, fitterStr,true);
        }

        pd.setVisibility(View.VISIBLE);
        return view;
    }

    private List<GoodsAllAttr> goodsAllAttrs;
    private List<Goods> goodses;

    /**
     * 获取全部产品
     * @param c_id
     * @param page
     * @param keywords
     * @param type
     * @param filter_attr
     * @param initFilterDropDownView
     */
    private void getGoodsList(String c_id, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView){
        mNetwork.sendGoodsList(c_id, page, "0", keywords, type, filter_attr, FmProduct.this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (initFilterDropDownView)//重复setMenuAdapter会报错
                            initFilterDropDownView(goodsAllAttrs);

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                FmProduct.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });

    }

    /**
     * 获取所属家居照明大类的产品列表(或者商业照明)
     * @param c_id
     * @param down_query
     * @param page
     * @param keywords
     * @param type
     * @param filter_attr
     * @param initFilterDropDownView
     */
    private void getCateGoodsList(String c_id, final int down_query, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView){
        mNetwork.sendCateGoodsList(c_id, down_query, page, "0", keywords, type, filter_attr, FmProduct.this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (initFilterDropDownView)//重复setMenuAdapter会报错
                            initFilterDropDownView(goodsAllAttrs);

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                FmProduct.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });

    }

    /**
     * 获取所属商业照明大类的产品列表
     * @param c_id
     * @param page
     * @param keywords
     * @param type
     * @param filter_attr
     */
    private void getCateBusinessGoodsList(String c_id, final int down_query, final int page, String keywords, String type, String filter_attr){
        mNetwork.sendCateGoodsList(c_id, down_query, page, "0", keywords, type, filter_attr, FmProduct.this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();

                        if(isScrollToTop)
                        {
                            mGridView.smoothScrollToPositionFromTop(0,0);
                            isScrollToTop=false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                FmProduct.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });

    }
//    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView) {
//        pd.setVisibility(View.VISIBLE);
//        Call<ResponseBody> call = apiService.getGoodsList(c_id, page,0, keywords, type, filter_attr);
//        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                pd.setVisibility(View.GONE);
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
//                try {
//                    String json = response.body().string();
//                    Log.i(TAG, json);
//                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
//                    if (null != resp && resp.isSuccess()) {
//                        goodsAllAttrs = resp.getGoodsAllAttrs();
//                        if (mInitFilterDropDownView){
//                            mInitFilterDropDownView=false;
//                            initFilterDropDownView(goodsAllAttrs);
//                        }
//
//                        List<Goods> goodsList = resp.getGoodses();
//                        if (1 == page)
//                            goodses = goodsList;
//                        else if (null != goodses) {
//                            goodses.addAll(goodsList);
//                            if (goodsList.isEmpty())
//                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
////                    Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                pd.setVisibility(View.GONE);
//                FmProduct.this.page--;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
////                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /** 家居照明 */
    private void initFilterDropDownView(List<GoodsAllAttr> goodsAllAttrs) {
        int titlePos = ((IndexActivity) getActivity()).titlePos;
        int itemPos = ((IndexActivity) getActivity()).itemPos;
        if (itemPosList.size() < goodsAllAttrs.size()) {
            itemPosList.add(0);
            itemPosList.add(0);
            itemPosList.add(0);
        }
        if (titlePos < itemPosList.size())
            itemPosList.remove(titlePos);
        itemPosList.add(titlePos, itemPos);
        ProDropMenuAdapter dropMenuAdapter = new ProDropMenuAdapter(getActivity(), goodsAllAttrs, itemPosList, this);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
    }

    /** 家居照明 */
    @Override
    public void onFilterDone(int titlePos, int itemPos, String itemStr) {

        page = 1;
        dropDownMenu.close();
        if (0 == itemPos)
            itemStr = goodsAllAttrs.get(titlePos).getAttrName();
        dropDownMenu.setPositionIndicatorText(titlePos, itemStr);

        if (titlePos < itemPosList.size())
            itemPosList.remove(titlePos);
        itemPosList.add(titlePos, itemPos);

        int goodsId = goodsAllAttrs.get(titlePos).getGoodsAttrs().get(itemPos).getGoods_id();
        if (itemPos!=0) {
            if(goodsId==0){
                goodsId=999;
            }
        }
        goodsIds[titlePos] = goodsId;
        fitterStr = goodsIds[0] + "." + goodsIds[1] + "." + goodsIds[2];

        //callGoodsList(apiService, "0", page, null, null, fitterStr, false);
        //getGoodsList("0", page, null, null, fitterStr, false);
        if (goodsType == "76") {// 家居照明
            getCateGoodsList("76", 1, page, null, null, fitterStr, false);
        } else if (goodsType == "0") {// 全部
            getGoodsList("0", page, null, null, fitterStr, false);
        }

        if (Constant.isDebug)
            Toast.makeText(getActivity(), itemStr + " titlePos：" + titlePos + "  itemPos：" + itemPos, Toast.LENGTH_SHORT).show();
    }

    /** 商业照明 */
    public void initDropDownMenu() {
        // 问题：“内存泄漏” 现象：点击筛选条件，提示没有更多，同时不能显示相应数据，需要刷新才会正常
        mDropMenuAdapter = new DropMenuAdapter(getActivity(), Arrays.asList(business), new OnFilterDoneListener() {
            @Override
            public void onFilterDone(int titlePos,int itemPos, String itemStr) {
                page = 1;
                dropDownMenu.close();
                if (itemPos == 0)
                    itemStr = "分类";
                dropDownMenu.setPositionIndicatorText(titlePos, itemStr);
                goodsClassId = businessKeys.get(itemPos);
                isScrollToTop=true;
                progressDialog = ProgressDialog.show(getActivity(),"","加载中");
                getCateBusinessGoodsList(goodsClassId, 1, page, null, null, fitterStr);

            }
        });
        dropDownMenu.setMenuAdapter(mDropMenuAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            if (Constant.isDebug)
                Toast.makeText(getActivity(), "点击：" + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            intent.putExtra("imgurl",goodses.get(position).getImg_url());
            intent.putExtra("goodsname",goodses.get(position).getName());
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        //callGoodsList(apiService, "0", page, null, null, fitterStr, false);
        //getGoodsList("0", page, null, null, fitterStr,false);
        if (goodsType == "76") {
            getCateGoodsList("76", 1, page, null, null, fitterStr, false);
        } else if (goodsType == "77") {

            getCateBusinessGoodsList(goodsClassId, 1, page, null, null, fitterStr);
        } else {
            getGoodsList("0", page, null, null, fitterStr,false);
        }

    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        //callGoodsList(apiService, "0", ++page, null, null, fitterStr, false);
        //getGoodsList("0", ++page, null, null, fitterStr,false)
        if (goodsType == "76") {
            getCateGoodsList("76", 1, ++page, null, null, fitterStr, false);
        } else if (goodsType == "77") {
            getCateBusinessGoodsList(goodsClassId, 1, ++page, null, null, fitterStr);
        } else {
            getGoodsList("0", ++page, null, null, fitterStr,false);
        }
    }

    private class MyAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public MyAdapter() {
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

    private int mScreenWidth;
    private ProgressBar pd;
    private GridView mGridView;
    private MyAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_product, container, false);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        pd = (ProgressBar) v.findViewById(R.id.pd);
        dropDownMenu = (DropDownMenu) v.findViewById(R.id.dropDownMenu);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        mPullToRefreshLayout = ((PullToRefreshLayout) v.findViewById(R.id.mFilterContentView));
        mPullToRefreshLayout.setOnRefreshListener(this);
        return v;
    }
}
