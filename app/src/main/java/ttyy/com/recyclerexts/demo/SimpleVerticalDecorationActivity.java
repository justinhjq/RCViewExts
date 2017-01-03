package ttyy.com.recyclerexts.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.idcs.SimpleVerticalItemDecoration;

/**
 * Author: Administrator
 * Date  : 2016/12/28 18:05
 * Name  : SimpleVerticalDecorationActivity
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/28    Administrator   1.0              1.0
 */
public class SimpleVerticalDecorationActivity extends AppCompatActivity {

    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_vertical);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.addItemDecoration(new SimpleVerticalItemDecoration());

        EXTRecyclerAdapter<String> adapter = new EXTRecyclerAdapter<String>(R.layout.item_tag_view) {
            @Override
            public void onBindViewHolder(EXTViewHolder holder, int position, String data) {
                holder.setText(R.id.text, data);
            }
        };

        ArrayList<String> datas = new ArrayList<>();
        datas.add("1");
        datas.add("2");
        datas.add("3");
        adapter.setDatas(datas);

        recycler_view.setAdapter(adapter);

    }
}
