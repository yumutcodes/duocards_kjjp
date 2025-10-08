plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.duocardsapplication2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.duocardsapplication2"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

}

dependencies {
    // Bölüm 1: Ağ (Network)
    // API istekleri ve JSON işlemleri için
    implementation(libs.okhttp)
    //libs.logging.interceptor
    implementation(libs.logging.interceptor)
    //retrofit
    implementation(libs.retrofit)


    // Bölüm 2: Bağımlılık Enjeksiyonu (Dependency Injection)
    // Hilt ile bağımlılıkların yönetimi
    implementation(libs.hilt.android)
    // Hilt'in Compose Navigation ile entegrasyonu
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material3)
    ksp(libs.hilt.compiler)


    // Bölüm 3: Veritabanı (Database)
    // Lokal veri depolama için Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Coroutines ve Flow desteği için
    ksp(libs.androidx.room.compiler) // Derleme zamanında kod üretimi için

    // Bölüm 4: Güvenlik (Security)
    // Şifrelenmiş SharedPreferences ve dosyalar için
    implementation(libs.androidx.datastore.preferences)

    // Bölüm 5: Kullanıcı Arayüzü (UI) - Jetpack Compose
    // Modern ve deklaratif UI oluşturmak için
    implementation(platform(libs.androidx.compose.bom)) // Kütüphane sürümlerini yönetmek için
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Activity ile Compose entegrasyonu
    implementation(libs.androidx.activity.compose)

    // Bölüm 6: Navigasyon (Navigation)
    // Compose'da ekranlar arası geçiş için
    implementation(libs.androidx.navigation.compose)

    // Bölüm 7: Android Çekirdek ve Yaşam Döngüsü (Core & Lifecycle)
    // Temel Android bileşenleri ve ViewModel
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Jetpack Compose ile ilgili diğer bağımlılıklarınız
    implementation(libs.androidx.compose.material3)

    // Bölüm 8: Serileştirme (Serialization)
    // Kotlin nesnelerini JSON'a dönüştürmek için
    //serialaztion moshi rakibi ama moshi daha iyi galiba
   implementation(libs.kotlinx.serialization.json)
    //Moshi:
//https://aistudio.google.com/prompts/1uelVZYGaR-XlJxqqIDpSVb9i4adSyNsw
    // Moshi'nin ana kütüphanesi
    implementation(libs.moshi)
    /*
    json işlemleri için moshi
     */
    implementation(libs.moshi.kotlin)
    // Retrofit ile Moshi'yi bağlayan dönüştürücü
    implementation(libs.converter.moshi)
    // codegen işlemi için
    ksp(libs.moshi.kotlin.codegen)

    // Bölüm 9: Test Kütüphaneleri
    // Birim ve UI testleri için
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Testler için de BOM kullanımı
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Bölüm 10: Hata Ayıklama (Debug)
    // Sadece debug derlemelerinde kullanılan araçlar
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.timber)
//utiluties
    implementation(libs.androidx.compose.material.icons.extended)


}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}