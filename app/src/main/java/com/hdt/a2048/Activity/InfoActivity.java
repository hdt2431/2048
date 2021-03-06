package com.hdt.a2048.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.hdt.a2048.R;


public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initToolbar();
        TextView textView = findViewById(R.id.info_text);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    //初始化toolbar
    private void initToolbar() {
        //绑定toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //找到toolbar标签
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        //将toolbar放到页面顶部
        setSupportActionBar(mToolbar);
        //设置左上角的图标可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //左上角返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //使自定义的普通View能在title栏显示
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBarTextView.setText(R.string.menu_info);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        //给返回的按钮设置点击监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此函数系统原有  使用结束当前页面
                onBackPressed();
            }
        });
    }
}
