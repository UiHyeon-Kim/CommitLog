plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hanhyo.commitlog.data"
    compileSdk = 36

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        // 원래 Entity나 Dao 하나만 수정해도 Room 관련 코드 전체 재생성
        // ture 시 변경된 파일만 처리하고 나머지 캐시 재사용 -> 빌드 속도 개선
        arg("room.incremental", "true")
        // 컴파일 시점에 (SELECT *) 의 칼럼을 미리 펼침 (상세 노션 확인)
        arg("room.expandProjection", "true")
    }

    defaultConfig {
        minSdk = 30

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
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.bundles.androidx.core)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Networking
    implementation(libs.bundles.networking)
    implementation(platform(libs.okhttp.bom))

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Utils
    implementation(libs.timber)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.test.unit)
}
