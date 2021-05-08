plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.0"
}

group = "ru.shipa"
version = "1.0-SNAPSHOT"

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val junit5Version = "5.0.2"

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    api("org.slf4j:slf4j-log4j12:1.7.25")

    compileOnly("org.apache.kafka:kafka-clients:2.6.0")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("kotlin-test-junit"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
}
