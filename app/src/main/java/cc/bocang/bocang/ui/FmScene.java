package cc.bocang.bocang.ui;

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

import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;

public class FmScene extends StatedFragment implements AdapterView.OnItemClickListener{
    private final String TAG = FmScene.class.getSimpleName();

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
    }
    private Network mNetwork;
    private HDApiService apiService;
    private Integer[] sceneIds = new Integer[]{0, 0, 0};
    private String fitterStr;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Constant.isDebug)
            Log.i(TAG, "onCreateView....");

        View view = initView(inflater, container);
        mNetwork=new Network();
        fitterStr = sceneIds[0] + "." + sceneIds[1] + "." + sceneIds[2];
        apiService = HDRetrofit.create(HDApiService.class);
        sendSceneClass();
        return view;
    }

    private List<Scene> scenes;

//    private void callSceneList(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr) {
//        pd.setVisibility(View.VISIBLE);
//        mNetwork.sendSceneList(c_id+"", page, keywords, type, filter_attr, this.getActivity(), new HttpListener() {
//            @Override
//            public void onSuccessListener(int what, String response) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                pd.setVisibility(View.GONE);
//                try {
//                    String json = response;
//                    Log.i(TAG, json);
//                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
//                    if (null != resp && resp.isSuccess()) {
//                        sceneAllAttrs = resp.getSceneAllAttrs();
//                        if (initFilterDropDownView)//重复setMenuAdapter会报错
//                            initFilterDropDownView(sceneAllAttrs);
//
//                        List<Scene> sceneList = resp.getScenes();
//                        if (1 == page)
//                            scenes = sceneList;
//                        else if (null != scenes) {
//                            scenes.addAll(sceneList);
//                            if (sceneList.isEmpty())
//                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    //                    Toast.makeText(getActivity(), "数据异常9...", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailureListener(int what, String ans) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                pd.setVisibility(View.GONE);
//                FmScene.this.page--;
//            }
//        });
//
//    }

    private void sendSceneClass(){
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendSceneClass(this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String ans) {
                pd.setVisibility(View.GONE);
                try {
                    ans="{\"data\":"+ans+"}";
                    JSONObject jsonObject = new JSONObject(ans);
                    JSONArray array = jsonObject.getJSONArray("data");
                    scenes=new ArrayList<Scene>();
                    for(int i=0;i<array.length();i++) {
                        String id = array.getJSONObject(i).getString("id");
                        String name = array.getJSONObject(i).getString("name");
                        String path = array.getJSONObject(i).getString("icon_paht");
                        scenes.add(new Scene(id,name,path,""));
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailureListener(int what, String ans) {
                pd.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            Intent intent = new Intent(getActivity(), SceneActivity.class);
            intent.putExtra("id", scenes.get(position).getId());
            intent.putExtra("title", scenes.get(position).getName());
            startActivity(intent);
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
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_scene, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(getActivity(), 45f)) / 2f * (3f / 4f);
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(scenes.get(position).getName());

            imageLoader.displayImage(Constant.SCENE_CLASS_URL + scenes.get(position).getPath(), holder.imageView, options);

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

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_scene, container, false);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        pd = (ProgressBar) v.findViewById(R.id.pd);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        return v;
    }
}
