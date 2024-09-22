plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.compose.compiler)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "com.vl.victorlekweuwaweatherapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vl.victorlekweuwaweatherapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        /*freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )*/
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.robolectric)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Mockito
//    testImplementation(libs.mockito.core)
//    testImplementation(libs.mockito.inline)
//    testImplementation(libs.mockito.kotlin)
//    androidTestImplementation(libs.mockito.android)

    // Room testing
    testImplementation(libs.androidx.room.testing)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Json Serialization
    implementation(libs.kotlinx.serialization.json)

    //Location
    implementation(libs.play.services.location)

    //Coil Network Images
    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
//    implementation(libs.androidx.hilt.lifecycle.viewmodel)
    kapt(libs.hilt.android.compiler)

//    // For Robolectric tests.
//    testImplementation(libs.hilt.android.testing)
//
//    // ...with Kotlin.
//    kaptTest(libs.hilt.android.compiler)
//
//    // ...with Java.
//    testAnnotationProcessor(libs.hilt.android.compiler)
//
//    // For instrumented tests.
//    androidTestImplementation(libs.hilt.android.testing)
//
//    // ...with Kotlin.
//    kaptAndroidTest(libs.hilt.android.compiler)
//
//    // ...with Java.
//    androidTestAnnotationProcessor(libs.hilt.android.compiler)

    // Retrofit + GSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)

    // Lifecycle utilities for Compose
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // Annotation processor
    kapt(libs.androidx.lifecycle.compiler)
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation(libs.androidx.lifecycle.common.java8)

    // optional - helpers for implementing LifecycleOwner in a Service
    implementation(libs.androidx.lifecycle.service)

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    implementation(libs.androidx.lifecycle.process)

    // optional - ReactiveStreams support for LiveData
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)

    // optional - Test helpers for LiveData
    testImplementation(libs.androidx.core.testing)

    // optional - Test helpers for Lifecycle runtime
    testImplementation (libs.androidx.lifecycle.runtime.testing)
}