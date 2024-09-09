plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.10"
}

group = "example.com"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
//    Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
//    Log
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.logback.gcp)
//    Google Cloud
    implementation(libs.google.cloud.bigquery)
    implementation(libs.google.cloud.storage)
//    Misc
    implementation(libs.apache.csv)
//    Testing
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.testcontainers.postgres)
}
