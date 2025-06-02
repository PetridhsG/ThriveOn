import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.realm)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "gr.aueb.thriveon"
    compileSdk = 35

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }

    val apiKey = localProperties["API_KEY"] ?: ""
    val baseUrl = localProperties["BASE_URL"] ?: ""
    val model = localProperties["MODEL"] ?: ""
    val temperature = localProperties["TEMPERATURE"]?.toString()?.toDoubleOrNull() ?: 0.0

    defaultConfig {
        applicationId = "gr.aueb.thriveon"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "OPENAI_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "MODEL", "\"$model\"")
        buildConfigField("double", "TEMPERATURE", temperature.toString())
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //Android Core Libraries
    implementation(libs.bundles.androidx.core)

    //Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    //Serialization
    implementation(libs.bundles.serialization)

    //Dependency Injection (Koin)
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    //Realm
    implementation(libs.realm.kotlin.base)

    //Security
    implementation(libs.androidx.security.crypto)

    //Retrofit
    implementation(libs.bundles.retrofit)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Google Play Services
    implementation(libs.google.play.services.auth)

    //Coil
    implementation(libs.coil.compose)

    //Ucrop
    implementation(libs.ucrop)
}
