package ir.gahnama

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
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

private val Light = lightColorScheme(
    primary = Color(0xFF285642), secondary = Color(0xFF47745B), background = Color(0xFFF7F8F3),
    surface = Color.White, onPrimary = Color.White, onBackground = Color(0xFF202B25), onSurface = Color(0xFF202B25),
    surfaceVariant = Color(0xFFEAEFE8), onSurfaceVariant = Color(0xFF4F6257), outline = Color(0xFFA7B9A8), error = Color(0xFFB5483A),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF7FB69A), secondary = Color(0xFF7FB69A), background = Color(0xFF111A15),
    surface = Color(0xFF18231D), onPrimary = Color(0xFF0E1D15), onBackground = Color(0xFFE6EDE8), onSurface = Color(0xFFE6EDE8),
    surfaceVariant = Color(0xFF223028), onSurfaceVariant = Color(0xFFAFC2B6), outline = Color(0xFF4A6155), error = Color(0xFFE08A7C),
)

/** وزیرمتن؛ فایل‌ها با scripts/fonts.sh در res/font قرار می‌گیرند. */
val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

// متن‌ها عمداً درشت‌تر از پیش‌فرض‌اند (خوانایی برای همهٔ سنین)
private val B = Typography()
private val Type = Typography(
    displayLarge = B.displayLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold, fontSize = 104.sp, lineHeight = 124.sp),
    displayMedium = B.displayMedium.copy(fontFamily = Vazirmatn),
    displaySmall = B.displaySmall.copy(fontFamily = Vazirmatn),
    headlineLarge = B.headlineLarge.copy(fontFamily = Vazirmatn),
    headlineMedium = B.headlineMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 36.sp),
    headlineSmall = B.headlineSmall.copy(fontFamily = Vazirmatn),
    titleLarge = B.titleLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 34.sp),
    titleMedium = B.titleMedium.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 30.sp),
    titleSmall = B.titleSmall.copy(fontFamily = Vazirmatn),
    bodyLarge = B.bodyLarge.copy(fontFamily = Vazirmatn, fontSize = 19.sp, lineHeight = 30.sp),
    bodyMedium = B.bodyMedium.copy(fontFamily = Vazirmatn, fontSize = 17.sp, lineHeight = 26.sp),
    bodySmall = B.bodySmall.copy(fontFamily = Vazirmatn),
    labelLarge = B.labelLarge.copy(fontFamily = Vazirmatn, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
    labelMedium = B.labelMedium.copy(fontFamily = Vazirmatn, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = B.labelSmall.copy(fontFamily = Vazirmatn),
)

@Composable
fun GahnamaTheme(
    dark: Boolean = when (Prefs.theme) { 1 -> false; 2 -> true; else -> isSystemInDarkTheme() },
    content: @Composable () -> Unit,
) {
    val colors = if (dark) Dark else Light
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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colors, typography = Type,
            shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp)),
            content = content,
        )
    }
}
