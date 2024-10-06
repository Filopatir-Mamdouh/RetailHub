import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.iti4.retailhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iti4.retailhub"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField(
            "String",
            "ADMIN_ACCESS_TOKEN",
            properties.getProperty("ADMIN_ACCESS_TOKEN_STRING")
        )
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
}

dependencies {
    //firebase auth
    implementation (platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:20.0.0")
    //Pagination

    implementation("androidx.paging:paging-runtime:3.3.2")

    //Apollo
    implementation(libs.apollo.runtime)

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.1")

    // lottie animation
    implementation("com.airbnb.android:lottie:6.5.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    // Retrofit
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    //Google Location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // map
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    //circular imageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.common)
    kapt(libs.hilt.android.compiler)

    //Stripe
    implementation("com.stripe:stripe-android:20.51.1")

    // facebook shimmer

    implementation("com.facebook.shimmer:shimmer:0.5.0")
    //------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}

apollo {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    service("service") {
        packageName.set("com.iti4.retailhub")
        introspection {
            endpointUrl.set("https://android-alex-team4.myshopify.com/admin/api/2024-10/graphql.json")
            headers.put("X-Shopify-Access-Token", properties.getProperty("ADMIN_ACCESS_TOKEN"))
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}