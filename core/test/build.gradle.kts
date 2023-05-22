@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}


android {
    namespace = "com.sidgowda.pawcalc.test"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.common.settings)
    implementation(libs.androidx.datastore.preferences)
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.kotest.assertions.core)
    api(libs.androidx.test.espresso.idlingResource)
    api(libs.hilt.android)
    api(libs.hilt.android.testing)
    kapt(libs.hilt.android.compiler)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.core)
    api(libs.androidx.compose.ui.test.junit)
    debugApi(libs.androidx.compose.ui.test.manifest)
    api(libs.androidx.navigation.testing)
    api(libs.androidx.arch.core.testing)
    api(libs.app.cash.turbine)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.ext.junit.ktx)
}
