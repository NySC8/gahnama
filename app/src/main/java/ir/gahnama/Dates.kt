package ir.gahnama

import android.icu.util.Calendar
import android.icu.util.IslamicCalendar
import android.icu.util.PersianCalendar
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class YMD(val y: Int, val m: Int, val d: Int)
data class DayInfo(val greg: LocalDate, val fa: YMD, val hijri: YMD, val weekday: String, val friday: Boolean)

/** تبدیل تاریخ با ICU خود اندروید (بدون الگوریتم دستی). */
object Dates {
    val faMonths = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
    val hijriMonths = listOf("محرم", "صفر", "ربیع‌الاول", "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی", "رجب", "شعبان", "رمضان", "شوال", "ذیقعده", "ذیحجه")
    val gregMonths = listOf("ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن", "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر")
    val weekdays = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه")

    private val zone get() = ZoneId.systemDefault()
    private fun ms(d: LocalDate) = d.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    /** hijriOffset: اصلاح اختلاف رؤیت هلال (۲- تا ۲+ روز) */
    fun info(date: LocalDate, hijriOffset: Int = 0): DayInfo {
        val t = ms(date)
        val p = PersianCalendar().apply { timeInMillis = t }
        val h = IslamicCalendar().apply {
            calculationType = IslamicCalendar.CalculationType.ISLAMIC_UMALQURA
            timeInMillis = t + hijriOffset * 86_400_000L
        }
        val i = (p.get(Calendar.DAY_OF_WEEK) - Calendar.SATURDAY + 7) % 7
        return DayInfo(
            date,
            YMD(p.get(Calendar.YEAR), p.get(Calendar.MONTH) + 1, p.get(Calendar.DAY_OF_MONTH)),
            YMD(h.get(Calendar.YEAR), h.get(Calendar.MONTH) + 1, h.get(Calendar.DAY_OF_MONTH)),
            weekdays[i], i == 6,
        )
    }

    fun fromPersian(y: Int, m: Int, d: Int): LocalDate {
        val c = PersianCalendar().apply { clear(); set(y, m - 1, d); set(Calendar.HOUR_OF_DAY, 12) }
        return Instant.ofEpochMilli(c.timeInMillis).atZone(zone).toLocalDate()
    }

    fun monthLength(y: Int, m: Int) = PersianCalendar().apply { clear(); set(y, m - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
    fun firstColumn(y: Int, m: Int) = weekdays.indexOf(info(fromPersian(y, m, 1)).weekday)
}

fun Int.fa() = toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")
fun LocalDate.label() = "${dayOfMonth.fa()} ${Dates.gregMonths[monthValue - 1]} ${year.fa()}"
fun YMD.hijriLabel() = "${d.fa()} ${Dates.hijriMonths[m - 1]} ${y.fa()}"
