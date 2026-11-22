package ir.gahnama

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class CalState(val y: Int, val m: Int, val selected: LocalDate)

class CalendarViewModel : ViewModel() {
    val state = LocalDate.now().let { t -> Dates.info(t).fa.let { MutableStateFlow(CalState(it.y, it.m, t)) } }
    fun month(step: Int) = state.update {
        val n = it.m - 1 + step
        it.copy(y = it.y + Math.floorDiv(n, 12), m = Math.floorMod(n, 12) + 1)
    }
    fun select(d: LocalDate) = state.update { it.copy(selected = d) }
}

@Composable
fun CalendarScreen(vm: CalendarViewModel = viewModel()) {
    val st by vm.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    val today = LocalDate.now()
    val first = Dates.firstColumn(st.y, st.m)
    val count = Dates.monthLength(st.y, st.m)
    val sel = Dates.info(st.selected)

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton({ vm.month(-1) }) { Text("قبل") }
            Text("${Dates.faMonths[st.m - 1]} ${st.y.fa()}", style = MaterialTheme.typography.headlineMedium)
            TextButton({ vm.month(1) }) { Text("بعد") }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Dates.weekdays.forEachIndexed { i, w ->
                Text(w.take(1), Modifier.weight(1f), textAlign = TextAlign.Center, color = if (i == 6) cs.error else cs.outline)
            }
        }
        repeat((first + count + 6) / 7) { r ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { c ->
                    val day = r * 7 + c - first + 1
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), Alignment.Center) {
                        if (day in 1..count) {
                            val d = Dates.fromPersian(st.y, st.m, day)
                            val bg = when { d == st.selected -> cs.primary; d == today -> cs.surfaceVariant; else -> cs.background }
                            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).background(bg).clickable { vm.select(d) }, Alignment.Center) {
                                Text(day.fa(), color = when { d == st.selected -> cs.onPrimary; c == 6 -> cs.error; else -> cs.onBackground })
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), MaterialTheme.shapes.large, CardDefaults.cardColors(cs.surface)) {
            Column(Modifier.padding(20.dp)) {
                Text("${sel.weekday} ${sel.fa.d.fa()} ${Dates.faMonths[sel.fa.m - 1]}", style = MaterialTheme.typography.titleMedium)
                Text("${sel.greg.label()}  •  ${sel.hijri.hijriLabel()}", color = cs.outline)
            }
        }
    }
}
