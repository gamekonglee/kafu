package cc.bocang.bocang.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.bocang.bocang.R;
import cc.bocang.bocang.global.Constant;

/**
 * Created by gamekonglee on 2018/3/15.
 */

public class StickerView2 extends RelativeLayout {
    /**
     * 初始化状态常量
     */
    public static final int STATUS_INIT = 1;

    /**
     * 图片放大状态常量
     */
    public static final int STATUS_ZOOM_OUT = 2;

    /**
     * 图片缩小状态常量
     */
    public static final int STATUS_ZOOM_IN = 3;

    /**
     * 图片拖动状态常量
     */
    public static final int STATUS_MOVE = 4;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM=3;
    private static final int TOP=4;
    private static final int CENTER=5;
    private static final int LEFT_TOP=6;
    private static final int LEFT_BOTTOM=7;
    private static final int RIGHT_TOP=8;
    private static final int RIGHT_BOTTOM=9;
    /**
     * 用于对图片进行移动和缩放变换的矩阵
     */

    /**
     * 待展示的Bitmap对象
     */
    private Bitmap sourceBitmap;

    /**
     * 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
     */
    private int currentStatus;

    /**
     * ZoomImageView控件的宽度
     */

    /**
     * ZoomImageView控件的高度
     */

    /**
     * 记录两指同时放在屏幕上时，中心点的横坐标值
     */
    private float centerPointX;

    /**
     * 记录两指同时放在屏幕上时，中心点的纵坐标值
     */
    private float centerPointY;

    /**
     * 记录当前图片的宽度，图片被缩放时，这个值会一起变动
     */
    private float currentBitmapWidth;

    /**
     * 记录当前图片的高度，图片被缩放时，这个值会一起变动
     */
    private float currentBitmapHeight;

    /**
     * 记录上次手指移动时的横坐标
     */
    private float lastXMove = -1;

    /**
     * 记录上次手指移动时的纵坐标
     */
    private float lastYMove = -1;

    /**
     * 记录手指在横坐标方向上的移动距离
     */
    private float movedDistanceX;

    /**
     * 记录手指在纵坐标方向上的移动距离
     */
    private float movedDistanceY;

    /**
     * 记录图片在矩阵上的横向偏移值
     */
    private float totalTranslateX;

    /**
     * 记录图片在矩阵上的纵向偏移值
     */
    private float totalTranslateY;

    /**
     * 记录图片在矩阵上的总缩放比例
     */
    private float totalRatio;

    /**
     * 记录手指移动的距离所造成的缩放比例
     */
    private float scaledRatio;

    /**
     * 记录图片初始化时的缩放比例
     */
    private float initRatio;

    /**
     * 记录上次两指之间的距离
     */
    private double lastFingerDis;
    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.2f;
    //手指移动距离必须超过这个数值

    private final float pointerLimitDis = 20f;
    private final float pointerZoomCoeff = 0.09f;
    private ImageView iv_delete;
    private ImageView iv_flip;
    private ImageView iv_resize;
    private ImageView iv_top;
    private OperationListener operationListener;
    private ImageView imageView;
    private float lastx;
    private float lasty;
    private float oldDis;
    private boolean isPointerDown;
    private float oringinWidth = 220;
    private float lastLength;
    public int mLightCount;
    public boolean inEdit;
    public boolean isInEdit;
    private Bitmap bitmap;
    private int width;
    private int height;
    private int screenWidth;
    private int screenHeight;
    private int lastY;
    private int lastX;
    private int dragDirection;
    private int oriLeft;
    private int oriTop;
    private int oriRight;
    private int oriBottom;
    private int offset=20;


    public StickerView2(Context context) {
        super(context);
        init();
        mLightCount = 0;
    }

    public StickerView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mLightCount = 0;
    }

    public StickerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mLightCount = 0;
    }

    public void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        if (true) {
            return;
        }
        iv_delete = findViewById(R.id.iv_delete);
        iv_flip = findViewById(R.id.iv_flip);
        iv_resize = findViewById(R.id.iv_resize);
        iv_top = findViewById(R.id.iv_top);
        imageView = findViewById(R.id.iv_bitmap);
        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                operationListener.onDeleteClick();
            }
        });
        iv_flip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setRotationY(180);
            }
        });
        iv_top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bringToFront();
                if (operationListener != null) {
                    operationListener.onTop(StickerView2.this);
                }
            }
        });
        imageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                boolean handled = true;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastx = event.getX(0);
                        lasty = event.getY(0);
                        Constant.mLightIndex = mLightCount;// TODO
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (spacing(event) > pointerLimitDis) {
                            oldDis = spacing(event);
                            isPointerDown = true;
                            midPointToStartPoint(event);
                        } else {
                            isPointerDown = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isPointerDown) {
                            float scale;
                            float disNew = spacing(event);
                            if (disNew == 0 || disNew < pointerLimitDis) {
                                scale = 1;
                            } else {
                                scale = disNew / oldDis;
                                //缩放缓慢
                                scale = (scale - 1) * pointerZoomCoeff + 1;
                            }
                            float scaleTemp = (scale * Math.abs(iv_flip.getLeft() - iv_resize.getLeft())) / oringinWidth;
                            if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
                                    (scaleTemp >= MAX_SCALE) && scale > 1) {
                                scale = 1;
                            } else {
                                lastLength = diagonalLength(event);
                            }
//                                matrix.postScale(scale, scale, mid.x, mid.y);
                            setScaleX(scale);
                            setScaleY(scale);
//                                invalidate();
                            Log.e("move", "isPosionterDown");
                        } else {
                            float x = event.getX(0);
                            float y = event.getY(0);
                            //TODO 移动区域判断 不能超出屏幕
                            setTranslationX(x - lastx);
                            setTranslationY(y - lasty);
//                                matrix.postTranslate(x - lastX, y - lastY);
                            lastx = x;
                            lasty = y;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        isPointerDown = false;
                        break;

                }
                if (handled && operationListener != null) {
                    operationListener.onEdit(StickerView2.this);
                }
                return handled;
            }
        });
    }
    Paint paint=new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(offset, offset, getWidth() - offset, getHeight()
                - offset, paint);
    }
    public void setBitmap(Bitmap bitmap) {
//        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(imgWidth, imgHeight,
                Bitmap.Config.ARGB_8888);
        // int brightness = progress - 127;
        float contrast = (float) ((40 + 64) / 128.0);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{contrast, 0, 0, 0, 0, 0,
                contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
//            System.out.println("isNotRecycled");
        canvas.drawBitmap(bitmap, 0, 0, paint);
        imageView.setImageBitmap(bmp);
    }

    public void setInEdit(boolean inEdit) {
        this.inEdit = inEdit;
        if (inEdit) {
            iv_top.setVisibility(VISIBLE);
            iv_flip.setVisibility(VISIBLE);
            iv_delete.setVisibility(VISIBLE);
            iv_resize.setVisibility(VISIBLE);
        } else {
            iv_resize.setVisibility(INVISIBLE);
            iv_top.setVisibility(INVISIBLE);
            iv_flip.setVisibility(INVISIBLE);
            iv_delete.setVisibility(INVISIBLE);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public interface OperationListener {
        void onDeleteClick();

        void onEdit(StickerView2 stickerView);

        void onTop(StickerView2 stickerView);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);
//        boolean handled = true;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//        }
//        if (handled && operationListener != null) {
//            operationListener.onEdit(this);
//        }
//        return handled;
//    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 计算双指之间的距离
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    /**
     * 触摸的位置和图片左上角位置的中点
     *
     * @param event
     */
    private PointF mid = new PointF();

    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
//        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = getLeft() + event.getX(0);
        float f4 = getTop() + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float diagonalLength(MotionEvent event) {
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);

//        boolean handled = true;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                lastx = event.getX(0);
//                lasty = event.getY(0);
//                Constant.mLightIndex = mLightCount;// TODO
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                if (spacing(event) > pointerLimitDis) {
//                    oldDis = spacing(event);
//                    isPointerDown = true;
//                    midPointToStartPoint(event);
//                } else {
//                    isPointerDown = false;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isPointerDown) {
//                    float scale;
//                    float disNew = spacing(event);
//                    if (disNew == 0 || disNew < pointerLimitDis) {
//                        scale = 1;
//                    } else {
//                        scale = disNew / oldDis;
//                        //缩放缓慢
//                        scale = (scale - 1) * pointerZoomCoeff + 1;
//                    }
//                    float scaleTemp = (scale * Math.abs(iv_flip.getLeft() - iv_resize.getLeft())) / 200;
//                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
//                            (scaleTemp >= MAX_SCALE) && scale > 1) {
//                        scale = 1;
//                    } else {
//                        lastLength = diagonalLength(event);
//                    }
////                                matrix.postScale(scale, scale, mid.x, mid.y);
//                    setScaleX(scale);
//                    setScaleY(scale);
////                                invalidate();
//                    Log.e("move","isPosionterDown");
//                }else {
//                    float x = event.getX(0);
//                    float y = event.getY(0);
//                    //TODO 移动区域判断 不能超出屏幕
////                    view.setTranslationX(x- lastx);
////                    view.setTranslationY(y- lasty);
////                    view.layout((((int)(view.getLeft()+x))),((int)(view.getTop()+y)),((int)(view.getRight()+x)),((int)(v.getBottom()+y)));
//                    Log.e("move","layout,x:"+x+",y:"+y);
//                    layout((int)x,(int)y,(int)(x),(int)(y));
////                                matrix.postTranslate(x - lastX, y - lastY);
//                    lastx = x;
//                    lasty = y;
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                isPointerDown = false;
//                break;
//
//        }
//        if (handled && operationListener != null) {
//            operationListener.onEdit(StickerView2.this);
//        }
//        return handled;
        View v=this;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
//                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //把新的位置 oriLeft, oriTop, oriRight, oriBottom设置到控件，实现位置移动和大小变化。
                Log.e("layoutparams","oril:"+oriLeft+",top:"+oriTop);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(oriRight - oriLeft, oriBottom - oriTop);
                lp.setMargins(oriLeft,oriTop,0,0);
                setLayoutParams(lp);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
//        invalidate();
        return  true;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 分别获取到ZoomImageView的宽度和高度
            width = getWidth();
            height = getHeight();
        }
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < -offset) {
            left = -offset;
            right = left + v.getWidth();
        }
        if (right > screenWidth + offset) {
            right = screenWidth + offset;
            left = right - v.getWidth();
        }
        if (top < -offset) {
            top = -offset;
            bottom = top + v.getHeight();
        }
        if (bottom > screenHeight + offset) {
            bottom = screenHeight + offset;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 40 && bottom - top - y < 40) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }
    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offset) {
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriTop = oriBottom - 2 * offset - 200;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriBottom = 200 + oriTop + 2 * offset;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriRight = oriLeft + 2 * offset + 200;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriLeft = oriRight - 2 * offset - 200;
        }
    }
}
