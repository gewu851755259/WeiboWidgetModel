package com.minla.cpwb.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.minla.cpwb.R;

import java.util.ArrayList;
import java.util.List;

public class CPWBProvider extends AppWidgetProvider {

    private static final String TAG = CPWBProvider.class.getSimpleName();
    private static final String ACTION_WIDGET_CONTENT_REFRESH = "action_widget_content_refresh";

    private static int index = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.e(TAG, "---CPWBProvider--------onReceive() and action is " + action);
        switch (action) {
            case ACTION_WIDGET_CONTENT_REFRESH:
                Log.e(TAG, "---CPWBProvider--------onReceive() switch start");
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cpwb_widget_layout);
                views.setTextViewText(R.id.txt_test_content, "onReceive()中刷新，并且index = " + index);
                index++;
                refreshWidget(context, views, false);
                break;

            default:
                Log.e(TAG, "---CPWBProvider--------onReceive() switch default");
                break;
        }
    }

    /**
     * 首次使用时调用，接受到AppWidgetManager.ACTION_APPWIDGET_ENABLED后执行
     * 当桌面已经有此小工具，再次创建的时候不会调用
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        Log.e(TAG, "---CPWBProvider--------onEnabled()");
        super.onEnabled(context);
    }

    /**
     * 每创建一个该小工具，就调用一次，接受到AppWidgetManager.ACTION_APPWIDGET_UPDATE后执行
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG, "---CPWBProvider--------onUpdate()");

        for (int appWidgetId : appWidgetIds) {
            Log.e(TAG, "---CPWBProvider--------onUpdate() appWidgetIds is " + appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cpwb_widget_layout);

            // 设置广播绑定刷新按钮点击
            Intent refreshIntent = new Intent(context, CPWBProvider.class); // 从该广播调到该广播
            refreshIntent.setAction(ACTION_WIDGET_CONTENT_REFRESH);
            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.ibtn_refresh, refreshPendingIntent);

            // 设置文本内容
            views.setTextViewText(R.id.txt_test_content, "在onUpdate()中初始化" + index);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 删除一个该窗口小工具就调用一次，接受到AppWidgetManager.ACTION_APPWIDGET_DELETED后执行
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "---CPWBProvider--------onDeleted()");
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 桌面上所有该小工具删除后执行一次，接受到AppWidgetManager.ACTION_APPWIDGET_DISABLED后执行
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        Log.e(TAG, "---CPWBProvider--------onDisabled()");
        super.onDisabled(context);
    }


    /**
     * 刷新Widget内容，该方法刷新所有的窗口，而onUpdate()方法中只刷新对应appWidgetId的窗口
     *
     * @param cxt
     * @param views
     * @param refreshList 是否刷新列表内容
     */
    private void refreshWidget(Context cxt, RemoteViews views, boolean refreshList) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(cxt);
        ComponentName componentName = new ComponentName(cxt, CPWBProvider.class);
        appWidgetManager.updateAppWidget(appWidgetManager.getAppWidgetIds(componentName), views);
        if (refreshList)
            ;
    }

}
