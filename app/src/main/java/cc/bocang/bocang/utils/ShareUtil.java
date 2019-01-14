package cc.bocang.bocang.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import cc.bocang.bocang.R;

/**
 * Created by gamekonglee on 2018/5/21.
 */

public class ShareUtil {

    /**
     * 分享操作
     */
    public  static void shareWx(final Activity activity, String title, final String path){
        IWXAPI api= WXAPIFactory.createWXAPI(activity,"wx0d5873a8c4a78126",true);
        WXWebpageObject wxWebpageObject=new WXWebpageObject();
        wxWebpageObject.webpageUrl=path;
        WXMediaMessage wxMediaMessage=new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.title=title;
        Bitmap thumb= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_icon_share);
        wxMediaMessage.thumbData=ImageUtil.bitmap2Bytes(ImageUtil.createThumbBitmap(thumb,50,50),32);
//        wxMediaMessage.thumbData=ImageUtil.getBitmapByte(thumb);
        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction="urlpage";
        req.message=wxMediaMessage;
        req.scene=SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }
    public static void sharePyq(FragmentActivity activity, String title, String path) {
        IWXAPI api=WXAPIFactory.createWXAPI(activity,"wx0d5873a8c4a78126",true);
        WXWebpageObject wxWebpageObject=new WXWebpageObject();
        wxWebpageObject.webpageUrl=path;
        WXMediaMessage wxMediaMessage=new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.title=title;
        Bitmap thumb= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_icon_share);
        wxMediaMessage.thumbData=ImageUtil.bitmap2Bytes(ImageUtil.createThumbBitmap(thumb,50,50),32);
//        wxMediaMessage.thumbData=ImageUtil.getBitmapByte(thumb);
        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction="urlpage";
        req.message=wxMediaMessage;
        req.scene=SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
    public static void shareQQ(FragmentActivity activity, String title, String apkUrl, String shareimage) {
        Tencent mTencent=Tencent.createInstance("1105698263",activity);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareimage);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, apkUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  UIUtils.getString(R.string.app_name));
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(activity, params, null);
    }
}
