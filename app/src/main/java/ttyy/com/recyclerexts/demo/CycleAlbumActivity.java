package ttyy.com.recyclerexts.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.recycle_album.ReCycleAlbumCallback;
import ttyy.com.recyclerexts.recycle_album.ReCycleAlbumLayoutManager;

/**
 * Author: Administrator
 * Date  : 2016/12/27 14:03
 * Name  : DragSwapActivity
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class CycleAlbumActivity extends AppCompatActivity {

    RecyclerView recycler_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new ReCycleAlbumLayoutManager());

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

        ItemTouchHelper helper = new ItemTouchHelper(new ReCycleAlbumCallback(adapter));
        helper.attachToRecyclerView(recycler_view);

    }
}
