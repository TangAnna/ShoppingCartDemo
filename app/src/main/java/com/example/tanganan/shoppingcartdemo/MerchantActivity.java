package com.example.tanganan.shoppingcartdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tanganan.shoppingcartdemo.model.Commodity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MerchantActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private List<Commodity> mData;
    private List<Commodity> mShoppingCart;
    private TextView mTvCount;
    private CommonAdapter mAdapter;
    private RelativeLayout mLayoutRoot;
    private ImageView mIvShopCat;
    private TextView mTvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        mListView = findViewById(R.id.lv_body);
        mTvCount = findViewById(R.id.tv_mercheat_count);
        mIvShopCat = findViewById(R.id.iv_shoppingCart);
        mLayoutRoot = findViewById(R.id.layout_root);
        mTvTotalPrice = findViewById(R.id.tv_totalPrice);
        findViewById(R.id.layout_show_shoppingCart).setOnClickListener(this);
        findViewById(R.id.btn_settlement).setOnClickListener(this);
        initData();
        setData();

    }

    private void setData() {
        mListView.setAdapter(new CommonAdapter<Commodity>(this, R.layout.item_commodity, mData) {
            @Override
            protected void convert(ViewHolder viewHolder, final Commodity item, int position) {
                viewHolder.setText(R.id.tv_commodity_name, item.name);
                viewHolder.setText(R.id.tv_commodity_price, "￥" + item.price);
                ImageView imageView = viewHolder.getView(R.id.iv_img);
                Glide.with(MerchantActivity.this).load(item.icon).into(imageView);
                //是否显示减号和数量
                if (item.count != 0) {
                    viewHolder.getView(R.id.iv_less).setVisibility(View.VISIBLE);
                    viewHolder.getView(R.id.tv_count).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.tv_count, item.count + "");
                } else {
                    viewHolder.getView(R.id.iv_less).setVisibility(View.GONE);
                    viewHolder.getView(R.id.tv_count).setVisibility(View.GONE);
                }
                //添加购物车
                viewHolder.getView(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.count++;
                        addShoppingCart(view, item);
                        mAdapter.notifyDataSetChanged();
                    }
                });

                viewHolder.getView(R.id.iv_less).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.count--;
                        lessShoppingCart(item);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        mAdapter = (CommonAdapter) mListView.getAdapter();
    }

    /**
     * 添加购物车
     */
    public void addShoppingCart(View view, Commodity item) {
        for (int i = 0; i < mShoppingCart.size(); i++) {
            if (item.id == mShoppingCart.get(i).id) {
                mShoppingCart.remove(i);
            }
        }
        mShoppingCart.add(item);
        setShoppingCartAnimetor(view);
        setBottomData();
    }

    /**
     * 删除商品
     *
     * @param item
     */
    public void lessShoppingCart(Commodity item) {
        if (item.count == 0) {
            mShoppingCart.remove(item);
        } else {
            //替换
            for (int i = 0; i < mShoppingCart.size(); i++) {
                if (item.id == mShoppingCart.get(i).id) {
                    mShoppingCart.remove(mShoppingCart.get(i));
                }
            }
            mShoppingCart.add(item);
        }

        setBottomData();
    }

    /**
     * 设置底部数据
     */
    public void setBottomData() {
        if (mShoppingCart.size() == 0) {
            mTvCount.setVisibility(View.GONE);
            mTvTotalPrice.setText("0.00");
        } else {
            mTvCount.setVisibility(View.VISIBLE);
            long totalCount = 0;
            double totalPrice = 0;
            for (int i = 0; i < mShoppingCart.size(); i++) {
                totalCount += mShoppingCart.get(i).count;
                totalPrice += mShoppingCart.get(i).price * mShoppingCart.get(i).count;
            }
            mTvCount.setText(totalCount + "");
            mTvTotalPrice.setText("￥ "+totalPrice);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_show_shoppingCart://查看购物车
                if (mShoppingCart.size() != 0) {
                    showCar(mShoppingCart);
                } else {
                    Toast.makeText(this, "购物车无数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_settlement://去结算
                Toast.makeText(this, "去结算", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * show 弹窗
     */
    public void showCar(List<Commodity> data) {
        DialogShopCart mDialog = new DialogShopCart(this, R.style.cartdialog);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.show();
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.dimAmount = 0.5f;
        window.setAttributes(params);
        mDialog.setData(data);
    }

    /**
     * 添加购物车的动画
     *
     * @param view
     */
    public void setShoppingCartAnimetor(View view) {
        //贝塞尔起始数据点
        int[] startPosition = new int[2];
        //贝塞尔结束数据点
        int[] endPosition = new int[2];
        //控制点
        int[] recyclerPosition = new int[2];

        view.getLocationInWindow(startPosition);
        mIvShopCat.getLocationInWindow(endPosition);
        mLayoutRoot.getLocationInWindow(recyclerPosition);

        PointF startF = new PointF();
        PointF endF = new PointF();
        PointF controllF = new PointF();

        startF.x = startPosition[0];
        startF.y = startPosition[1] - recyclerPosition[1];
        endF.x = endPosition[0];
        endF.y = endPosition[1] - recyclerPosition[1];
        controllF.x = endF.x;
        controllF.y = startF.y;

        final ImageView imageView = new ImageView(this);
        mLayoutRoot.addView(imageView);
        imageView.setImageResource(R.mipmap.add);
        imageView.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.shopping_cart_width);
        imageView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.shopping_cart_height);
        imageView.setVisibility(View.VISIBLE);
        imageView.setX(startF.x);
        imageView.setY(startF.y);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierTypeEvaluator(controllF), startF, endF);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
            }
        });


        ObjectAnimator objectAnimatorX = new ObjectAnimator().ofFloat(mIvShopCat, "scaleX", 0.6f, 1.0f);
        ObjectAnimator objectAnimatorY = new ObjectAnimator().ofFloat(mIvShopCat, "scaleY", 0.6f, 1.0f);
        objectAnimatorX.setInterpolator(new AccelerateInterpolator());
        objectAnimatorY.setInterpolator(new AccelerateInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimatorX).with(objectAnimatorY).after(valueAnimator);
        set.setDuration(800);
        set.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setVisibility(View.GONE);
                mLayoutRoot.removeView(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initData() {
        mData = new ArrayList<>();
        mShoppingCart = new ArrayList<>();
        Commodity c1 = new Commodity();
        c1.name = "浓情烤翅";
        c1.icon = "https://fuss10.elemecdn.com/1/e0/354a2fdda861ad64610f2960fff93jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c1.price = 15.00;
        c1.id = 1;
        mData.add(c1);

        Commodity c2 = new Commodity();
        c2.name = "美式大薯格";
        c2.icon = "https://fuss10.elemecdn.com/d/13/42e186cb4274d2c1d6122de8365f5jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c2.price = 20.00;
        c2.id = 2;
        mData.add(c2);

        Commodity c3 = new Commodity();
        c3.name = "黄金夏威夷比萨套餐";
        c3.icon = "https://fuss10.elemecdn.com/6/c2/db40239a83098e3f333f976dec017jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c3.price = 73.00;
        c3.id = 3;
        mData.add(c3);

        Commodity c4 = new Commodity();
        c4.name = "鸡茸蘑菇汤";
        c4.icon = "https://fuss10.elemecdn.com/a/f4/605fda6430d3bccb5d69411f20794jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c4.price = 23.00;
        c4.id = 4;
        mData.add(c4);

        Commodity c5 = new Commodity();
        c5.name = "肉酱意面套餐";
        c5.icon = "https://fuss10.elemecdn.com/5/fd/cb366e8066247de6ef7e00ce1e9cejpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c5.price = 74.00;
        c5.id = 5;
        mData.add(c5);

        Commodity c6 = new Commodity();
        c6.name = "新奥尔良烤肉比萨s[芝心][10寸]";
        c6.icon = "https://fuss10.elemecdn.com/8/42/b8a87807bc8799a824db74a50b8c1jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c6.price = 91.00;
        mData.add(c6);

        Commodity c7 = new Commodity();
        c7.name = "冰沙买三送一";
        c7.icon = "https://fuss10.elemecdn.com/8/7c/d8764d02d07a31a1c5875417e3903jpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85";
        c7.price = 101.00;
        mData.add(c7);


    }
}
