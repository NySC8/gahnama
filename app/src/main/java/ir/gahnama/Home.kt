package ir.gahnama

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** اجزای مشترک رابط کاربری (سطح شیشه‌ای، برچسب، ردیف مناسبت، چیپ و سوییچ) */
@Composable
fun Panel(modifier: Modifier = Modifier, pad: Dp = 18.dp, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), MaterialTheme.shapes.medium, CardDefaults.cardColors(glassFill()), border = BorderStroke(1.dp, glassStroke())) {
        Column(Modifier.padding(pad), content = content)
    }
}

@Composable
fun DayTile(text: String, size: Dp = 56.dp) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.size(size).clip(RoundedCornerShape(size / 3.5f)).background(cs.primary), Alignment.Center) {
        Text(text, color = cs.onPrimary, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun Tag(text: String, hot: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.padding(start = 8.dp).clip(RoundedCornerShape(10.dp)).background(if (hot) cs.error.copy(alpha = 0.12f) else cs.surfaceVariant).padding(horizontal = 10.dp, vertical = 2.dp)) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = if (hot) cs.error else cs.onSurfaceVariant)
    }
}

@Composable
fun EventLine(e: Ev, onClick: (() -> Unit)? = null) = Row(
    Modifier.fillMaxWidth().clickable(enabled = onClick != null) { onClick?.invoke() }.padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(e.n, Modifier.weight(1f), color = if (e.h) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
    Tag(if (e.h) "تعطیل" else e.typeName, e.h)
}

@Composable
fun Chips(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) =
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { k, l -> FilterChip(selected == k, { onSelect(k) }, { Text(l) }, Modifier.height(44.dp)) }
    }

@Composable
fun Toggle(title: String, checked: Boolean, onChange: (Boolean) -> Unit) =
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked, onChange)
    }

/** نوار بروزرسانی: وقتی نسخهٔ تازه‌تری در ریلیز گیت‌هاب باشد نمایش داده می‌شود. */
@Composable
fun UpdateBanner() {
    val ctx = LocalContext.current
    when (val s = Updater.state) {
        is Updater.S.New -> Panel {
            Text("نسخهٔ جدید ${s.tag} آماده است", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Button({ Updater.install(ctx, s.url) }, Modifier.fillMaxWidth().height(52.dp)) { Text("بروزرسانی") }
        }
        is Updater.S.Busy -> Panel {
            Text("در حال دانلود… ${s.pct.fa()}٪")
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { s.pct / 100f }, modifier = Modifier.fillMaxWidth())
        }
        is Updater.S.Err -> Panel { Text(s.msg, color = MaterialTheme.colorScheme.error) }
        else -> {}
    }
}
