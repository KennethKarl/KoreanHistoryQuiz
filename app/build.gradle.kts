plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.historyquiz.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.historyquiz.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ── 빌드 플레이버 ──────────────────────────────────────────────────────
    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://dev-api.historyquiz.com/v1\"")
            buildConfigField("String", "ENV", "\"dev\"")
        }
        create("staging") {
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BASE_URL", "\"https://staging-api.historyquiz.com/v1\"")
            buildConfigField("String", "ENV", "\"staging\"")
        }
        create("prod") {
            buildConfigField("String", "BASE_URL", "\"https://api.historyquiz.com/v1\"")
            buildConfigField("String", "ENV", "\"prod\"")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Room 스키마 export 경로
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

dependencies {
    // ── AndroidX Core ────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.splashscreen)

    // ── Material Design 3 ────────────────────────────────────────────────
    implementation(libs.material)

    // ── Navigation ───────────────────────────────────────────────────────
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // ── Lifecycle ────────────────────────────────────────────────────────
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // ── Coroutines ───────────────────────────────────────────────────────
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // ── DI (Koin) ────────────────────────────────────────────────────────
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // ── Network ──────────────────────────────────────────────────────────
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // ── Room ─────────────────────────────────────────────────────────────
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // ── DataStore ────────────────────────────────────────────────────────
    implementation(libs.datastore.preferences)

    // ── Image ────────────────────────────────────────────────────────────
    implementation(libs.coil)

    // ── Firebase ─────────────────────────────────────────────────────────
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // ── Testing ──────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.koin.test)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}
