plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 31
    
    defaultConfig {
        applicationId = "com.johnturkson.sync"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildFeatures {
        compose = true
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
    
    buildFeatures {
        viewBinding = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-alpha02"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0")
    
    implementation("androidx.compose.ui:ui:1.2.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling:1.2.0-alpha02")
    implementation("androidx.compose.runtime:runtime:1.2.0-alpha02")
    implementation("androidx.compose.foundation:foundation:1.2.0-alpha02")
    implementation("androidx.compose.material:material:1.2.0-alpha02")
    implementation("androidx.compose.material3:material3:1.0.0-alpha04")
    
    implementation("androidx.navigation:navigation-compose:2.4.0-rc01")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    
    implementation("com.google.accompanist:accompanist-permissions:0.22.0-rc")
    
    implementation("com.google.dagger:hilt-android:2.40.5")
    kapt("com.google.dagger:hilt-compiler:2.40.5")
    
    implementation("androidx.room:room-runtime:2.4.1")
    kapt("androidx.room:room-compiler:2.4.1")
    implementation("androidx.room:room-ktx:2.4.1")
    
    implementation("androidx.camera:camera-core:1.1.0-beta01")
    implementation("androidx.camera:camera-camera2:1.1.0-beta01")
    implementation("androidx.camera:camera-lifecycle:1.1.0-beta01")
    implementation("androidx.camera:camera-view:1.1.0-beta01")
    implementation("com.google.mlkit:barcode-scanning:17.0.2")
    
    implementation("androidx.biometric:biometric:1.2.0-alpha04")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
