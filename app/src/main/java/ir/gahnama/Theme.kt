package ir.gahnama

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/** یک رنگ‌بندی با دو نسخهٔ روشن و تاریک */
class Pal(val name: String, val light: ColorScheme, val dark: ColorScheme)

private fun pal(
    name: String, lp: Long, ls: Long, lbg: Long, lv: Long, lov: Long, lo: Long,
    dp: Long, dbg: Long, dsf: Long, dv: Long, dov: Long,
) = Pal(
    name,
    lightColorScheme(
        primary = Color(lp), secondary = Color(ls), background = Color(lbg), surface = Color.White, onPrimary = Color.White,
        onBackground = Color(0xFF1F2A27), onSurface = Color(0xFF1F2A27), surfaceVariant = Color(lv), onSurfaceVariant = Color(lov),
        outline = Color(lo), error = Color(0xFFB5483A),
    ),
    darkColorScheme(
        primary = Color(dp), secondary = Color(dp), background = Color(dbg), surface = Color(dsf), onPrimary = Color(0xFF0E1511),
        onBackground = Color(0xFFE8EEEA), onSurface = Color(0xFFE8EEEA), surfaceVariant = Color(dv), onSurfaceVariant = Color(dov),
        outline = Color(dov).copy(alpha = 0.4f), error = Color(0xFFE08A7C),
    ),
)

val Palettes = listOf(
    pal("جنگل", 0xFF285642, 0xFF47745B, 0xFFF2F6F1, 0xFFE3ECE5, 0xFF4F6257, 0xFFA7B9A8, 0xFF7FB69A, 0xFF0F1813, 0xFF18231D, 0xFF223028, 0xFFAFC2B6),
    pal("اقیانوس", 0xFF1F4E79, 0xFF3A7CA5, 0xFFEFF4F9, 0xFFDCE8F2, 0xFF4A6277, 0xFF9FB6C9, 0xFF7DB4E0, 0xFF0C1520, 0xFF142130, 0xFF1D2E40, 0xFFA9BFD3),
    pal("ارغوانی", 0xFF4B3A7A, 0xFF7358B0, 0xFFF5F2FA, 0xFFE8E1F3, 0xFF5E5275, 0xFFB9AED0, 0xFFB7A3E8, 0xFF15111F, 0xFF1E1830, 0xFF2A2240, 0xFFC3B9D8),
    pal("یاقوتی", 0xFF8A2D3E, 0xFFB04A5A, 0xFFFAF2F3, 0xFFF2E2E4, 0xFF6E4F54, 0xFFD2B3B8, 0xFFE89AA6, 0xFF1C1114, 0xFF281A1E, 0xFF36242A, 0xFFD6B8BE),
    pal("کهربایی", 0xFF8A5A12, 0xFFB7791F, 0xFFFAF6EC, 0xFFF1E8D2, 0xFF6B5A3A, 0xFFD2C29E, 0xFFE6B65C, 0xFF191409, 0xFF241D10, 0xFF332A18, 0xFFD5C7A5),
    pal("گرافیت", 0xFF2F3B45, 0xFF5C6B77, 0xFFF3F4F5, 0xFFE3E6E8, 0xFF515C66, 0xFFB3BBC1, 0xFFAAB8C4, 0xFF111416, 0xFF1A1F22, 0xFF252B30, 0xFFB7C0C8),
)

val LocalDark = compositionLocalOf { false }

/** سطح شیشه‌ای: سفید نیمه‌شفاف با لبهٔ ظریف */
@Composable fun glassFill() = if (LocalDark.current) Color.White.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.62f)
@Composable fun glassStroke() = if (LocalDark.current) Color.White.copy(alpha = 0.14f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

/** پس‌زمینهٔ نرم با دو لکهٔ محو به رنگ تم؛ پشت کارت‌های شیشه‌ای دیده می‌شود */
@Composable
fun Modifier.glassBackground(): Modifier {
    val cs = MaterialTheme.colorScheme
    val dark = LocalDark.current
    return background(cs.background).drawBehind {
        drawRect(Brush.radialGradient(listOf(cs.primary.copy(alpha = if (dark) 0.22f else 0.20f), Color.Transparent), Offset(size.width * 0.9f, size.height * 0.06f), size.width))
        drawRect(Brush.radialGradient(listOf(cs.secondary.copy(alpha = if (dark) 0.18f else 0.16f), Color.Transparent), Offset(size.width * 0.05f, size.height * 0.8f), size.width * 1.1f))
    }
}

/** وزیرمتن؛ فایل‌ها با scripts/fonts.sh در res/font قرار می‌گیرند. */
val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

// متن‌ها عمداً درشت‌تر از پیش‌فرض‌اند (خوانایی برای همهٔ سنین)
private fun typo(f: FontFamily): Typography {
    val b = Typography()
    return Typography(
        displayLarge = b.displayLarge.copy(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 104.sp, lineHeight = 124.sp),
        displayMedium = b.displayMedium.copy(fontFamily = f), displaySmall = b.displaySmall.copy(fontFamily = f),
        headlineLarge = b.headlineLarge.copy(fontFamily = f),
        headlineMedium = b.headlineMedium.copy(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 36.sp),
        headlineSmall = b.headlineSmall.copy(fontFamily = f),
        titleLarge = b.titleLarge.copy(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 34.sp),
        titleMedium = b.titleMedium.copy(fontFamily = f, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 30.sp),
        titleSmall = b.titleSmall.copy(fontFamily = f),
        bodyLarge = b.bodyLarge.copy(fontFamily = f, fontSize = 19.sp, lineHeight = 30.sp),
        bodyMedium = b.bodyMedium.copy(fontFamily = f, fontSize = 17.sp, lineHeight = 26.sp),
        bodySmall = b.bodySmall.copy(fontFamily = f),
        labelLarge = b.labelLarge.copy(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
        labelMedium = b.labelMedium.copy(fontFamily = f, fontSize = 14.sp, lineHeight = 20.sp),
        labelSmall = b.labelSmall.copy(fontFamily = f),
    )
}

private val Type = typo(Vazirmatn)

@Composable
fun GahnamaTheme(
    dark: Boolean = when (Prefs.theme) { 1 -> false; 2 -> true; else -> isSystemInDarkTheme() },
    content: @Composable () -> Unit,
) {
    val pal = Palettes[Prefs.palette.coerceIn(0, Palettes.lastIndex)]
    val colors = if (dark) pal.dark else pal.light
    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        val w = (view.context as Activity).window
        w.statusBarColor = colors.background.toArgb()
        w.navigationBarColor = colors.surface.toArgb()
        WindowCompat.getInsetsController(w, view).run {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalDark provides dark) {
        MaterialTheme(
            colorScheme = colors, typography = Type,
            shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(22.dp), large = RoundedCornerShape(28.dp)),
            content = content,
        )
    }
}
