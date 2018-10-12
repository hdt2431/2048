package com.hdt.a2048.AppWidgets;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hdt.a2048.Activity.LoginActivity;
import com.hdt.a2048.Activity.MainActivity;
import com.hdt.a2048.Activity.MainFragment;
import com.hdt.a2048.Activity.SplashActivity;
import com.hdt.a2048.R;

public class AppWidgetProviderFor2048 extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        Log.e("aaa","onUpdate");
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context,SplashActivity.class).putExtra("score",333);
            intent.setAction("com.stone.action.start");
            //context.startService(intent);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //用于接收指定意图,处理相关需求,可以重写onRecrive(),如我们收到一个toast的动作时,显示一条Toast
        Log.e("aaa","onReceive");
        super.onReceive(context, intent);
//        if (intent.getAction().equals("com.stone.action.start")) {
//
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
//            int s = 0;
//            if(intent.getIntExtra("score",0)!=0) {
//                s = intent.getIntExtra("score", 0);
//                Log.e("aaa",s+""+333);
//            }
//            if(s!=0){
//                views.setTextViewText(R.id.button, s+"");
//                Log.e("aaa",s+"");
//            }else {
//                views.setTextViewText(R.id.button, s+"");
//                Log.e("aaa",s+"");
//            }
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            ComponentName componentName = new ComponentName(context,AppWidgetProviderFor2048.class);
//            appWidgetManager.updateAppWidget(componentName, views);
//
//        }



    }


}
