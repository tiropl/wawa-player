plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "androidx.media3.exoplayer"
    compileSdk = 37
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":lib-common"))
    api(project(":lib-container"))
    api(project(":lib-datasource"))
    api(project(":lib-decoder"))
    api(project(":lib-extractor"))
    api(project(":lib-database"))
    implementation(libs.annotation)
    implementation(libs.exifinterface)
    implementation(libs.juniversalchardet)
    compileOnly(libs.errorprone.annotations)
    compileOnly(libs.checkerframework.qual)
    compileOnly(libs.kotlin.annotations.jvm)
    compileOnly(libs.j2objc.annotations)
    compileOnly(libs.animal.sniffer.annotations)
}
