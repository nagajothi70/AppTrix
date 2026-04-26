plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false

    // ✅ ktlint plugin (THIS IS ENOUGH)
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}
