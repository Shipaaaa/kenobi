import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm")
}

group = "ru.shipa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val igniteVersion = "2.9.1"
    val junit5Version = "5.0.2"

    implementation(project(":core"))
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    implementation("com.sksamuel.scrimage:scrimage-core:4.0.16")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.16")

    implementation("org.apache.ignite:ignite-spark-2.4:$igniteVersion")
    implementation("org.apache.ignite:ignite-kubernetes:$igniteVersion")
    implementation("javax.cache:cache-api:1.1.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("kotlin-test-junit"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
}

application {
    mainClass.set("ru.shipa.image.processing.ImageProcessingApp")
    mainClassName = "ru.shipa.image.processing.ImageProcessingApp"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

/*
tasks.jar {
    manifest {
        attributes("Main-Class" to "ru.shipa.image.processing.ImageProcessingApp")
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
*/
tasks.withType<ShadowJar> {
    isZip64 = true

    manifest {
        attributes["Main-Class"] = "ru.shipa.image.processing.ImageProcessingApp"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
