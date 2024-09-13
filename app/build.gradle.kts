plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    //room
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"

    id("kotlin-kapt")
}
//
android {
    namespace = "com.sylovestp.firebasetest.testspringrestapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sylovestp.firebasetest.testspringrestapp"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    //뷰 바인딩 설정, 뷰들을 하나의 인스턴스에 담아서, 꺼내서 사용하기., 뷰 선택하기 편하게
    viewBinding {
        enable = true
    }
}

dependencies {

    //뷰페이져2 추가
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // 코루틴 추가
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2'")
    //glide 추가
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    //retrofit 추가, gson 컨버터 추가.
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
// json 변환하기 위한 또라이브러리, gson 예시.
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    //room
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.mediation.test.suite)
    implementation(libs.androidx.media3.common)
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    //room

    // 추가
    implementation ("androidx.fragment:fragment-ktx:1.3.6")
    // 추가 glide 관련 툴 .
    kapt ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")
    // webkit, 다음 주소 웹 뷰 사용하기.
    implementation("androidx.webkit:webkit:1.8.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}