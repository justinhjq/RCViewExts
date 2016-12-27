package ttyy.com.recyclerexts.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Author: hjq
 * Date  : 2016/12/22 21:17
 * Name  : MainActivity
 * Intro : Edit By hjq
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/22    hjq   1.0              1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.tv_tags_vertical:
                intent = new Intent(this, TagsVerticalActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tags_horizontal:
                intent = new Intent(this, TagsHorizontalActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_drag_swap:
                intent = new Intent(this, DragSwapActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_swipe:
                intent = new Intent(this, SwipeDeleteActivity.class);
                startActivity(intent);
                break;
        }
    }
}
