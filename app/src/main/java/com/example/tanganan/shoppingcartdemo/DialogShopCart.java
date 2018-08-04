package com.example.tanganan.shoppingcartdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tanganan.shoppingcartdemo.model.Commodity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 购物车详情
 * Created by TangAnna on 2018/7/11.
 */
public class DialogShopCart extends Dialog implements View.OnClickListener {

    private List<Commodity> mData;
    private LinearLayout mLayoutTop;
    private ListView mListView;
    private TextView mTvCount;
    private TextView mTvTotalPrice;

    public DialogShopCart(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_shopping_car);
        mLayoutTop = findViewById(R.id.layout_dialog_top);
        mListView = findViewById(R.id.rlv_dialog_list);
        mTvCount = findViewById(R.id.tv_mercheat_count);
        mTvTotalPrice = findViewById(R.id.tv_totalPrice);
        findViewById(R.id.layout_show_shoppingCart).setOnClickListener(this);

        mData = new ArrayList<>();
        mListView.setAdapter(new CommonAdapter<Commodity>(getContext(), R.layout.item_shopping_cart, mData) {
            @Override
            protected void convert(ViewHolder viewHolder, Commodity item, int position) {
                viewHolder.setText(R.id.tv_item_shopping_name, item.name);
                viewHolder.setText(R.id.tv_item_shopping_price, "￥ " + item.price);
                viewHolder.setText(R.id.tv_item_shopping_count, item.count + "");
            }
        });
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<Commodity> data) {
        mData.clear();
        mData.addAll(data);
        mTvCount.setVisibility(View.VISIBLE);
        long totalCount = 0;
        double totalPrice = 0;
        for (int i = 0; i < data.size(); i++) {
            totalCount += data.get(i).count;
            totalPrice += data.get(i).price * data.get(i).count;
        }
        mTvCount.setText(totalCount + "");
        mTvTotalPrice.setText("￥ "+totalPrice);
    }

    @Override
    public void show() {
        super.show();
        animationShow(500);
    }

    @Override
    public void dismiss() {
        animationHide(500);
    }


    private void animationShow(int mDuration) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mLayoutTop, "translationY", 1000, 0).setDuration(mDuration)
        );
        animatorSet.start();
    }

    private void animationHide(int mDuration) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mLayoutTop, "translationY", 0, 1000).setDuration(mDuration)
        );
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                DialogShopCart.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_show_shoppingCart:
                dismiss();
                break;
        }
    }
}
