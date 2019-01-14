package cc.bocang.bocang.view;

import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cc.bocang.bocang.R;
import cc.bocang.bocang.global.Constant;


/**
 * @author: Jun
 * @date : 2017/2/16 15:12
 * @description :
 */
public class SceneParamePopWindow extends BasePopwindown implements View.OnClickListener {
    private WebView mWebView;
    private RelativeLayout main_rl;
    private String mSharePath="";
    private ImageView closeIv;

    public SceneParamePopWindow(Context context) {
        super(context);


    }

    @Override
    protected void initView(Context context) {
        View contentView = View.inflate(mContext, R.layout.pop_sceen_paramt, null);
        initUI(contentView);

    }


    private void initUI(View contentView) {
        main_rl = (RelativeLayout) contentView.findViewById(R.id.main_rl);
        main_rl.setOnClickListener(this);
        closeIv = (ImageView) contentView.findViewById(R.id.closeIv);
        closeIv.setOnClickListener(this);
        mWebView = (WebView) contentView.findViewById(R.id.wv);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
    }

    public void getWebViewData(String desc){
        String html = desc;
        html = html.replace("<img src=\"", "<img src=\"" + Constant.BASE_URL);
        html = "<meta name=\"viewport\" content=\"width=device-width\">" + html;
        mWebView.loadData(html, "text/html; charset=UTF-8", null);//这种写法可以正确解析中文
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_rl:
                onDismiss();
                break;
            case R.id.closeIv:
                onDismiss();
                break;
        }
    }
}
