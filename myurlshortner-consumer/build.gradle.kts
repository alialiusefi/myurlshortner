import org.apache.avro.tool.SpecificCompilerTool
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.acme.myurlshortner"
version = "1.0.0"
description = "MyUrlShortner Consumer"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:1.0.0-rc-1")
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.apicurio:apicurio-registry-serdes-avro-serde:3.0.0.M4")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-rc-1")
    implementation("org.jetbrains.exposed:exposed-java-time:1.0.0-rc-1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    //implementation("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.apache.avro:avro-tools:1.12.0")
    }
}

val avroCompiledDir = "build/avro-generated"
val avroCompile = tasks.register("avroCompile") {
    inputs.dir("src/main/avro")
    outputs.dir(avroCompiledDir)
    logging.captureStandardOutput(LogLevel.INFO)
    logging.captureStandardError(LogLevel.ERROR)
    doLast {
        SpecificCompilerTool().run(
            System.`in`, System.out, System.err, listOf(
                "-encoding", "UTF-8",
                "-string",
                "-fieldVisibility", "private",
                "-noSetters",
                "schema", "$projectDir/src/main/avro".toString(), "$projectDir/$avroCompiledDir".toString()
            )
        )
    }
}

java.sourceSets["main"].java {
    srcDir(avroCompiledDir)
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(avroCompile)
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName = "alialiusefi/myurlshortner-consumer:${project.version}"
    buildpacks = listOf("paketobuildpacks/amazon-corretto:9", "urn:cnb:builder:paketo-buildpacks/java")
}

tasks.register("printVersion") {
    println(project.version)
}
