package cc.bocang.bocang.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.interfaces.OnFilterItemClickListener;
import com.baiiu.filter.typeview.SingleGridView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;

import java.util.List;

import cc.bocang.bocang.R;

/**
 * @author: hardawin
 * @date : 2017/12/5 15:06
 * @description : 商业照明3级分类
 */
public class DropMenuAdapter implements MenuAdapter {

    private final Context mContext;
    private OnFilterDoneListener mOnFilterDoneListener;
    private List<String> mGoodsType;

    public DropMenuAdapter(Context context, List<String> goodsType, OnFilterDoneListener onFilterDoneListener) {
        mContext = context;
        mGoodsType = goodsType;
        mOnFilterDoneListener = onFilterDoneListener;
    }

    /** 设置筛选条目个数 */
    @Override
    public int getMenuCount() {
        return 1;
    }

    /** 设置每个筛选器默认Title */
    @Override
    public String getMenuTitle(int position) {
        return "分类";
    }

    /** 设置每个筛选条目距离底部距离 */
    @Override
    public int getBottomMargin(int position) {
        return 0;
    }

    /** 设置每个筛选条目的View */
    @Override
    public View getView(int position, FrameLayout parentContainer) {
        return createSingleGridView(position);
    }

    private View createSingleGridView(final int position) {
        SingleGridView<String> singleGridView = new SingleGridView<String>(mContext)
                .adapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String s) {
                        return s;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(0, UIUtil.dp(context, 3), 0, UIUtil.dp(context, 3));
                        checkedTextView.setGravity(Gravity.CENTER);
                        checkedTextView.setBackgroundResource(R.drawable.selector_filter_grid);
                    }
                })
                .onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(int itemPos, String itemStr) {
                        if (mOnFilterDoneListener != null) {
                            mOnFilterDoneListener.onFilterDone(position, itemPos, itemStr);
                        }
                    }
                });
        singleGridView.setList(mGoodsType, position);//-1：默认不选中
        return singleGridView;
    }
}
