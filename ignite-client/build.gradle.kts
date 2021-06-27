import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.4.32"
}

group = "ru.shipa"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

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
    val igniteVersion = "2.9.1"
    val junit5Version = "5.0.2"

    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.apache.ignite:ignite-core:$igniteVersion")
    implementation("org.apache.ignite:ignite-spring:$igniteVersion")
    implementation("org.apache.ignite:ignite-indexing:$igniteVersion")
    implementation("org.apache.ignite:ignite-kubernetes:$igniteVersion")
    implementation("org.apache.ignite:ignite-kafka:$igniteVersion")
    implementation("org.apache.ignite:ignite-log4j2:$igniteVersion")
    implementation("com.h2database:h2:1.4.197")
    implementation("javax.cache:cache-api:1.1.1")

    implementation("org.apache.kafka:kafka-clients:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("kotlin-test-junit"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
}
