package ir.gahnama

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** c: s شمسی، l قمری، g میلادی · t: n ملی، r مذهبی، w جهانی، p شخصی · h: تعطیل · y: فقط همان سال (۰ = هر سال) */
data class Ev(val id: String, val c: Char, val m: Int, val d: Int, val n: String, val t: Char, val h: Boolean, val y: Int = 0) {
    val typeName get() = typeLabel(t)
}

fun typeLabel(t: Char) = when (t) { 'n' -> "ملی"; 'r' -> "مذهبی"; 'w' -> "جهانی"; else -> "شخصی" }

/**
 * همهٔ مناسبت‌ها قابل ویرایش‌اند. بار اول از assets/events.json کپی می‌شوند و بعد در حافظهٔ برنامه ذخیره می‌شوند.
 * با بالا بردن VER هنگام تغییر events.json، مناسبت‌های تازه اضافه می‌شوند و ویرایش‌های کاربر حفظ می‌ماند.
 */
object Events {
    private const val VER = 2
    var all by mutableStateOf(emptyList<Ev>()); private set
    private var sp: SharedPreferences? = null
    private var deleted = setOf<String>()

    fun load(c: Context) {
        if (sp != null) return
        val p = c.applicationContext.getSharedPreferences("events", Context.MODE_PRIVATE)
        sp = p
        val defaults = parse(c.assets.open("events.json").bufferedReader().use { it.readText() })
        val saved = p.getString("list", null)?.let { parse(it) }
        deleted = p.getStringSet("deleted", emptySet()) ?: emptySet()
        all = when {
            saved == null -> defaults
            p.getInt("ver", 0) < VER -> { val ids = saved.map { it.id }.toSet(); saved + defaults.filter { it.id !in ids && it.id !in deleted } }
            else -> saved
        }
        persist()
    }

    fun save(e: Ev) { all = if (all.any { it.id == e.id }) all.map { if (it.id == e.id) e else it } else all + e; persist() }
    fun delete(id: String) { all = all.filter { it.id != id }; deleted = deleted + id; persist() }
    fun reset(c: Context) {
        all = parse(c.assets.open("events.json").bufferedReader().use { it.readText() }); deleted = emptySet(); persist()
    }

    private fun parse(text: String): List<Ev> {
        val a = JSONArray(text)
        return List(a.length()) {
            val o = a.getJSONObject(it)
            val c = o.getString("c")[0]; val m = o.getInt("m"); val d = o.getInt("d"); val n = o.getString("n")
            Ev(o.optString("i", "").ifEmpty { "$c$m/$d/$n" }, c, m, d, n, o.getString("t")[0], o.optInt("h") == 1, o.optInt("y"))
        }
    }

    private fun persist() {
        val a = JSONArray()
        all.forEach { a.put(JSONObject().put("i", it.id).put("c", it.c.toString()).put("m", it.m).put("d", it.d).put("n", it.n).put("t", it.t.toString()).put("h", if (it.h) 1 else 0).put("y", it.y)) }
        sp?.edit()?.putString("list", a.toString())?.putInt("ver", VER)?.putStringSet("deleted", deleted)?.apply()
    }

    fun forDay(date: LocalDate): List<Ev> {
        val i = Dates.info(date, Prefs.offset)
        val next by lazy { Dates.info(date.plusDays(1), Prefs.offset) }
        return all.filter { e ->
            (e.t != 'r' || Prefs.religious) && (e.t != 'w' || Prefs.world) && when (e.c) {
                's' -> i.fa.m == e.m && i.fa.d == e.d && (e.y == 0 || e.y == i.fa.y)
                'g' -> date.monthValue == e.m && date.dayOfMonth == e.d && (e.y == 0 || e.y == date.year)
                // روز ۳۰ قمری: اگر ماه ۲۹ روزه باشد، روز ۲۹ (آخر ماه) در نظر گرفته می‌شود
                else -> i.hijri.m == e.m && (i.hijri.d == e.d || (e.d == 30 && i.hijri.d == 29 && next.hijri.m != e.m)) && (e.y == 0 || e.y == i.hijri.y)
            }
        }.sortedByDescending { it.h }
    }
}
