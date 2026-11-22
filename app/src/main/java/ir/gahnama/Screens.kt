package ir.gahnama

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun Centered(content: @Composable ColumnScope.() -> Unit) =
    Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally, content = content)

@Composable
fun EventsScreen() = Centered {
    Text("مناسبت‌ها", style = MaterialTheme.typography.headlineMedium)
    Text("پس از افزودن داده‌های معتبر (مرحلهٔ ۲) نمایش داده می‌شود.", color = MaterialTheme.colorScheme.outline)
}

@Composable
fun AboutScreen() = Centered {
    Text("گاه‌نما", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
    Text("GAHNAMA ۰٫۱٫۰", color = MaterialTheme.colorScheme.outline)
}

@Composable
fun SettingsScreen() {
    // مرحلهٔ ۳: ذخیره با DataStore
    val items = listOf("اعلان روزانه", "مناسبت‌های جهانی", "مناسبت‌های مذهبی", "نمایش تاریخ قمری", "تم تاریک")
    val checked = remember { mutableStateListOf(true, true, true, true, false) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        items.forEachIndexed { i, t ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(t, Modifier.weight(1f))
                Switch(checked[i], { checked[i] = it })
            }
        }
    }
}
