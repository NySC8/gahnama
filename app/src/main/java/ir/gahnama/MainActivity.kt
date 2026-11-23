package ir.gahnama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private enum class Tab(val label: String, val icon: ImageVector) {
    Calendar("تقویم", Ic.Calendar), Events("مناسبت‌ها", Ic.Flag), Settings("تنظیمات", Ic.Sliders), About("درباره", Ic.Info)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Prefs.init(this); Events.load(this); Updater.check(this)
        setContent { GahnamaTheme { App() } }
    }

    // با هر بازگشت به برنامه، اعلان و ویجت دوباره با تاریخ امروز تازه می‌شوند
    override fun onResume() { super.onResume(); Notifier.refresh(this) }
}

@Composable
private fun App() {
    val cs = MaterialTheme.colorScheme
    val nav = rememberNavController()
    val current = nav.currentBackStackEntryAsState().value?.destination?.route
    // رنگ پیش‌فرض متن؛ بدون آن در تم تاریک بعضی متن‌ها سیاه می‌ماند
    CompositionLocalProvider(LocalContentColor provides cs.onBackground) {
        Box(Modifier.fillMaxSize().glassBackground()) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    Column {
                        HorizontalDivider(color = glassStroke())
                        NavigationBar(containerColor = cs.surface, tonalElevation = 0.dp) {
                            Tab.entries.forEach { t ->
                                val selected = current == t.name
                                NavigationBarItem(
                                    selected = selected, label = { Text(t.label, style = MaterialTheme.typography.labelMedium) },
                                    icon = {
                                        val s by animateFloatAsState(if (selected) 1.15f else 1f, spring(dampingRatio = 0.5f), label = "icon")
                                        Icon(t.icon, t.label, Modifier.scale(s))
                                    },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = cs.surfaceVariant, selectedIconColor = cs.primary, selectedTextColor = cs.primary, unselectedIconColor = cs.onSurfaceVariant, unselectedTextColor = cs.onSurfaceVariant),
                                    onClick = { nav.navigate(t.name) { popUpTo(Tab.Calendar.name); launchSingleTop = true } },
                                )
                            }
                        }
                    }
                },
            ) { pad ->
                NavHost(
                    nav, Tab.Calendar.name, Modifier.padding(pad),
                    enterTransition = { fadeIn(tween(240)) + slideInVertically(tween(280)) { it / 20 } },
                    exitTransition = { fadeOut(tween(120)) },
                ) {
                    composable(Tab.Calendar.name) { CalendarScreen() }
                    composable(Tab.Events.name) { EventsScreen() }
                    composable(Tab.Settings.name) { SettingsScreen() }
                    composable(Tab.About.name) { AboutScreen() }
                }
            }
        }
    }
}
