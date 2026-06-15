plugins {
    id("com.android.library")
    //kotlin("android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
//        applicationId = "com.example.data"
        minSdk = 33
//        targetSdk = 36
//        versionCode = 1
//        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":domain"))

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // или ks
    implementation(libs.androidx.room.ktx)

    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Сериализация для контента постов (JSON в Entity)
    implementation(libs.kotlinx.serialization.json)

    // Если нужен маппинг Flow из Firestore – может понадобиться
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.kotlinx.datetime)

}