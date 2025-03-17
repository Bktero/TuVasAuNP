plugins {
    kotlin("jvm") version "2.1.10"
    application
//    kotlin("plugin.serialization") version "2.1.10"
}

group = "dev.gradot"
version = "1.0-SNAPSHOT"

application {
    // https://docs.gradle.org/current/userguide/application_plugin.html

    mainClass = "MainKt"
    // The file is Main.kt, but because our main() function is defined at top-level,
    // we must use MainKt (because Kotlin creates an implicit companion object).
    // We could have used Main and @JvmStatic, but it's noisier
}

dependencies {
    testImplementation(kotlin("test"))
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.json:json:20231013")
    implementation("org.telegram:telegrambots-client:8.2.0")
    implementation("org.telegram:telegrambots-longpolling:8.2.0")
}

kotlin {
    jvmToolchain(23)
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
