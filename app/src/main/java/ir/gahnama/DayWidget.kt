package ir.gahnama

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDate

/** ویجت صفحهٔ اصلی: عدد روز داخل مربع گوشه‌گرد، ماه شمسی، روز هفته و تاریخ میلادی. با لمس، برنامه باز می‌شود. */
class DayWidget : AppWidgetProvider() {
    override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) = Notifier.refresh(c)

    companion object {
        fun update(c: Context) {
            val m = AppWidgetManager.getInstance(c)
            val ids = m.getAppWidgetIds(ComponentName(c, DayWidget::class.java))
            if (ids.isEmpty()) return
            Prefs.init(c)
            val i = Dates.info(LocalDate.now(), Prefs.offset)
            val v = RemoteViews(c.packageName, R.layout.widget_day).apply {
                setTextViewText(R.id.w_day, i.fa.d.fa())
                setTextViewText(R.id.w_weekday, i.weekday)
                setTextViewText(R.id.w_month, "${Dates.faMonths[i.fa.m - 1]} ${i.fa.y.fa()}")
                setTextViewText(R.id.w_greg, i.greg.label())
                setOnClickPendingIntent(R.id.w_root, PendingIntent.getActivity(c, 2, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            }
            m.updateAppWidget(ids, v)
        }
    }
}
