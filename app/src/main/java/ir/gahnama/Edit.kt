package ir.gahnama

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/** ارقام فارسی و عربی را هم می‌پذیرد */
fun String.toIntFa(): Int? = map { when (it) { in '۰'..'۹' -> '0' + (it - '۰'); in '٠'..'٩' -> '0' + (it - '٠'); else -> it } }.joinToString("").toIntOrNull()

/** افزودن یا ویرایش مناسبت: init = null یعنی مناسبت جدید برای تاریخ date */
@Composable
fun EventDialog(init: Ev?, date: LocalDate, onDone: () -> Unit) {
    val i = Dates.info(date, Prefs.offset)
    var name by remember { mutableStateOf(init?.n ?: "") }
    var c by remember { mutableStateOf(init?.c ?: 's') }
    var m by remember { mutableStateOf((init?.m ?: i.fa.m).fa()) }
    var d by remember { mutableStateOf((init?.d ?: i.fa.d).fa()) }
    var t by remember { mutableStateOf(init?.t ?: 'p') }
    var hol by remember { mutableStateOf(init?.h ?: false) }
    var once by remember { mutableStateOf((init?.y ?: 0) != 0) }
    val cals = listOf('s', 'l', 'g')
    val types = listOf('p', 'n', 'r', 'w')
    fun pick(k: Char) {
        c = k
        if (init == null) {
            val (mm, dd) = when (k) { 's' -> i.fa.m to i.fa.d; 'l' -> i.hijri.m to i.hijri.d; else -> date.monthValue to date.dayOfMonth }
            m = mm.fa(); d = dd.fa()
        }
    }
    val mi = m.toIntFa(); val di = d.toIntFa()
    val ok = name.isNotBlank() && mi in 1..12 && di in 1..31
    val num = KeyboardOptions(keyboardType = KeyboardType.Number)

    AlertDialog(
        onDismissRequest = onDone,
        title = { Text(if (init == null) "مناسبت جدید" else "ویرایش مناسبت") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("عنوان") }, singleLine = true)
                Text("تقویم")
                Chips(listOf("شمسی", "قمری", "میلادی"), cals.indexOf(c)) { pick(cals[it]) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(m, { m = it }, Modifier.weight(1f), label = { Text("ماه") }, singleLine = true, keyboardOptions = num)
                    OutlinedTextField(d, { d = it }, Modifier.weight(1f), label = { Text("روز") }, singleLine = true, keyboardOptions = num)
                }
                Text("نوع")
                Chips(types.map { typeLabel(it) }, types.indexOf(t)) { t = types[it] }
                Toggle("تعطیل رسمی", hol) { hol = it }
                Toggle("فقط در همین سال", once) { once = it }
            }
        },
        confirmButton = {
            TextButton(enabled = ok, onClick = {
                val year = when (c) { 's' -> i.fa.y; 'l' -> i.hijri.y; else -> date.year }
                val y = if (!once) 0 else if (init != null && init.y != 0 && init.c == c) init.y else year
                Events.save(Ev(init?.id ?: "u${System.currentTimeMillis()}", c, mi ?: 1, di ?: 1, name.trim(), t, hol, y))
                onDone()
            }) { Text("ذخیره") }
        },
        dismissButton = {
            Row {
                if (init != null) TextButton({ Events.delete(init.id); onDone() }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                TextButton(onDone) { Text("انصراف") }
            }
        },
    )
}
