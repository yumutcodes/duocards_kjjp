plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
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
    kotlinOptions {
        jvmTarget = "11"
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

    // Jetpack Compose ile ilgili diğer bağımlılıklarınız
    implementation(libs.androidx.compose.material3)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}