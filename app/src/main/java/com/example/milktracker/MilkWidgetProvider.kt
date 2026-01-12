// Path: app/src/main/java/com/example/milktracker/MilkWidgetProvider.kt
package com.example.milktracker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.util.Calendar

class MilkWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_milk_tracker)
            
            // Simple Logic: Check time
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val status = if (hour in 6..10) "Morning Entry Open" 
                         else if (hour in 17..21) "Evening Entry Open" 
                         else "Next: Tomorrow"
            
            views.setTextViewText(R.id.widget_status_text, status)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
