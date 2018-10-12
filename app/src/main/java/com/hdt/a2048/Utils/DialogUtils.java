package com.hdt.a2048.Utils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hdt.a2048.Activity.LoginActivity;
import com.hdt.a2048.Activity.MainActivity;
import com.hdt.a2048.Activity.MainFragment;
import com.hdt.a2048.Data.MySqlHelper;
import com.hdt.a2048.Model.Gamer;
import com.hdt.a2048.R;


public class DialogUtils {
    private static final int DATABASES_VERSION = 3;

    public static void getAddChartDialog(final Context context, final int score, final String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflate = LayoutInflater.from(context);
        View v = inflate.inflate(R.layout.dialog_charts, null);
        final EditText editText = (EditText) v.findViewById(R.id.et_name);

        builder.setTitle(R.string.dialog_title).setView(v).setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //插入数据
                Context friendContext = null;
                try {
                    friendContext = context.createPackageContext("com.hdt.datasfor2048",
                            Context.CONTEXT_IGNORE_SECURITY);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
                SQLiteDatabase db = mySqlHelper.getWritableDatabase();
                Cursor cursor = db.query("charts", null, null, null, null, null, "user_score desc");
                if (cursor.getCount() == 10) {
                    cursor.moveToLast();
                    db.delete("charts", "id = ?", new String[]{String.valueOf(cursor.getInt(0))});
                }
                ContentValues values = new ContentValues();
                values.put("user_name", editText.getText().toString());
                values.put("user_score", score);
                values.put("time", time);
                db.insert("charts", "id", values);
                db.delete("gamestate", "name = ?", new String[]{"state1"});

                db.close();
                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("back", "1");
                context.startActivity(i);
                SysApplication.getInstance().exit();


            }
        }).setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context friendContext = null;
                try {
                    friendContext = context.createPackageContext("com.hdt.datasfor2048",
                            Context.CONTEXT_IGNORE_SECURITY);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
                SQLiteDatabase db = mySqlHelper.getWritableDatabase();
                db.delete("gamestate", "name = ?", new String[]{"state1"});
//                MainFragment.getMainFragment().startGame();
                db.close();
                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("back", "1");
                context.startActivity(i);
                SysApplication.getInstance().exit();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        final Button mBtnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        mBtnPositive.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    mBtnPositive.setEnabled(true);
                } else {
                    mBtnPositive.setEnabled(false);
                }
            }
        });

    }

    public static void endDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title2);
        LayoutInflater inflate = LayoutInflater.from(context);
        View v = inflate.inflate(R.layout.dialog_end, null);
        builder.setView(v);
        builder.setNegativeButton(R.string.new_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //MainFragment.getMainFragment().startGame();
               Intent i = new Intent(context, MainActivity.class);
                i.putExtra("flag","1");
                context.startActivity(i);
                SysApplication.getInstance().exit();
            }
        });
        builder.setPositiveButton(R.string.dialog_return, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context friendContext = null;
                try {
                    friendContext = context.createPackageContext("com.hdt.datasfor2048",
                            Context.CONTEXT_IGNORE_SECURITY);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                MySqlHelper mySqlHelper = new MySqlHelper(friendContext, "db2048.db", null, 1);
                SQLiteDatabase db = mySqlHelper.getWritableDatabase();
                db.delete("gamestate", "name = ?", new String[]{"state1"});
//                MainFragment.getMainFragment().startGame();
                db.close();
                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("back", "1");
                context.startActivity(i);
                SysApplication.getInstance().exit();
            }
        });
        //dialog属性操作
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public static void getOpenDialog(final Context context, Gamer gamer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_open_title);
        LayoutInflater inflate = LayoutInflater.from(context);
        View v = inflate.inflate(R.layout.dialog_open, null);
        //设置布局
        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
        TextView tv_score = (TextView) v.findViewById(R.id.tv_score);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
        tv_name.setText("姓名： " + gamer.getName());
        tv_score.setText("分数： " + gamer.getScore());
        tv_time.setText("时间： " + gamer.getTime());
        builder.setView(v);
        builder.setPositiveButton("确定", null);

        //dialog属性操作
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
