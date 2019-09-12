package com.yiuhet.widget.listview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yiuhet on 2019/7/3.
 * <p>
 * 对原有Adapter拓展headerView 和 footerView功能
 */
public class NormalAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    enum ITEM_TYPE {
        HEADER,
        FOOTER,
        NORMAL
    }

    private NormalAdapter mAdapter;
    private View mHeadView;
    private View mFooterView;

    public NormalAdapterWrapper(NormalAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.HEADER.ordinal();
        } else if (position == mAdapter.getItemCount() + 1) {
            return ITEM_TYPE.FOOTER.ordinal();
        } else {
            return ITEM_TYPE.NORMAL.ordinal();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.HEADER.ordinal()) {
            return new RecyclerView.ViewHolder(mHeadView) {
            };
        } else if (viewType == ITEM_TYPE.FOOTER.ordinal()) {
            return new RecyclerView.ViewHolder(mHeadView) {
            };
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            return;
        } else if (position == mAdapter.getItemCount() + 1) {
            return;
        } else {
            mAdapter.onBindViewHolder(holder, position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 2;
    }

    public void addHeaderView(View view) {
        this.mHeadView = view;
    }

    public void addFooterView(View view) {
        this.mFooterView = view;
    }
}
