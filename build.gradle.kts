plugins {
    kotlin("jvm") version "2.0.20"
}

group = "br.com.dhionata"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the main function"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("br.com.dhionata.MainKt")
}