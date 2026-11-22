package ir.gahnama

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** تنظیمات ماندگار با stateهای Compose. theme: ۰ سیستم، ۱ روشن، ۲ تاریک */
object Prefs {
    private var sp: SharedPreferences? = null
    var theme by mutableIntStateOf(0)
    var religious by mutableStateOf(true)
    var world by mutableStateOf(true)
    var hijri by mutableStateOf(true)
    var offset by mutableIntStateOf(0)
    var notif by mutableStateOf(false)

    fun init(c: Context) {
        if (sp != null) return
        sp = c.applicationContext.getSharedPreferences("gahnama", Context.MODE_PRIVATE).also {
            theme = it.getInt("theme", 0); religious = it.getBoolean("religious", true)
            world = it.getBoolean("world", true); hijri = it.getBoolean("hijri", true)
            offset = it.getInt("offset", 0); notif = it.getBoolean("notif", false)
        }
    }

    fun update(block: Prefs.() -> Unit) {
        block()
        sp?.edit()?.putInt("theme", theme)?.putBoolean("religious", religious)?.putBoolean("world", world)
            ?.putBoolean("hijri", hijri)?.putInt("offset", offset)?.putBoolean("notif", notif)?.apply()
    }
}
