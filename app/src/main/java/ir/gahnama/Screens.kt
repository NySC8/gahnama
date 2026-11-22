package ir.gahnama

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun EventsScreen() {
    val cs = MaterialTheme.colorScheme
    val now = remember { Dates.info(LocalDate.now()).fa }
    var y by remember { mutableIntStateOf(now.y) }
    var m by remember { mutableIntStateOf(now.m) }
    fun step(k: Int) { val n = m - 1 + k; y += Math.floorDiv(n, 12); m = Math.floorMod(n, 12) + 1 }
    val rows = remember(y, m, Prefs.offset, Prefs.world, Prefs.religious) {
        (1..Dates.monthLength(y, m)).mapNotNull { d ->
            val dt = Dates.fromPersian(y, m, d)
            Events.forDay(dt).takeIf { it.isNotEmpty() }?.let { Triple(d, Dates.info(dt, Prefs.offset).weekday, it) }
        }
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { MonthBar("${Dates.faMonths[m - 1]} ${y.fa()}", { step(-1) }, { step(1) }) }
        if (rows.isEmpty()) item { Text("مناسبتی برای این ماه ثبت نشده است.", color = cs.onSurfaceVariant) }
        items(rows) { (d, weekday, evs) ->
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(cs.primary), Alignment.Center) {
                        Text(d.fa(), color = cs.onPrimary, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(weekday, color = cs.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                        evs.forEach { EventLine(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        Prefs.update { notif = ok }; Notifier.refresh(ctx)
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.headlineMedium)
        Section("ظاهر") {
            Chips(listOf("سیستم", "روشن", "تاریک"), Prefs.theme) { k -> Prefs.update { theme = k } }
        }
        Section("مناسبت‌ها و تاریخ") {
            Toggle("مناسبت‌های مذهبی", Prefs.religious) { v -> Prefs.update { religious = v } }
            Toggle("مناسبت‌های جهانی", Prefs.world) { v -> Prefs.update { world = v } }
            Toggle("نمایش تاریخ قمری", Prefs.hijri) { v -> Prefs.update { hijri = v }; Notifier.refresh(ctx) }
            Text("اصلاح روز قمری", Modifier.padding(top = 8.dp, bottom = 6.dp), style = MaterialTheme.typography.titleMedium)
            Chips(listOf(-2, -1, 0, 1, 2).map { (if (it > 0) "+" else if (it < 0) "−" else "") + abs(it).fa() }, Prefs.offset + 2) { k ->
                Prefs.update { offset = k - 2 }; Notifier.refresh(ctx)
            }
            Text("اگر تاریخ قمری یا مناسبت‌های مذهبی با تقویم رسمی یک روز اختلاف دارد، اینجا اصلاح کنید.",
                Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Section("نوار وضعیت") {
            Toggle("نمایش عدد روز در نوار بالای گوشی", Prefs.notif) { on ->
                if (on && Build.VERSION.SDK_INT >= 33 && ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                    ask.launch(Manifest.permission.POST_NOTIFICATIONS)
                else { Prefs.update { notif = on }; Notifier.refresh(ctx) }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
    Panel(content = content)
}

@Composable
private fun Toggle(title: String, checked: Boolean, onChange: (Boolean) -> Unit) =
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked, onChange)
    }

@Composable
private fun Chips(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) =
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { k, l -> FilterChip(selected == k, { onSelect(k) }, { Text(l) }, Modifier.height(44.dp)) }
    }

@Composable
fun AboutScreen() = Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
    Text("گاه‌نما", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
    Text("GAHNAMA · نسخهٔ ۰٫۲٫۰", color = MaterialTheme.colorScheme.onSurfaceVariant)
}
