@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}


android {
    namespace = "com.sidgowda.pawcalc.test"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.kotest.assertions.core)
    api(libs.androidx.test.espresso.idlingResource)
    api(libs.androidx.test.ext.junit)
    api(libs.hilt.android.testing)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.core)
    api(libs.mockk.test)
    api(libs.androidx.test.junit.ktx)
    api(libs.robolectric)
    api(libs.androidx.compose.ui.test.junit)
    api(libs.androidx.navigation.testing)
    api(libs.hilt.android.compiler)
    debugApi(libs.androidx.compose.ui.test.manifest)
}
