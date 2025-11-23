import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("io.freefair.lombok") version "9.0.0-rc2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.itmo"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.itmo:common:1.0")
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("${project.name}.jar")

    manifest {
        attributes(mapOf("Main-Class" to "org.itmo.Main"))
    }

    mergeServiceFiles()
}
