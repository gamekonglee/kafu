package cc.bocang.bocang.global;

/**
 * Created by xpHuang on 2016/8/21.
 */
public class Constant {

    /**
     * 是否为调试模式
     */
    public static boolean isDebug = false;

    /**
     * 域名
     */
    public static final String BASE_URL = "http://kafu.bocang.cc";

    /**
     * 获取app最新版本号接口
     */
    public static final String VERSION_URL ="http://app.08138.com/version/versioninfo.php?bc_ver_name2=kafua";

    /**
     * 广告背景请求参数：1为平板首页背景图，2为平板产品列表页广告图，3为手机首页广告图，4为平板启动页，5为品牌背景图
     */
    public static final int AD_PARAM = 3;

    /**
     * 首页广告背景url
     */
    public static final String AD_URL = "http://kafu.bocang.cc/App/kafu/Public/uploads/ad/";

    /**
     * 产品url
     */
    public static final String PRODUCT_URL = "http://kafu.bocang.cc/App/kafu/Public/uploads/goods/";

    /**
     * 场景url
     */
    public static final String SCENE_URL = "http://kafu.bocang.cc/App/kafu/Public/uploads//scene/";

    public static final String SCENE_URL2 = "http://bocang.oss-cn-shenzhen.aliyuncs.com/scene/";

    //场景分类url
    public static final String SCENE_CLASS_URL = "http://kafu.bocang.cc/App/kafu/Public/uploads//";

    public static final String SHAPE_SCEEN = "http://kafu.bocang.cc/App/kafu/Public/uploads//plan/" ;


    /**
     * Apk下载地址
     */
    public static final String APK_URL = "http://app.08138.com/kafu.apk";
    /**
     * 下载完Apk保存的文件名
     */
    public static final String APK_NAME1 = "kafu_v";
    public static final String APK_NAME2 = ".apk";


    /**
     * 拍照保存在sd卡下的文件夹名称
     */
    public static final String CAMERA_PATH = "kafu/camera";

    /**
     * 本地数据库名称
     */
    public static final String DB_NAME = "local02.db";

    /**
     * 本地数据库版本号
     */
    public static final int DB_VERSION = 2;


    /**
     * 官网
     */
    public final static String OfficialWebsite ="http://www.colorfullighting.com";

    /**
     * 服务电话
     */
    public final static String ServicePhone = "400-830-3335";

    /**
     * 一键联系电话
     */
    public final static String OneContactPhone = "400-830-3335";

    /**
     * 公司简介
     */
    public final static String Companyrofile = "　　中山市卡弗照明有限公司成立于2004年，产品涵盖了以商业照明为主导，家居照明、工程照明、光源电气等系列的产品线，是集研、产、销于一体的综合化照明产品运营商。\n" +
            "  　\n" +
            "　　创立伊始，卡弗即以领先的设计理念为指挥棒，以打造“高品质、高品位”产品为宗旨，刻苦钻研，不懈探索。目前，卡弗荣获各项设计专利达986项之多，堪称照明行业专利之首。 \n" +
            "\n" +
            "　　以此同时，公司两项专利分别被评为“国家专利技术发明一等奖”和“国家科学技术成果二等奖”，在香港国际专利技术专利产品博览会上以绝对优势荣获金奖，总经理潘家红先生当选《世界当代优秀科技专家》并被聘为香港国际专利技术专利产品博览会高级技术顾问。\n" +
            "\n" +
            "　　公司的高效管理和蓬勃发展也赢得国家和社会的高度认可。一举通过了IS09001:2008国际质量管理体系认证，英国环球UKS质量体系认证。产品获3C、欧盟CE认证。此外，公司还荣膺“消费者放心产品”、“中国照明灯饰行业质量与品牌100强企业”、“商业照明推荐品牌”、“中山市科技创新奖”、“中国照明行业最具发展潜力企业”诸项殊荣。 \n" +
            "\n" +
            "　　公司销售网络庞大，覆盖国内30多个省市以及欧美市场。内外销兼容并蓄，相得益彰。未来的卡弗人将一如既往锐意进取，不断创新，以复兴民族工业为己任，创行业先锋，立诚信伟业。";

     /**单
     */
    public final static  String SHAREICON=BASE_URL+"/index.php/Interface/order_show?id=";

    /**
     * 分享订单图片
     */
//    public final static  String SHAREIMAGE="http://app.08138.com/icon.jpg";
    public final static  String SHAREIMAGE="http://app.08138.com/kafu/ic_icon.png";

    /**
     * 分享产品
     */
    public final static  String SHAREPRODUCT="http://kafu.bocang.cc/Interface/goods_show?";

    /**
     * 分享app
     */
    public final static  String SHAREAPP="http://app.bocang.cc/Ewm/index/url/kafu.bocang.cc";

    /**
     * 更多方案
     */
    public final static  String SHAREPLAN="http://kafu.bocang.cc/index.php/Interface/plan_show?";


    public final static  String SUBMITPLAN=BASE_URL+"/index.php/Interface/upload_plan";

    /**
     * 产品上样
     */
    public final static  String UPLOADSAMPLE=BASE_URL+"/index.php/Interface/upload_sample";

    /**
     * 产品类别
     */
    public static String GOODSCLASS=BASE_URL+"/Interface/get_goods_class";

    public static String SETUPPROUCT="http://app.08138.com/mmm/interface.php?action=judge&door=kafu&key=";

    public static String PROUCT_CID="http://app.08138.com/mmm,/interface.php?action=read&name=kafu";
    /**
     * 一键联系电话
     */
    public final static String TwoContactPhone = "13927273545";

    public static String address="address";
    public static final int FROMLOG= 0X005;

    public static final String isSELECTADDRESS="isSELECTADDRESS";
    public static final int FROMADDRESS = 0X003;


    /** 点出来的灯的序号 */
    public static int mLightIndex = 0;

    /** 视频地址 */
    public static final String VIDEO_URL = "http://7xt9qi.com1.z0.glb.clouddn.com/job_kafu2";

}
