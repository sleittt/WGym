plugins {
    kotlin("jvm")
}

//android {
//    namespace = "com.example.domain"
//    compileSdk {
//        version = release(36)
//    }
//
//    defaultConfig {
//        applicationId = "com.example.domain"
//        minSdk = 33
//        targetSdk = 36
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    // если нужна дата-время в моделях
    implementation(libs.kotlinx.datetime)

    // Если хотите использовать @Inject в домене (опционально)
    implementation(libs.javax.inject)
}