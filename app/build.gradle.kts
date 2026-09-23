plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.nutrilensai"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.nutrilensai"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        mlModelBinding = false
    }
}

dependencies {

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Room Database
    implementation("androidx.room:room-runtime:2.8.3")
    annotationProcessor("androidx.room:room-compiler:2.8.3")
    
    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // Google ML Kit Image Labeling
    implementation("com.google.mlkit:image-labeling:17.0.9")
    // Google ML Kit Custom Image Labeling
    implementation("com.google.mlkit:image-labeling-custom:17.0.3")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    // CameraX
    implementation("androidx.camera:camera-core:1.5.0")
    implementation("androidx.camera:camera-camera2:1.5.0")
    implementation("androidx.camera:camera-lifecycle:1.5.0")
    implementation("androidx.camera:camera-view:1.5.0")

// ML Kit Barcode Scanner
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}