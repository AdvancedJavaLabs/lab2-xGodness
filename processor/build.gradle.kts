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
    implementation("org.itmo:common:1.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.1")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("${project.name}.jar")

    manifest {
        attributes(mapOf("Main-Class" to "org.itmo.Main"))
    }

    mergeServiceFiles()
}