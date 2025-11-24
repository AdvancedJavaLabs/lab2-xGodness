import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("io.freefair.lombok") version "9.0.0-rc2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.springframework.boot") version "3.4.3"
}

group = "org.itmo"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.itmo:common:1.0")
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter:3.4.3")
    implementation("org.slf4j:slf4j-api:2.0.17")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("${project.name}.jar")

    manifest {
        attributes(mapOf("Main-Class" to "org.itmo.Main"))
    }

    mergeServiceFiles()
}
