package com.example.helloworld.rv;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;

import java.util.ArrayList;

public class CoverFlowActivity extends Activity {
    private ArrayList<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowlayout);

        generateDatas();
        RecyclerView mRecyclerView = findViewById(R.id.linear_recycler_view);

        //线性布局
        mRecyclerView.setLayoutManager(new CoverFlowLayoutManager());

        CoverFlowAdapter adapter = new CoverFlowAdapter(this, mDatas);
        mRecyclerView.setAdapter(adapter);
    }

    private void generateDatas() {
        for (int i = 0; i < 200; i++) {
            mDatas.add("第 " + i + " 个item");
        }
    }
}
