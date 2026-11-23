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
import androidx.compose.ui.platform.LocalContext
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
    primary = Color(0xFF285642), secondary = Color(0xFF47745B), background = Color(0xFFF2F6F1),
    surface = Color.White, onPrimary = Color.White, onBackground = Color(0xFF202B25), onSurface = Color(0xFF202B25),
    surfaceVariant = Color(0xFFE3ECE5), onSurfaceVariant = Color(0xFF4F6257), outline = Color(0xFFA7B9A8), error = Color(0xFFB5483A),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF7FB69A), secondary = Color(0xFF7FB69A), background = Color(0xFF0F1813),
    surface = Color(0xFF18231D), onPrimary = Color(0xFF0E1D15), onBackground = Color(0xFFE6EDE8), onSurface = Color(0xFFE6EDE8),
    surfaceVariant = Color(0xFF223028), onSurfaceVariant = Color(0xFFAFC2B6), outline = Color(0xFF4A6155), error = Color(0xFFE08A7C),
)

val LocalDark = compositionLocalOf { false }

/** سطح شیشه‌ای: سفید نیمه‌شفاف با لبهٔ ظریف */
@Composable fun glassFill() = if (LocalDark.current) Color.White.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.62f)
@Composable fun glassStroke() = if (LocalDark.current) Color.White.copy(alpha = 0.14f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

/** پس‌زمینهٔ نرم با دو لکهٔ سبز محو؛ پشت کارت‌های شیشه‌ای دیده می‌شود */
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

@Composable
fun GahnamaTheme(
    dark: Boolean = when (Prefs.theme) { 1 -> false; 2 -> true; else -> isSystemInDarkTheme() },
    content: @Composable () -> Unit,
) {
    val colors = if (dark) Dark else Light
    val ctx = LocalContext.current
    val type = remember(Prefs.font) { typo(Fonts.family(ctx, Prefs.font)) }
    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        val w = (view.context as Activity).window
        w.statusBarColor = colors.background.toArgb()
        w.navigationBarColor = colors.background.toArgb()
        WindowCompat.getInsetsController(w, view).run {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalDark provides dark) {
        MaterialTheme(
            colorScheme = colors, typography = type,
            shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(22.dp), large = RoundedCornerShape(28.dp)),
            content = content,
        )
    }
}
