import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("io.freefair.lombok") version "9.0.0-rc2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.itmo"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.27.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("${project.name}.jar")
    mergeServiceFiles()
}
