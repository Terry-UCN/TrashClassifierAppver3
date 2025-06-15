plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.trashclassifier"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.trashclassifier"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {


        // 使用 lite 版本可避免 class 重複問題
    //noinspection Aligned16KB
    implementation(libs.pytorch.android.lite.v1131)
    //noinspection Aligned16KB,UseTomlInstead
    implementation("org.pytorch:pytorch_android_torchvision_lite:1.13.1")
    //noinspection UseTomlInstead
    implementation("androidx.appcompat:appcompat:1.7.1")  // 加入 AppCompat

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}