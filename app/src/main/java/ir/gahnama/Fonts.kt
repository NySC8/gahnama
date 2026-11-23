package ir.gahnama

import android.content.Context
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * فونت دلخواه: پوشه‌ای مثل app/src/main/assets/fonts/Damoon/ بسازید و داخلش
 * regular.ttf (و در صورت وجود medium / semibold / bold با پسوند ttf یا otf) بگذارید.
 * نام پوشه در تنظیمات به‌عنوان گزینهٔ فونت ظاهر می‌شود.
 */
object Fonts {
    fun names(c: Context): List<String> = (c.assets.list("fonts") ?: emptyArray()).sorted()

    fun family(c: Context, name: String): FontFamily {
        if (name.isEmpty() || name !in names(c)) return Vazirmatn
        val weights = listOf("regular" to FontWeight.Normal, "medium" to FontWeight.Medium, "semibold" to FontWeight.SemiBold, "bold" to FontWeight.Bold)
        val fonts = weights.flatMap { (file, w) ->
            listOf("ttf", "otf").map { "fonts/$name/$file.$it" }.filter { exists(c, it) }.map { Font(it, c.assets, w) }
        }
        return if (fonts.isEmpty()) Vazirmatn else FontFamily(fonts)
    }

    private fun exists(c: Context, path: String) = try { c.assets.open(path).close(); true } catch (e: Exception) { false }
}
