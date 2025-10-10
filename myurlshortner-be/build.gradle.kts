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
    implementation(libs.quarkus.flyway)
    implementation(libs.quarkus.arc)
    implementation(libs.quarkus.kafka)
    implementation(libs.quarkus.apicurioregistry.avro)
    implementation(libs.quarkus.hibernate)
    implementation(libs.quarkus.jdbc.postgres)
    implementation(libs.vavr.core)
    implementation(libs.jspecify.core)
    implementation(libs.quarkus.health)
    implementation(libs.quarkus.image.docker)
    testImplementation(libs.quarkus.junit)
    testImplementation(libs.quarkus.junit.mockito)
    testImplementation(libs.quarkus.restassured)
}

group = "org.acme"
version = "1.0.2"

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

tasks.register("printVersion") {
    println(project.version)
}
