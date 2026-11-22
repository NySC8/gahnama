package ir.gahnama

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Light = lightColorScheme(
    primary = Color(0xFF285642), secondary = Color(0xFF47745B), background = Color(0xFFF7F8F3),
    surface = Color.White, onPrimary = Color.White, onBackground = Color(0xFF202B25), onSurface = Color(0xFF202B25),
    surfaceVariant = Color(0xFFEAEFE8), outline = Color(0xFFA7B9A8), error = Color(0xFFB5483A),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF7FB69A), secondary = Color(0xFF47745B), background = Color(0xFF111A15),
    surface = Color(0xFF18231D), onPrimary = Color(0xFF0E1D15), onBackground = Color(0xFFE6EDE8),
    onSurface = Color(0xFFE6EDE8), surfaceVariant = Color(0xFF223028), outline = Color(0xFF5E7568), error = Color(0xFFE08A7C),
)
// برای فونت وزیرمتن: فایل‌ها را در res/font بگذارید و fontFamily را به همهٔ استایل‌ها بدهید.
private val Type = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 96.sp, lineHeight = 110.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp),
)

@Composable
fun GahnamaTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = if (dark) Dark else Light, typography = Type,
            shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp)),
            content = content,
        )
    }
