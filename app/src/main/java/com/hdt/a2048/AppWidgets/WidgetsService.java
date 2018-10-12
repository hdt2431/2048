package com.hdt.a2048.AppWidgets;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hdt.a2048.Activity.MainFragment;

public class WidgetsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        int score = intent.getIntExtra("score",0);
        sendBroadcast(new Intent("com.stone.action.start").putExtra("score", score));
        return Service.START_STICKY;
    }
}
