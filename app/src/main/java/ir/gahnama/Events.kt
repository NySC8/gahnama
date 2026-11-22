package ir.gahnama

import android.content.Context
import org.json.JSONArray
import java.time.LocalDate

/** c: s شمسی، l قمری، g میلادی · t: n ملی، r مذهبی، w جهانی · h: تعطیل رسمی */
data class Ev(val c: Char, val m: Int, val d: Int, val n: String, val t: Char, val h: Boolean) {
    val typeName get() = when (t) { 'n' -> "ملی"; 'r' -> "مذهبی"; else -> "جهانی" }
}

/** مناسبت‌ها از assets/events.json خوانده می‌شوند؛ برای به‌روزرسانی فقط همان فایل را ویرایش کنید. */
object Events {
    private var all = emptyList<Ev>()

    fun load(c: Context) {
        if (all.isNotEmpty()) return
        val a = JSONArray(c.assets.open("events.json").bufferedReader().use { it.readText() })
        all = List(a.length()) {
            val o = a.getJSONObject(it)
            Ev(o.getString("c")[0], o.getInt("m"), o.getInt("d"), o.getString("n"), o.getString("t")[0], o.optInt("h") == 1)
        }
    }

    fun forDay(date: LocalDate): List<Ev> {
        val i = Dates.info(date, Prefs.offset)
        val next by lazy { Dates.info(date.plusDays(1), Prefs.offset) }
        return all.filter { e ->
            (e.t != 'r' || Prefs.religious) && (e.t != 'w' || Prefs.world) && when (e.c) {
                's' -> i.fa.m == e.m && i.fa.d == e.d
                'g' -> date.monthValue == e.m && date.dayOfMonth == e.d
                // روز ۳۰ قمری: اگر ماه ۲۹ روزه باشد، روز ۲۹ (آخر ماه) در نظر گرفته می‌شود
                else -> i.hijri.m == e.m && (i.hijri.d == e.d || (e.d == 30 && i.hijri.d == 29 && next.hijri.m != e.m))
            }
        }.sortedByDescending { it.h }
    }
}
