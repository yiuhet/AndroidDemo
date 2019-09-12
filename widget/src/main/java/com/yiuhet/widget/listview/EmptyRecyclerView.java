package com.yiuhet.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yiuhet on 2019/7/3.
 * <p>
 * 有空视图的RecyclerView
 */
public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            if (adapter.getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
                EmptyRecyclerView.this.setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(GONE);
                EmptyRecyclerView.this.setVisibility(GONE);
            }
            super.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }
    };

    public EmptyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public EmptyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        ((ViewGroup) this.getRootView()).addView(mEmptyView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
        mObserver.onChanged();
    }
}
