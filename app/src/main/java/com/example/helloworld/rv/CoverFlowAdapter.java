package com.example.helloworld.rv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;

import java.util.ArrayList;

public class CoverFlowAdapter extends RecyclerView.Adapter<CoverFlowAdapter.NormalHolder> {

    private final Context mContext;
    private final ArrayList<String> mDatas;
    private int mCreatedHolder=0;
    private int[] mPics = {R.mipmap.item1,R.mipmap.item2,R.mipmap.item3,R.mipmap.item4,
            R.mipmap.item5,R.mipmap.item6};
    public CoverFlowAdapter(Context context, ArrayList<String> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mCreatedHolder++;
        Log.d("qijian", "onCreateViewHolder num:"+mCreatedHolder);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new NormalHolder(inflater.inflate(R.layout.item_coverflow, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalHolder holder, int position) {
        Log.d("qijian", "onBindViewHolder");
        NormalHolder normalHolder = holder;
        normalHolder.mTV.setText(mDatas.get(position));
        normalHolder.mImg.setImageDrawable(mContext.getResources().getDrawable(mPics[position%mPics.length]));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        public TextView mTV;
        public ImageView mImg;

        public NormalHolder(View itemView) {
            super(itemView);

            mTV = itemView.findViewById(R.id.text);
            mTV.setOnClickListener(v -> Toast.makeText(mContext, mTV.getText(), Toast.LENGTH_SHORT).show());

            mImg = itemView.findViewById(R.id.img);
            mImg.setOnClickListener(v -> Toast.makeText(mContext, mTV.getText(), Toast.LENGTH_SHORT).show());

        }
    }
}
