package com.hdt.a2048.Utils;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class SysApplication extends Application {
    private List<Activity> mList = new ArrayList<>();
    private static SysApplication instance;
    private Activity activity;
    private SysApplication(){}
    public static synchronized SysApplication getInstance(){
        if (null == instance){
            instance = new SysApplication();
        }
        return instance;
    }

    //将启动的进程添加进入list中
    public void addActivity(Activity activity){
        mList.add(activity);
    }

    //将list中的activity全部销毁
    public void exit(){
        for(Activity activity:mList){
            if (activity != null){
                activity.finish();
            }
        }
    }


    //杀进程
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
