package com.hdt.a2048.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hdt.a2048.AppWidgets.WidgetsService;
import com.hdt.a2048.R;
import com.hdt.a2048.Utils.MyMenuDialogFragment;
import com.hdt.a2048.Utils.SysApplication;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.hdt.a2048.Utils.TimeUtil.*;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener, Runnable {

    private MainFragment mainFragment;
    private long firsttime; // 监听两次返回
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    private volatile boolean isPause = false;//是否暂停
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);
        SysApplication.getInstance().setActivity(this);
        Intent intent = getIntent();
        String data = intent.getStringExtra("flag");

        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

        mainFragment = new MainFragment();
        mainFragment.setFlag(data);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                mainFragment.getTextView().setText((String) msg.obj);
            }
        };
        new Thread(this).start();
        // if(!mainFragment.isAdded())
        addFragment(mainFragment, true, R.id.container);
    }

    public void run() {
        try {
            while (!isPause) {
                String str = getFormatHMS(mainFragment.getCurrentSecond());
                handler.sendMessage(handler.obtainMessage(100, str));
                mainFragment.setCurrentSecond(mainFragment.getCurrentSecond() + 1000);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isPause = false;
        new Thread(this).start();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("back", "1");
        startActivity(i);
        finish();
        SysApplication.getInstance().exit();
    }

    //初始化menufragment
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = MyMenuDialogFragment.newInstance(menuParams);
    }


    //获取数据
    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);


        MenuObject restart = new MenuObject(getResources().getString(R.string.menu_restart));
        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        restart.setResource(R.drawable.restart);
        MenuObject see = new MenuObject(getResources().getString(R.string.menu_charts));
//        BitmapDrawable bd = new BitmapDrawable(getResources(),
//                BitmapFactory.decodeResource(getResources(), R.drawable.eyes));
        see.setResource(R.drawable.eyes);

        MenuObject about_autor = new MenuObject(getResources().getString(R.string.menu_info));
        about_autor.setResource(R.drawable.illustration);

        MenuObject exit = new MenuObject(getResources().getString(R.string.menu_exit));
        exit.setResource(R.drawable.exit);

        menuObjects.add(close);
        menuObjects.add(restart);
        menuObjects.add(see);
        menuObjects.add(about_autor);
        menuObjects.add(exit);
        return menuObjects;
    }

    //初始化toolbar
    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBarTextView.setText("2048");
    }

    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.context_menu:

                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null&&!mMenuDialogFragment.isAdded()) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Intent i;
        switch (position) {

            //取消
            case 0:
                break;
            case 1:
                mainFragment.startGame();
                break;

            //我要看榜
            case 2:
                i = new Intent(this, ChartsActivity.class);
                startActivity(i);
                break;

            //关于作者
            case 3:
                i = new Intent(this, InfoActivity.class);
                startActivity(i);
                break;

            //退出游戏
            case 4:
                SysApplication.getInstance().exit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    //点击两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - firsttime < 3000) {
                finish();
                return true;
            } else {
                firsttime = System.currentTimeMillis();
                Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainFragment.getGameView().updateCards();
//        Log.e("cc11",mainFragment.getGameView().queryCards());
//        Log.e("cc11",String.valueOf(mainFragment.getGameView().queryTime()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {//存在Bundle数据,去除fragments的状态保存，解决Fragme错乱问题。
            String FRAGMENTS_TAG = "android:support:fragments";
            outState.remove(FRAGMENTS_TAG);
        }

    }

    public void setPause(boolean pause) {
        isPause = pause;
    }
}
