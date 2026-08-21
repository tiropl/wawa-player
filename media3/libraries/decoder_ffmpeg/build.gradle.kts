import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
}

val nativeDependencies = Properties().apply {
    file("src/main/resources/META-INF/androidx.media3.decoder.ffmpeg/native-dependencies.properties")
        .inputStream()
        .use(::load)
}

android {
    namespace = "androidx.media3.decoder.ffmpeg"
    compileSdk = 37
    ndkVersion = nativeDependencies.getProperty("android.ndk.version")
    defaultConfig {
        minSdk = 24
        ndk { abiFilters += listOf("armeabi-v7a", "arm64-v8a") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Configure native build only if ffmpeg headers are present
if (project.file("src/main/ffmpeg/include/libavcodec/avcodec.h").exists()) {
    android.externalNativeBuild.cmake.path = file("src/main/jni/CMakeLists.txt")
    android.externalNativeBuild.cmake.version = "3.21.0+"
}

dependencies {
    api(project(":lib-decoder"))
    implementation(project(":lib-exoplayer"))
    implementation(libs.annotation)
    compileOnly(libs.errorprone.annotations)
    compileOnly(libs.checkerframework.qual)
    compileOnly(libs.kotlin.annotations.jvm)
    compileOnly(libs.j2objc.annotations)
    compileOnly(libs.animal.sniffer.annotations)
}
