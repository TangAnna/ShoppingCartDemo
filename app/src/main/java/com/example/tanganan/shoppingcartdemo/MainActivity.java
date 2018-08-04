package com.example.tanganan.shoppingcartdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.tanganan.shoppingcartdemo.model.Merchant;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private List<Merchant> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.lv_main);
        initData();
        setData();

    }

    private void setData() {
        mListView.setAdapter(new CommonAdapter<Merchant>(this, R.layout.item_main_merchant, mData) {
            @Override
            protected void convert(ViewHolder viewHolder, Merchant item, int position) {
                viewHolder.setText(R.id.tv_name, item.name);
                ImageView icon = (ImageView) viewHolder.getView(R.id.iv_icon);
                Glide.with(MainActivity.this).load(item.url).into(icon);
            }
        });
        mListView.setOnItemClickListener(this);
    }

    /**
     * 造数据
     */
    private void initData() {
        mData = new ArrayList<>();
        Merchant m1 = new Merchant();
        m1.name = "必胜客";
        m1.url = "https://fuss10.elemecdn.com/1/72/3c54d8b5a9b582555bc9e97430acfpng.png?imageMogr2/thumbnail/95x95/format/webp/quality/85";
        mData.add(m1);

        Merchant m2 = new Merchant();
        m2.name = "麦当劳";
        m2.url = "https://fuss10.elemecdn.com/6/89/a039e8ae3804f9e3ab0f6eb6f1428jpeg.jpeg?imageMogr2/thumbnail/95x95/format/webp/quality/85";
        mData.add(m2);


        Merchant m3 = new Merchant();
        m3.name = "西贝";
        m3.url = "https://fuss10.elemecdn.com/c/1a/4838f860109657aff75e238ee519djpeg.jpeg?imageMogr2/thumbnail/95x95/format/webp/quality/85";
        mData.add(m3);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, MerchantActivity.class));
    }
}
