plugins {
    id("audiobook.android.application")
    id("audiobook.android.application.compose")
    id("kotlin-parcelize")
    id("audiobook.firebase")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.urdzik.audiobook"

    buildTypes {
        getByName("debug") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(projects.feature.player.presentation)
    implementation(projects.feature.player.data)
    implementation(projects.core.ui)
    implementation(projects.core.api.impl)
    implementation(projects.core.database.impl)
    testImplementation(libs.junit)
    testImplementation(project(":core:api:contract"))
    testImplementation(project(":core:database:contract"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.robolectric)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)


}