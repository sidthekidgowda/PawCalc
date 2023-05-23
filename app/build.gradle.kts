@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.reactivecircus.app.versioning)
}

android {
    namespace = "com.sidgowda.pawcalc"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.sidgowda.pawcalc"
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "com.sidgowda.pawcalc.test.PawCalcTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("pawcalc-keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_STORE_PATH")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            animationsDisabled = true
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources {
            excludes += "**/attach_hotspot_windows.dll"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(projects.feature.newdog)
    implementation(projects.feature.editdog)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.doglist)
    implementation(projects.feature.dogdetails)
    implementation(projects.feature.settings)
    implementation(projects.common.navigation)
    implementation(projects.common.settings)
    implementation(projects.common.ui)
    implementation(projects.common.test)
    implementation(projects.core.data)
    implementation(projects.core.db)
    coreLibraryDesugaring(libs.android.tools.desugarJdk)
    implementation(projects.core.domain)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.navigation.compose)
    implementation(libs.jakewharton.timber.logging)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // test libraries
    testImplementation(projects.core.test)
    testImplementation(libs.mockk.test)
    testImplementation(libs.robolectric)
    androidTestImplementation(projects.core.test)
    androidTestImplementation(libs.adevinto.android.barista) {
        exclude(group = "org.jetbrains.kotlin")
    }
    kaptAndroidTest(libs.hilt.android.compiler)
}
