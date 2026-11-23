package ir.gahnama

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** آیکون‌های خطی مینیمال اختصاصی گاه‌نما (۲۴×۲۴، قلم گرد). رنگ از tint خود Icon می‌آید. */
object Ic {
    private fun make(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            paths.forEach {
                addPath(
                    PathParser().parsePathString(it).toNodes(), stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()

    val Calendar = make("calendar", "M7 5h10a3 3 0 0 1 3 3v9a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3V8a3 3 0 0 1 3-3z", "M4 10h16", "M9 3v4", "M15 3v4", "M8 14h3v3H8z")
    val Flag = make("flag", "M6 21V4", "M6 5h11l-2.5 4L17 13H6")
    val Sliders = make("sliders", "M4 7h9", "M17 7h3", "M4 17h3", "M11 17h9", "M13 7a2 2 0 1 0 4 0a2 2 0 1 0-4 0", "M7 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0")
    val Info = make("info", "M3 12a9 9 0 1 0 18 0a9 9 0 1 0-18 0", "M12 11v5", "M12 8h.01")
    val Add = make("add", "M12 5v14", "M5 12h14")
    val Right = make("right", "M9 6l6 6-6 6")
    val Left = make("left", "M15 6l-6 6 6 6")
    val Download = make("download", "M12 4v11", "M7 11l5 5 5-5", "M5 20h14")
    val Check = make("check", "M5 12.5l4.5 4.5L19 7.5")
}
