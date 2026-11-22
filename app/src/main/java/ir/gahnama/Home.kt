package ir.gahnama

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class HomeViewModel : ViewModel() {
    val date = MutableStateFlow(LocalDate.now())
    fun shift(days: Long) = date.update { it.plusDays(days) }
    fun today() { date.value = LocalDate.now() }
}

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val date by vm.date.collectAsState()
    val cs = MaterialTheme.colorScheme
    val i = Dates.info(date, Prefs.offset)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("گاه‌نما", Modifier.weight(1f), color = cs.primary, style = MaterialTheme.typography.titleLarge)
            OutlinedButton({ vm.today() }) { Text("امروز") }
        }
        Card(Modifier.fillMaxWidth(), MaterialTheme.shapes.large, CardDefaults.cardColors(cs.primary)) {
            AnimatedContent(date, Modifier.fillMaxWidth(), transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) }, label = "day") { HeroDay(it) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard("میلادی", i.greg.label(), Modifier.weight(1f))
            if (Prefs.hijri) InfoCard("قمری", i.hijri.hijriLabel(), Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton({ vm.shift(-1) }, Modifier.weight(1f).height(56.dp)) { Text("روز قبل") }
            FilledTonalButton({ vm.shift(1) }, Modifier.weight(1f).height(56.dp)) { Text("روز بعد") }
        }
        EventsPanel("مناسبت‌های این روز", Events.forDay(date))
    }
}

@Composable
private fun HeroDay(d: LocalDate) {
    val cs = MaterialTheme.colorScheme
    val x = Dates.info(d, Prefs.offset)
    val official = Events.forDay(d).any { it.h }
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(x.weekday, style = MaterialTheme.typography.headlineMedium, color = cs.onPrimary)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.clip(RoundedCornerShape(36.dp)).background(cs.onPrimary.copy(alpha = 0.14f)).padding(horizontal = 40.dp, vertical = 4.dp)) {
            Text(x.fa.d.fa(), style = MaterialTheme.typography.displayLarge, color = cs.onPrimary)
        }
        Spacer(Modifier.height(8.dp))
        Text("${Dates.faMonths[x.fa.m - 1]} ${x.fa.y.fa()}", style = MaterialTheme.typography.headlineMedium, color = cs.onPrimary)
        if (official || x.friday) {
            Spacer(Modifier.height(12.dp))
            Box(Modifier.clip(RoundedCornerShape(50)).background(cs.onPrimary).padding(horizontal = 18.dp, vertical = 4.dp)) {
                Text(if (official) "تعطیل رسمی" else "جمعه", color = cs.primary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(modifier.fillMaxWidth(), MaterialTheme.shapes.medium, CardDefaults.cardColors(cs.surface), border = BorderStroke(1.dp, cs.outline.copy(alpha = 0.5f))) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier) = Panel(modifier) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun Tag(text: String, hot: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    Box(Modifier.padding(start = 8.dp).clip(RoundedCornerShape(10.dp)).background(if (hot) cs.error.copy(alpha = 0.12f) else cs.surfaceVariant).padding(horizontal = 10.dp, vertical = 2.dp)) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = if (hot) cs.error else cs.onSurfaceVariant)
    }
}

@Composable
fun EventLine(e: Ev) = Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
    Text(e.n, Modifier.weight(1f), color = if (e.h) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
    Tag(if (e.h) "تعطیل" else e.typeName, e.h)
}

@Composable
fun EventsPanel(title: String, evs: List<Ev>) = Panel {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))
    if (evs.isEmpty()) Text("مناسبتی برای این روز ثبت نشده است.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    evs.forEach { EventLine(it) }
}

@Composable
fun MonthBar(title: String, onPrev: () -> Unit, onNext: () -> Unit) = Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    FilledTonalButton(onPrev) { Text("قبل") }
    Text(title, Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium)
    FilledTonalButton(onNext) { Text("بعد") }
}
