package ttyy.com.recyclerexts.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.drag_swipe_support.SimpleDragSwapCallback;
import ttyy.com.recyclerexts.drag_swipe_support.SimpleSwipeCallback;

/**
 * Author: Administrator
 * Date  : 2016/12/27 14:03
 * Name  : DragSwapActivity
 * Intro : 滑动删除
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class SwipeDeleteActivity extends AppCompatActivity {

    RecyclerView recycler_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        EXTRecyclerAdapter<String> adapter = new EXTRecyclerAdapter<String>(R.layout.item_circle_album_view) {
            @Override
            public void onBindViewHolder(EXTViewHolder holder, int position, String data) {
                holder.setText(R.id.text, data);
            }
        };

        ArrayList<String> datas = new ArrayList<>();
        Collections.addAll(datas, getResources().getStringArray(R.array.circle_album));
        adapter.setDatas(datas);

        recycler_view.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new SimpleSwipeCallback(adapter));
        helper.attachToRecyclerView(recycler_view);

    }
}
