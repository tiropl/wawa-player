plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "androidx.media3.decoder"
    compileSdk = 37
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":lib-common"))
    implementation(libs.annotation)
    compileOnly(libs.errorprone.annotations)
    compileOnly(libs.checkerframework.qual)
    compileOnly(libs.kotlin.annotations.jvm)
    compileOnly(libs.j2objc.annotations)
    compileOnly(libs.animal.sniffer.annotations)
}
