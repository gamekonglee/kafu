package cc.bocang.bocang.utils.net;

import cc.bocang.bocang.global.Constant;

/**
 * @author: Jun
 * @date : 2017/8/4 15:55
 * @description :
 */
public class NetWorkConst {

    /**
     * 产品列表
     */
    public static final String PRODUCTLIST_URL = Constant.BASE_URL+"/Interface/get_goods_list" ;
    public static final int WITH_PRODUCTLIST_URL=0x001;

    /**
     * 产品分类
     */
    public static final String PRODUCT_CLASS_URL = Constant.BASE_URL + "/Interface/get_goods_class2";
    public static final int WITH_PRODUCT_CLASS_URL = 0x004;

    /**
     * 场景列表
     */
    public static final String SCENETLIST_URL =Constant.BASE_URL+"/Interface/get_scene_list" ;
    public static final int WITH_SCENELIST_URL=0x002;

    public static final String SCENETLIST_CLASS_URL =Constant.BASE_URL+"/index.php/Interface/get_scene_class";
    public static final int WITH_SCENETLIST_CLASS_URL=0x003;
}
