package ir.gahnama

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import java.time.LocalDate
import java.time.ZoneId

/** نمایش عدد روز در نوار وضعیت (اعلان دائمی) و به‌روزرسانی خودکار نیمه‌شب. */
object Notifier {
    private const val CH = "day"
    private const val ID = 1

    fun refresh(c: Context) {
        Prefs.init(c); Events.load(c)
        val nm = c.getSystemService(NotificationManager::class.java)
        schedule(c)
        if (!Prefs.notif) { nm.cancel(ID); return }
        if (Build.VERSION.SDK_INT >= 26)
            nm.createNotificationChannel(NotificationChannel(CH, "تاریخ روز", NotificationManager.IMPORTANCE_LOW).apply { setShowBadge(false) })
        val today = LocalDate.now()
        val i = Dates.info(today, Prefs.offset)
        val day = i.fa.d.fa()
        val sub = listOfNotNull(i.greg.label(), if (Prefs.hijri) i.hijri.hijriLabel() else null).joinToString(" · ")
        val ev = Events.forDay(today).joinToString("، ") { it.n }
        val open = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val n = NotificationCompat.Builder(c, CH)
            .setSmallIcon(IconCompat.createWithBitmap(tile(c, day, 96, false)))
            .setLargeIcon(tile(c, day, 192, true))
            .setContentTitle("${i.weekday} $day ${Dates.faMonths[i.fa.m - 1]} ${i.fa.y.fa()}")
            .setContentText(ev.ifEmpty { sub })
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (ev.isEmpty()) sub else "$sub\n$ev"))
            .setContentIntent(open).setOngoing(true).setOnlyAlertOnce(true).setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        nm.notify(ID, n)
    }

    /** آلارم نیمه‌شب با RTC_WAKEUP (گوشی را از خواب بیدار می‌کند)؛ هر بار اجرا، آلارم بعدی را می‌گذارد. */
    private fun schedule(c: Context) {
        val am = c.getSystemService(AlarmManager::class.java)
        val pi = PendingIntent.getBroadcast(c, 1, Intent(c, DayReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        if (!Prefs.notif) { am.cancel(pi); return }
        val t = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() + 5_000
        if (Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
        else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
    }

    /** مربع گوشه‌گرد با عدد روز؛ در نوار وضعیت فقط خط‌دور، در اعلان توپر. */
    private fun tile(c: Context, text: String, size: Int, filled: Boolean): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val cv = Canvas(bmp)
        val s = size.toFloat()
        val r = s * 0.22f
        val box = Paint(Paint.ANTI_ALIAS_FLAG)
        if (filled) {
            box.color = 0xFF285642.toInt()
            cv.drawRoundRect(0f, 0f, s, s, r, r, box)
        } else {
            box.color = Color.WHITE; box.style = Paint.Style.STROKE; box.strokeWidth = s * 0.08f
            val p = s * 0.06f
            cv.drawRoundRect(p, p, s - p, s - p, r, r, box)
        }
        val t = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(c, R.font.vazirmatn_bold)
            textSize = s * if (text.length > 1) 0.48f else 0.62f
        }
        cv.drawText(text, s / 2, s / 2 - (t.descent() + t.ascent()) / 2, t)
        return bmp
    }
}

class DayReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) = Notifier.refresh(c)
}
