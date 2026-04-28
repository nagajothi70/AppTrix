plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    kotlin("kapt")
}

android {
    namespace = "com.example.apptrix"
    compileSdk = 34   // 🔥 36 stable illa → 34 use pannu

    defaultConfig {
        applicationId = "com.example.apptrix"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"   // 🔥 duplicate remove panniten
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    // 🔥 CORE
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    // 🔥 COMPOSE BOM
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // 🔥 NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 🔥 HILT
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // 🔥 VIEWMODEL
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // 🔥 FIREBASE
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.firebase:firebase-analytics:21.5.0")

    // 🔥 EXTRA
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.biometric:biometric:1.1.0")

    // 🔥 TEST
    testImplementation("junit:junit:4.13.2")
    implementation(project(":libraries:service"))
    implementation(project(":libraries:models"))
    implementation(project(":libraries:security"))
}
