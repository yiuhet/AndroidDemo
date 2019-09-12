package com.yiuhet.widget.listview;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yiuhet on 2019/7/3.
 * <p>
 * RecyclerView 万能适配器
 *
 *
 * example:
 * private QuickAdapter mAdapter = new QuickAdapter<Integer>(data) {
 *         @Override
 *         public int getLayoutId(int viewType) {
 *             switch (viewType) {
 *                 case type_1:
 *                     return R.layout_item_1;
 *                 case type_2:
 *                     return R.layout_item_2;
 *             }
 *             return 0;
 *         }
 *
 *         @Override
 *         public int getItemViewType(int position) {
 *             return super.getItemViewType(position);
 *         }
 *
 *         @Override
 *         public void convert(VH holder, Integer data, int position) {
 *             int type = getItemViewType(position);
 *             switch (type) {
 *                 case type_1:
 *                     holder.setText();
 *                     break;
 *                 case type_2:
 *                     holder.setText();
 *                     break;
 *             }
 *
 *         }
 *
 *     };
 */
public abstract class QuickAdapter<T> extends RecyclerView.Adapter<QuickAdapter.VH> {

    private List<T> mDatas;

    public QuickAdapter(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public abstract int getLayoutId(int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return VH.get(parent, getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //bind操作
    public abstract void convert(VH holder, T data, int position);

    static class VH extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertview;

        private VH(View itemView) {
            super(itemView);
            mConvertview = itemView;
            mViews = new SparseArray<>();
        }

        public static VH get(ViewGroup parent, int layoutId) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new VH(convertView);
        }

        public <T extends View> T getView(int id) {
            View v = mViews.get(id);
            if (v == null) {
                v = mConvertview.findViewById(id);
                mViews.put(id, v);
            }
            return (T) v;
        }

        public void setText(int id, String text) {
            TextView textView = getView(id);
            textView.setText(text);
        }

    }
}