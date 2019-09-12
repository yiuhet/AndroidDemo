package com.yiuhet.widget.listview;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yiuhet on 2019/7/3.
 * <p>
 * 普通的adapter
 */
public class NormalAdapter extends QuickAdapter {

    public NormalAdapter(List mDatas) {
        super(mDatas);
    }

    @Override
    public int getLayoutId(int viewType) {
        return 0;
    }

    @Override
    public void convert(VH holder, Object data, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
