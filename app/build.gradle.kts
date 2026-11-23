plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "ir.gahnama"
    compileSdk = 34
    defaultConfig {
        applicationId = "ir.gahnama"; minSdk = 24; targetSdk = 34
        // در GitHub Actions با -Pvc و -Pvn مقدار می‌گیرد تا هر نسخه از قبلی جدیدتر باشد
        versionCode = (findProperty("vc") as String?)?.toInt() ?: 1
        versionName = (findProperty("vn") as String?) ?: "0.2.0"
    }
    // کلید امضای ثابت: بدون آن، هر بیلد کلید متفاوتی دارد و اندروید بروزرسانی روی نسخهٔ قبلی را نمی‌پذیرد
    signingConfigs {
        create("app") { storeFile = file("gahnama.keystore"); storePassword = "gahnama"; keyAlias = "gahnama"; keyPassword = "gahnama" }
    }
    buildTypes {
        debug { signingConfig = signingConfigs.getByName("app") }
        release { signingConfig = signingConfigs.getByName("app"); isMinifyEnabled = true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt")) }
    }
    compileOptions { isCoreLibraryDesugaringEnabled = true; sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}
dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.core:core-ktx:1.13.1")
}
