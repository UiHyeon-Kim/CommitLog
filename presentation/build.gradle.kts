plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hanhyo.commitlog.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.androidx.ui)

    implementation(libs.bundles.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Glance Widget
    implementation(libs.bundles.glance)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // UI Tools
    implementation(libs.bundles.vico)
    implementation(libs.shimmer.compose)

    // Async
    implementation(libs.kotlinx.serialization.json)

    // Utils
    implementation(libs.timber)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(libs.bundles.test.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.bundles.compose.debug)
    debugImplementation(libs.leakcanary)

    // Glance Widget Preview
    debugImplementation(libs.bundles.glance.debug)
}
