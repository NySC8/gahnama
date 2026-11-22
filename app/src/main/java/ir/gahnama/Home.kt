package ir.gahnama

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton({ vm.shift(-1) }) { Text("روز قبل") }
            TextButton(vm::today) { Text("امروز") }
            TextButton({ vm.shift(1) }) { Text("روز بعد") }
        }
        Spacer(Modifier.height(24.dp))
        AnimatedContent(date, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) }, label = "day") { d ->
            val i = Dates.info(d)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(i.weekday, style = MaterialTheme.typography.headlineMedium, color = if (i.friday) cs.error else cs.secondary)
                Text(i.fa.d.fa(), style = MaterialTheme.typography.displayLarge, color = cs.primary)
                Text("${Dates.faMonths[i.fa.m - 1]} ${i.fa.y.fa()}", style = MaterialTheme.typography.headlineMedium)
            }
        }
        Spacer(Modifier.height(28.dp))
        val info = Dates.info(date)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard("میلادی", info.greg.label(), Modifier.weight(1f))
            InfoCard("قمری", info.hijri.hijriLabel(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), MaterialTheme.shapes.large, CardDefaults.cardColors(cs.surface)) {
            Column(Modifier.padding(20.dp)) {
                Text("مناسبت‌های این روز", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                // مرحلهٔ ۲: اتصال به events.json
                Text("داده‌های مناسبت‌ها در مرحلهٔ بعد اضافه می‌شود.", color = cs.outline)
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier) =
    Card(modifier, MaterialTheme.shapes.medium, CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Text(value, textAlign = TextAlign.Center)
        }
    }
