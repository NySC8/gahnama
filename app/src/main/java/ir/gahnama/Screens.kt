package ir.gahnama

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import kotlin.math.abs

private data class DayRow(val d: Int, val weekday: String, val date: LocalDate, val evs: List<Ev>)

@Composable
fun EventsScreen() {
    val cs = MaterialTheme.colorScheme
    val now = remember { Dates.info(LocalDate.now()).fa }
    var y by remember { mutableIntStateOf(now.y) }
    var m by remember { mutableIntStateOf(now.m) }
    var editing by remember { mutableStateOf<Pair<LocalDate, Ev>?>(null) }
    fun step(k: Int) { val n = m - 1 + k; y += Math.floorDiv(n, 12); m = Math.floorMod(n, 12) + 1 }
    val rows = remember(y, m, Prefs.offset, Prefs.world, Prefs.religious, Events.all) {
        (1..Dates.monthLength(y, m)).mapNotNull { d ->
            val dt = Dates.fromPersian(y, m, d)
            Events.forDay(dt).takeIf { it.isNotEmpty() }?.let { DayRow(d, Dates.info(dt, Prefs.offset).weekday, dt, it) }
        }
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton({ step(-1) }) { Text("قبل") }
                Text("${Dates.faMonths[m - 1]} ${y.fa()}", Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
                FilledTonalButton({ step(1) }) { Text("بعد") }
            }
        }
        if (rows.isEmpty()) item { Text("مناسبتی برای این ماه ثبت نشده است.", color = cs.onSurfaceVariant) }
        items(rows) { r ->
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DayTile(r.d.fa())
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(r.weekday, color = cs.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                        r.evs.forEach { e -> EventLine(e) { editing = r.date to e } }
                    }
                }
            }
        }
    }
    editing?.let { (dt, e) -> EventDialog(e, dt) { editing = null } }
}

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        Prefs.update { notif = ok }; Notifier.refresh(ctx)
    }
    val fonts = remember { Fonts.names(ctx) }
    var sure by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("تنظیمات", style = MaterialTheme.typography.headlineMedium)
        Section("ظاهر") {
            Chips(listOf("سیستم", "روشن", "تاریک"), Prefs.theme) { k -> Prefs.update { theme = k } }
            Text("فونت", Modifier.padding(top = 12.dp, bottom = 6.dp), style = MaterialTheme.typography.titleMedium)
            Chips(listOf("وزیرمتن") + fonts, if (Prefs.font in fonts) fonts.indexOf(Prefs.font) + 1 else 0) { k -> Prefs.update { font = if (k == 0) "" else fonts[k - 1] } }
            Text("فونت دلخواه: پوشه‌ای با نام فونت در app/src/main/assets/fonts/ بسازید و فایل regular.ttf (و در صورت وجود bold.ttf) را داخلش بگذارید.",
                Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Section("مناسبت‌ها و تاریخ") {
            Toggle("مناسبت‌های مذهبی", Prefs.religious) { v -> Prefs.update { religious = v } }
            Toggle("مناسبت‌های جهانی", Prefs.world) { v -> Prefs.update { world = v } }
            Toggle("نمایش تاریخ قمری", Prefs.hijri) { v -> Prefs.update { hijri = v }; Notifier.refresh(ctx) }
            Text("اصلاح روز قمری", Modifier.padding(top = 8.dp, bottom = 6.dp), style = MaterialTheme.typography.titleMedium)
            Chips(listOf(-2, -1, 0, 1, 2).map { (if (it > 0) "+" else if (it < 0) "−" else "") + abs(it).fa() }, Prefs.offset + 2) { k ->
                Prefs.update { offset = k - 2 }; Notifier.refresh(ctx)
            }
            Text("اگر تاریخ قمری یا مناسبت‌های مذهبی با تقویم رسمی یک روز اختلاف دارد، اینجا اصلاح کنید. مناسبت‌ها را هم با زدن روی هر کدام در صفحهٔ تقویم می‌توانید ویرایش یا حذف کنید.",
                Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            TextButton({ if (sure) { Events.reset(ctx); sure = false } else sure = true }) {
                Text(if (sure) "برای تأیید دوباره بزنید (ویرایش‌ها پاک می‌شود)" else "بازنشانی مناسبت‌ها به پیش‌فرض", color = MaterialTheme.colorScheme.error)
            }
        }
        Section("نوار وضعیت") {
            Toggle("نمایش عدد روز در نوار بالای گوشی", Prefs.notif) { on ->
                if (on && Build.VERSION.SDK_INT >= 33 && ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                    ask.launch(Manifest.permission.POST_NOTIFICATIONS)
                else { Prefs.update { notif = on }; Notifier.refresh(ctx) }
            }
            Text("برای تغییر خودکار عدد در نیمه‌شب، در تنظیمات برنامه «شروع خودکار» را روشن و باتری را «بدون محدودیت» کنید (گوشی‌های شیائومی).",
                color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            TextButton({ ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${ctx.packageName}"))) }) { Text("باز کردن تنظیمات برنامه") }
        }
        Section("بروزرسانی") {
            Text("نسخهٔ فعلی: ${Updater.versionName(ctx)}")
            val s = Updater.state
            Text(when (s) { is Updater.S.Latest -> "نسخهٔ شما به‌روز است"; is Updater.S.New -> "نسخهٔ ${s.tag} آماده است (در صفحهٔ تقویم)"; is Updater.S.Err -> s.msg; else -> "" },
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            FilledTonalButton({ Updater.check(ctx, true) }, Modifier.padding(top = 8.dp).fillMaxWidth().height(52.dp)) { Text("بررسی بروزرسانی") }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
    Panel(content = content)
}

@Composable
fun AboutScreen() {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("گاه‌نما", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("GAHNAMA · نسخهٔ ${Updater.versionName(ctx)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
