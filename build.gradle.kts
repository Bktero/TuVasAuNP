plugins {
    kotlin("jvm") version "2.4.10"
    application
//    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
//    id("io.gitlab.arturbosch.detekt") version "2.0.0-alpha.6"
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
    implementation("ch.qos.logback:logback-classic:1.6.5")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("io.jenetics:jpx:3.2.1")
    implementation("io.ktor:ktor-server-core:3.6.0")
    implementation("io.ktor:ktor-server-netty:3.6.0")
    implementation("org.json:json:20231013")
    implementation("org.telegram:telegrambots-client:10.0.0")
    implementation("org.telegram:telegrambots-longpolling:10.3.0")
}

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
