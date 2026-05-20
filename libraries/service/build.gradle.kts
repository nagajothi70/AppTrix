plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    kotlin("kapt")
}

android {
    namespace = "com.example.service"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.biometric:biometric:1.1.0")
    // 🔥 FIREBASE
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation(project(":libraries:models"))
    implementation(project(":libraries:security"))


}
