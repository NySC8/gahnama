package ir.gahnama

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    val selected = MutableStateFlow(LocalDate.now())
    fun select(d: LocalDate) { selected.value = d }
}

private const val MID = 600 // صفحهٔ وسط = ماه جاری؛ ۶۰۰ ماه به هر طرف

/** عنوان فرعی ماه: ماه‌های میلادی و قمری که با این ماه شمسی هم‌پوشانی دارند */
private fun span(y: Int, m: Int): String {
    val a = Dates.fromPersian(y, m, 1)
    val b = Dates.fromPersian(y, m, Dates.monthLength(y, m))
    val g = if (a.monthValue == b.monthValue) "${Dates.gregMonths[a.monthValue - 1]} ${a.year.fa()}"
            else "${Dates.gregMonths[a.monthValue - 1]} – ${Dates.gregMonths[b.monthValue - 1]} ${b.year.fa()}"
    if (!Prefs.hijri) return g
    val ha = Dates.info(a, Prefs.offset).hijri; val hb = Dates.info(b, Prefs.offset).hijri
    val h = if (ha.m == hb.m) "${Dates.hijriMonths[ha.m - 1]} ${ha.y.fa()}" else "${Dates.hijriMonths[ha.m - 1]} – ${Dates.hijriMonths[hb.m - 1]} ${hb.y.fa()}"
    return "$g · $h"
}

@Composable
fun CalendarScreen(vm: CalendarViewModel = viewModel()) {
    val cs = MaterialTheme.colorScheme
    val sel by vm.selected.collectAsState()
    val today = LocalDate.now()
    val base = remember { Dates.info(today).fa.let { it.y * 12 + it.m - 1 } }
    val pager = rememberPagerState(MID) { 1200 }
    val scope = rememberCoroutineScope()
    val idx = base + pager.currentPage - MID
    var editing by remember { mutableStateOf<Ev?>(null) }
    var adding by remember { mutableStateOf(false) }
    val showBanner = Updater.state.let { it is Updater.S.New || it is Updater.S.Busy || it is Updater.S.Err }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            AnimatedVisibility(showBanner, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Column { UpdateBanner(); Spacer(Modifier.height(12.dp)) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(idx, Modifier.weight(1f), transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) }, label = "month") { i ->
                    val y = Math.floorDiv(i, 12); val m = Math.floorMod(i, 12) + 1
                    Column {
                        Text("${Dates.faMonths[m - 1]} ${y.fa()}", style = MaterialTheme.typography.headlineMedium)
                        Text(span(y, m), color = cs.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    }
                }
                OutlinedButton({ scope.launch { pager.animateScrollToPage(MID) }; vm.select(today) }) { Text("امروز") }
            }
        }
        Panel(pad = 8.dp) {
            Row(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                Dates.weekdays.forEachIndexed { k, w ->
                    Text(w.take(1), Modifier.weight(1f), textAlign = TextAlign.Center, color = if (k == 6) cs.error else cs.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
                }
            }
            HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth().height(324.dp)) { p ->
                val i = base + p - MID
                MonthGrid(Math.floorDiv(i, 12), Math.floorMod(i, 12) + 1, sel, today) { vm.select(it) }
            }
        }
        DayPanel(sel, { adding = true }) { editing = it }
    }
    if (adding || editing != null) EventDialog(editing, sel) { adding = false; editing = null }
}

@Composable
private fun MonthGrid(y: Int, m: Int, selected: LocalDate, today: LocalDate, onSelect: (LocalDate) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val first = remember(y, m) { Dates.firstColumn(y, m) }
    val count = remember(y, m) { Dates.monthLength(y, m) }
    val days = remember(y, m, Prefs.offset, Prefs.world, Prefs.religious, Events.all) {
        (1..count).map { d -> Dates.fromPersian(y, m, d).let { it to Events.forDay(it) } }
    }
    val shape = RoundedCornerShape(14.dp)
    Column(Modifier.fillMaxSize()) {
        repeat(6) { r ->
            Row(Modifier.weight(1f).fillMaxWidth()) {
                repeat(7) { c ->
                    val day = r * 7 + c - first + 1
                    Box(Modifier.weight(1f).fillMaxHeight().padding(2.dp), Alignment.Center) {
                        if (day in 1..count) {
                            val (d, evs) = days[day - 1]
                            val isSel = d == selected
                            val hot = c == 6 || evs.any { it.h }
                            // انتخاب روز با انیمیشن نرم رنگ و اندازه
                            val bg by animateColorAsState(if (isSel) cs.primary else Color.Transparent, tween(220), label = "bg")
                            val fg by animateColorAsState(when { isSel -> cs.onPrimary; hot -> cs.error; else -> cs.onBackground }, tween(220), label = "fg")
                            val sc by androidx.compose.animation.core.animateFloatAsState(if (isSel) 1f else 0.94f, tween(220), label = "sc")
                            Column(
                                Modifier.fillMaxSize().graphicsLayer { scaleX = sc; scaleY = sc }.clip(shape).background(bg)
                                    .then(if (d == today && !isSel) Modifier.border(1.5.dp, cs.primary, shape) else Modifier)
                                    .clickable { onSelect(d) },
                                Arrangement.Center, Alignment.CenterHorizontally,
                            ) {
                                Text(day.fa(), color = fg, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(d.dayOfMonth.fa(), color = fg.copy(alpha = 0.6f), fontSize = 11.sp, lineHeight = 14.sp)
                                    if (evs.isNotEmpty()) Box(Modifier.size(5.dp).clip(CircleShape).background(if (isSel) cs.onPrimary else if (hot) cs.error else cs.secondary))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayPanel(d: LocalDate, onAdd: () -> Unit, onEdit: (Ev) -> Unit) {
    Panel(Modifier.animateContentSize()) {
        AnimatedContent(d, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(100)) }, label = "day") { dd -> DayBody(dd, onEdit) }
        Spacer(Modifier.height(8.dp))
        FilledTonalButton(onAdd, Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Ic.Add, null); Spacer(Modifier.width(8.dp)); Text("افزودن مناسبت")
        }
    }
}

@Composable
private fun DayBody(d: LocalDate, onEdit: (Ev) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val x = Dates.info(d, Prefs.offset)
    val evs = Events.forDay(d)
    val official = evs.any { it.h }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DayTile(x.fa.d.fa(), 64.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("${x.weekday} ${Dates.faMonths[x.fa.m - 1]} ${x.fa.y.fa()}", style = MaterialTheme.typography.titleMedium)
                Text(listOfNotNull(x.greg.label(), if (Prefs.hijri) x.hijri.hijriLabel() else null).joinToString(" · "), color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
            if (official || x.friday) Tag(if (official) "تعطیل رسمی" else "جمعه", true)
        }
        Spacer(Modifier.height(8.dp))
        if (evs.isEmpty()) Text("مناسبتی برای این روز ثبت نشده است.", color = cs.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
        evs.forEach { e -> EventLine(e) { onEdit(e) } }
    }
}
