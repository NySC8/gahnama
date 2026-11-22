package ir.gahnama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private enum class Tab(val label: String, val icon: ImageVector) {
    Home("امروز", Icons.Default.Home), Calendar("تقویم", Icons.Default.DateRange),
    Events("مناسبت", Icons.Default.Star), Settings("تنظیمات", Icons.Default.Settings), About("درباره", Icons.Default.Info)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GahnamaTheme { App() } }
    }
}

@androidx.compose.runtime.Composable
private fun App() {
    val nav = rememberNavController()
    val current = nav.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = current == t.name, label = { Text(t.label) }, icon = { Icon(t.icon, t.label) },
                        onClick = { nav.navigate(t.name) { popUpTo(Tab.Home.name); launchSingleTop = true } },
                    )
                }
            }
        },
    ) { pad ->
        NavHost(nav, Tab.Home.name, Modifier.padding(pad), enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
            composable(Tab.Home.name) { HomeScreen() }
            composable(Tab.Calendar.name) { CalendarScreen() }
            composable(Tab.Events.name) { EventsScreen() }
            composable(Tab.Settings.name) { SettingsScreen() }
            composable(Tab.About.name) { AboutScreen() }
        }
    }
}
