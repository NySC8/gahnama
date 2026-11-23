package ir.gahnama

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/** بروزرسانی از آخرین ریلیز گیت‌هاب: تگ v0.2.N با APK به نام gahnama.apk */
object Updater {
    private const val REPO = "NySC8/gahnama"

    sealed interface S {
        object Idle : S
        object Latest : S
        data class New(val tag: String, val url: String) : S
        data class Busy(val pct: Int) : S
        data class Err(val msg: String) : S
    }

    var state: S by mutableStateOf(S.Idle); private set

    fun versionName(c: Context): String = c.packageManager.getPackageInfo(c.packageName, 0).versionName ?: ""
    private fun versionCode(c: Context) = PackageInfoCompat.getLongVersionCode(c.packageManager.getPackageInfo(c.packageName, 0)).toInt()

    fun check(c: Context, manual: Boolean = false) {
        if (state is S.Busy) return
        val app = c.applicationContext
        thread {
            state = try {
                val cn = URL("https://api.github.com/repos/$REPO/releases/latest").openConnection() as HttpURLConnection
                cn.connectTimeout = 10000; cn.readTimeout = 10000
                cn.setRequestProperty("Accept", "application/vnd.github+json")
                val j = JSONObject(cn.inputStream.bufferedReader().use { it.readText() })
                val tag = j.getString("tag_name")
                val assets = j.getJSONArray("assets")
                val apk = (0 until assets.length()).map { assets.getJSONObject(it) }.firstOrNull { it.getString("name").endsWith(".apk") }
                val remote = tag.substringAfterLast('.').toIntOrNull() ?: 0
                when {
                    apk != null && remote > versionCode(app) -> S.New(tag.removePrefix("v"), apk.getString("browser_download_url"))
                    manual -> S.Latest
                    else -> S.Idle
                }
            } catch (e: Exception) { if (manual) S.Err("اتصال به گیت‌هاب ممکن نشد") else S.Idle }
        }
    }

    fun install(c: Context, url: String) {
        if (Build.VERSION.SDK_INT >= 26 && !c.packageManager.canRequestPackageInstalls()) {
            state = S.Err("اجازهٔ نصب را روشن کنید و دوباره بروزرسانی را بزنید")
            c.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${c.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }
        val app = c.applicationContext
        thread {
            try {
                val dir = File(app.cacheDir, "updates").apply { mkdirs() }
                val f = File(dir, "gahnama.apk")
                val cn = URL(url).openConnection() as HttpURLConnection
                cn.connectTimeout = 15000; cn.readTimeout = 30000
                val total = cn.contentLength.toLong().coerceAtLeast(1L)
                cn.inputStream.use { input ->
                    f.outputStream().use { out ->
                        val buf = ByteArray(32768); var n = 0; var done = 0L
                        while (input.read(buf).also { n = it } > 0) { out.write(buf, 0, n); done += n; state = S.Busy((done * 100 / total).toInt()) }
                    }
                }
                val uri = FileProvider.getUriForFile(app, "${app.packageName}.files", f)
                app.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK))
                state = S.Idle
            } catch (e: Exception) { state = S.Err("دانلود ناموفق بود") }
        }
    }
}
