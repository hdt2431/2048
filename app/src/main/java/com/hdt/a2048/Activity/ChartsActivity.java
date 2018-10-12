package com.hdt.a2048.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

import com.hdt.a2048.Adapter.ChartsAdapter;
import com.hdt.a2048.Data.MySqlHelper;
import com.hdt.a2048.Model.Gamer;
import com.hdt.a2048.R;
import com.hdt.a2048.Utils.DialogUtils;

import java.util.ArrayList;

public class ChartsActivity extends AppCompatActivity {

    //滑动item
    private SwipeMenuListView mListView; //排行榜列表
    private ChartsAdapter mAdapter; //排行榜列表适配器
    private ArrayList<Gamer> mList = new ArrayList<Gamer>(); //排行榜列表数据
    private static final int DATABASES_VERSION = 3;
    private Context friendContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //must
        //设置页面布局
        setContentView(R.layout.activity_charts);
        //首先将数据放进list
        getData();
        //绑定listview
        mListView = (SwipeMenuListView) findViewById(R.id.chartsListView);
        //初始化适配器
        mAdapter = new ChartsAdapter(this, mList);
        //给listview设置适配器
        mListView.setAdapter(mAdapter);
        //初始化toolbar
        initToolbar();
        //开源库控件设置
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // 新建一个open模块
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // 新建一个删除模块
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //把  creator 与listview连接
        mListView.setMenuCreator(creator);
        final Context context = this;
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //第一个图标按钮

                        //显示用户信息dialog
                        DialogUtils.getOpenDialog(context, mList.get(position));
                        break;
                    case 1:
                        //第二个图标按钮

                        //首先拿到数据库对象
                        try {
                            friendContext = context.createPackageContext("com.hdt.datasfor2048",
                                    Context.CONTEXT_IGNORE_SECURITY);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
                        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
                        //在数据库中删除这条数据
                        db.delete("charts", "id = ?", new String[]{String.valueOf(mList.get(position).getId())});
                        //在当前显示的list中删除这条数据
                        mList.remove(position);
                        //更新listview
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent i = new Intent(this, LoginActivity.class);
//        i.putExtra("back","1");
//        startActivity(i);
        finish();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBarTextView.setText(R.string.start_charts);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //获取排行榜数据
    private void getData() {
        //数据库helper（上下文  数据库名称 数据库工厂 数据库版本）
        try {
            friendContext = this.createPackageContext("com.hdt.datasfor2048",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getWritableDatabase();
        //查询数据

        /**
         * table：表名。相当于select语句from关键字后面的部分。如果是多表联合查询，可以用逗号将两个表名分开。
         * columns：要查询出来的列名。相当于select语句select关键字后面的部分。
         * selection：查询条件子句，相当于select语句where关键字后面的部分，在条件子句允许使用占位符“？”
       　* selectionArgs：对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常。
         * groupBy：相当于select语句groupby关键字后面的部分
         * having：相当于select语句having关键字后面的部分
         * orderBy：相当于select语句orderby关键字后面的部分
         */

        Cursor cursor = db.query("charts", null, null, null, null, null, "user_score desc");//列名称  倒排序
        while (cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndex("user_name");
            int scoreIndex = cursor.getColumnIndex("user_score");
            int idIndex = cursor.getColumnIndex("id");
            int timeIndex = cursor.getColumnIndex("time");
            String time = cursor.getString(timeIndex);
            String name = cursor.getString(nameIndex);
            int score = cursor.getInt(scoreIndex);
            int id = cursor.getInt(idIndex);
            mList.add(new Gamer(id, name, score,time));
        }
       // Log.e("list",String.valueOf(mList.size()));
        db.close();
    }

    //dp换算成px
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
