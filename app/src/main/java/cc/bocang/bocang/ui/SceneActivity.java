package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.parser.ParseGetSceneListResp;
import cc.bocang.bocang.data.response.GetSceneListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;

/**
 * @author: Jun
 * @date : 2017/9/22 9:31
 * @description : 场景2级目录
 */
public class SceneActivity extends BaseActivity implements AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private Network mNetwork;
    private List<Scene> scenes;
    private int mPage = 1;
    private PullToRefreshLayout mPullToRefreshLayout;
    private String mCId="";
    private TextView title_tv;
    private String mTitle="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_scene);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        pd = (ProgressBar) findViewById(R.id.pd);
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        mNetwork = new Network();
        mPullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.mFilterContentView));
        mPullToRefreshLayout.setOnRefreshListener(this);
        callSceneList();
        title_tv = (TextView)findViewById(R.id.title_tv);
        title_tv.setText(mTitle);
    }

    private void initData() {
        Intent intent=getIntent();
        mCId=intent.getStringExtra("id");
        mTitle=intent.getStringExtra("title");

    }

    public void goBack(View v){
        finish();
    }

    /**
     * 获取场景列表
     */
    private void callSceneList() {
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendSceneList(mCId, mPage, null, null, null, this, new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                pd.setVisibility(View.GONE);
                try {
                    String json = response;
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<Scene> sceneList = resp.getScenes();
                        if (1 == mPage)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(SceneActivity.this, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                pd.setVisibility(View.GONE);
                SceneActivity.this.mPage--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            Intent intent = new Intent(this, DiyActivity.class);
            intent.putExtra("from", "scene");
            intent.putExtra("path", scenes.get(position).getPath());
            intent.putExtra("scene_desc", scenes.get(position).getScene_desc());
            intent.putExtra("title", mTitle);
            startActivity(intent);

        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPage=1;
        callSceneList();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPage=mPage+1;
        callSceneList();
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
            if (null == scenes)
                return 0;
            return scenes.size();
        }

        @Override
        public Scene getItem(int position) {
            if (null == scenes)
                return null;
            return scenes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(SceneActivity.this, R.layout.item_gridview_fm_scene, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(SceneActivity.this, 45f)) / 2f * (3f / 4f);
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(scenes.get(position).getName());

//            imageLoader.displayImage(Constant.SCENE_URL + scenes.get(position).getPath(), holder.imageView, options);
            if (Integer.parseInt(mCId) == 53) {// 混合空间
                imageLoader.displayImage(Constant.SCENE_URL2 + scenes.get(position).getPath(), holder.imageView, options);
            }else {
                imageLoader.displayImage(Constant.SCENE_URL + scenes.get(position).getPath(), holder.imageView, options);
            }

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

}
