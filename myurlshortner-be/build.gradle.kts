plugins {
    java
    alias(libs.plugins.quarkus)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(libs.quarkus.restjackson)
    implementation(libs.quarkus.arc)
    implementation(libs.vavr.core)
    implementation(libs.jspecify.core)
    testImplementation(libs.quarkus.junit)
    testImplementation(libs.quarkus.restassured)
}

group = "org.acme"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
