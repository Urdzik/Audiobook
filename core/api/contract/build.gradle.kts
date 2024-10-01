plugins {
    id("audiobook.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.urdzik.core.api.contract"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

}