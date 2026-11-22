package ir.gahnama

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
    val days = remember(st.y, st.m, Prefs.offset, Prefs.world, Prefs.religious) {
        (1..count).map { d -> Dates.fromPersian(st.y, st.m, d).let { it to Events.forDay(it) } }
    }
    val sel = Dates.info(st.selected, Prefs.offset)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        MonthBar("${Dates.faMonths[st.m - 1]} ${st.y.fa()}", { vm.month(-1) }, { vm.month(1) })
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
            Dates.weekdays.forEachIndexed { i, w ->
                Text(w.take(1), Modifier.weight(1f), textAlign = TextAlign.Center, color = if (i == 6) cs.error else cs.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
            }
        }
        repeat((first + count + 6) / 7) { r ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { c ->
                    val day = r * 7 + c - first + 1
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), Alignment.Center) {
                        if (day in 1..count) {
                            val (d, evs) = days[day - 1]
                            val isSel = d == st.selected
                            val hot = c == 6 || evs.any { it.h }
                            val bg = when { isSel -> cs.primary; d == today -> cs.surfaceVariant; else -> cs.background }
                            val fg = when { isSel -> cs.onPrimary; hot -> cs.error; else -> cs.onBackground }
                            Column(Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)).background(bg).clickable { vm.select(d) }, Arrangement.Center, Alignment.CenterHorizontally) {
                                Text(day.fa(), color = fg, style = MaterialTheme.typography.bodyLarge, fontWeight = if (d == today) FontWeight.Bold else null)
                                if (evs.isNotEmpty()) Box(Modifier.size(5.dp).clip(CircleShape).background(if (isSel) cs.onPrimary else if (hot) cs.error else cs.secondary))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Panel {
            Text("${sel.weekday} ${sel.fa.d.fa()} ${Dates.faMonths[sel.fa.m - 1]}", style = MaterialTheme.typography.titleMedium)
            Text(listOfNotNull(sel.greg.label(), if (Prefs.hijri) sel.hijri.hijriLabel() else null).joinToString(" · "), color = cs.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        EventsPanel("مناسبت‌های این روز", Events.forDay(st.selected))
    }
}
