plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.blacksuits"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.blacksuits"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")

//    implementation (platform("com.google.firebase:firebase-bom:28.0.1"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")


    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    // FirebaseUI for Cloud Firestore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    // FirebaseUI for Firebase Auth
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")

    // FirebaseUI for Cloud Storage
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")

    implementation("com.android.volley:volley:1.2.1")

    implementation ("com.google.code.gson:gson:2.8.9")




    implementation("com.google.firebase:firebase-messaging:23.4.0")

//    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Add the dependencies for the In-App Messaging and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-inappmessaging-display")
    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-installations:17.2.0")

    implementation ("com.google.android.gms:play-services-location:19.0.1")


    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")

    implementation ("com.google.code.gson:gson:2.6.2")
    implementation ("com.squareup.retrofit2:retrofit:2.0.2")
    implementation ("com.squareup.retrofit2:converter-gson:2.0.2")
    implementation ("com.github.Dhaval2404:ImagePicker:v2.1")



}