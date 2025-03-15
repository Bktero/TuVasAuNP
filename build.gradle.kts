plugins {
    kotlin("jvm") version "2.1.10"
//    kotlin("plugin.serialization") version "2.1.10"
}

group = "dev.gradot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.json:json:20231013")
    implementation("org.telegram:telegrambots-client:8.2.0")
    implementation("org.telegram:telegrambots-longpolling:8.2.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}