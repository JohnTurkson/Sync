plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.johnturkson.sync"
    
    compileSdk = 33
    
    defaultConfig {
        applicationId = "com.johnturkson.sync"
        minSdk = 26
        targetSdk = 33
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
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    
    implementation("androidx.compose.ui:ui:1.3.0-rc01")
    implementation("androidx.compose.ui:ui-tooling:1.3.0-rc01")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")
    
    implementation("androidx.navigation:navigation-compose:2.5.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    
    implementation("com.google.accompanist:accompanist-permissions:0.26.5-rc")
    
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    
    implementation("androidx.room:room-ktx:2.4.3")
    implementation("androidx.room:room-runtime:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    implementation("androidx.camera:camera-core:1.2.0-beta02")
    implementation("androidx.camera:camera-camera2:1.2.0-beta02")
    implementation("androidx.camera:camera-lifecycle:1.2.0-beta02")
    implementation("androidx.camera:camera-view:1.2.0-beta02")
    implementation("com.google.mlkit:barcode-scanning:17.0.2")
    
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
