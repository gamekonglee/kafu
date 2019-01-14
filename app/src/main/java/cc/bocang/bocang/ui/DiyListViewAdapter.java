package cc.bocang.bocang.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.GoodsClass;
import cc.bocang.bocang.utils.UIUtils;

public class DiyListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<GoodsClass> data;
    private int selection;// 当前选中位置
    private int mScreenWidth;
    private Context mContext;

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setData(List<GoodsClass> data) {
        this.data = data;
        for(GoodsClass goodsClass: data){
            if(goodsClass.getLevel()==3){
                goodsClass.isVisible=false;
            }else {
                goodsClass.isVisible=true;
            }
        }
    }

    public DiyListViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    }
    public void setChildVisible(int id){
        for(int i=0;i<data.size();i++){
            if(data.get(i).getPid()==id){
                data.get(i).isVisible=!data.get(i).isVisible;
                }
            }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == data)
            return 0;

        return data.size();
    }

    @Override
    public GoodsClass getItem(int position) {
        if (null == data)
            return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_lv_diy, null);

            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.textView);
            holder.tv_num=convertView.findViewById(R.id.tv_num);
            holder.gridview_item_linearlayout=convertView.findViewById(R.id.gridview_item_linearlayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.tv.setMinHeight((int) (120.0f / 1200.0f * mScreenWidth));
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.tv.setText("+"+getItem(position).getName());
            layoutParams.setMargins(0,UIUtils.dip2PX(5),0,UIUtils.dip2PX(5));
        if(data.get(position).getLevel()==1){
//            holder.tv.setTextSize(UIUtils.dip2PX(4));
        }else if(data.get(position).getLevel()==2){
            layoutParams.setMargins(0,UIUtils.dip2PX(5),0,UIUtils.dip2PX(5));
//            holder.tv.setTextSize(UIUtils.dip2PX(4));
        }else if(data.get(position).getLevel()==3){
            holder.tv.setText(""+getItem(position).getName());
            layoutParams.setMargins(UIUtils.dip2PX(10),UIUtils.dip2PX(5),0,UIUtils.dip2PX(5));
//            holder.tv.setTextSize(UIUtils.dip2PX(4));
        }
        holder.tv.setLayoutParams(layoutParams);
        if(data.get(position).isVisible){
            holder.tv.setVisibility(View.VISIBLE);
        }else {
            holder.tv.setVisibility(View.GONE);
        }

        holder.tv_num.setText(data.get(position).getLevel()+"-");
        // 处理选中和未选中的品牌背景
        if (selection == position) {
            holder.gridview_item_linearlayout.setBackgroundColor(Color.parseColor("#66000000"));
//            holder.tv.setBackgroundColor(Color.parseColor("#66000000"));
        } else {
            holder.gridview_item_linearlayout.setBackgroundColor(Color.parseColor("#000000"));
//            holder.tv.setBackgroundColor(Color.parseColor("#000000"));
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv;
        TextView tv_num;
        View gridview_item_linearlayout;
    }
}
