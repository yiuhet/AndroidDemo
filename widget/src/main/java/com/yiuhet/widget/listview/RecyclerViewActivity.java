package com.yiuhet.widget.listview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yiuhet.widget.R;

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

    }

    /**
     * 为recyclerView添加顶部视图和底部视图
     */
    private void addHeaderView() {
        RecyclerView recyclerView = new RecyclerView(this);
        NormalAdapter adapter = new NormalAdapter(null);
        NormalAdapterWrapper newAdapter = new NormalAdapterWrapper(adapter);
        View headerView = new TextView(this);
        View footerView = new TextView(this);
        newAdapter.addHeaderView(headerView);
        newAdapter.addFooterView(footerView);
        recyclerView.setAdapter(newAdapter);
    }
}
