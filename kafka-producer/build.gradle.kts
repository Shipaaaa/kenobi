import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

group = "ru.shipa"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "ru.shipa.kenobi.kafka.producer.KafkaProducerApp"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "ru.shipa.kenobi.kafka.producer.KafkaProducerApp")
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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
    val junit5Version = "5.0.2"

    implementation(project(":core"))

    implementation("org.apache.kafka:kafka-clients:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
}
