package com.minla.cpwb.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.minla.cpwb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yml.com.baseapplib.views.VerticalSlidingView;

public class EmptyActivtiy extends AppCompatActivity {

    private VerticalSlidingView mVertivalSlidingView;
    private TextView mSearchCountText;
    private ListView mListView;
    private List<Map<String, ?>> mDatas = new ArrayList<>();
    private SimpleAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty);

        mVertivalSlidingView = findViewById(R.id.vsv_test);
        mSearchCountText = mVertivalSlidingView.getTitleView().findViewById(R.id.tv_search_count_txt);
        mListView = mVertivalSlidingView.getBodyView().findViewById(R.id.lv_search_diming_result);

        mSearchCountText.setText("搜索到无数条结果");

        for (int i = 0; i < 20; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("content", "这是第" + (i + 1) + "条数据");
            mDatas.add(map);
        }
        mAdapter = new SimpleAdapter(this, mDatas, android.R.layout.simple_list_item_1,
                new String[]{"content"}, new int[]{android.R.id.text1});
        mListView.setAdapter(mAdapter);
    }
}
