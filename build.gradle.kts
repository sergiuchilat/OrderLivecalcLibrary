import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
}

group = "com.merax"
version = "1.0.2"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }

}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.12")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
