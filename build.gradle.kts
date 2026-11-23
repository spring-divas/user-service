plugins {
    java
    checkstyle
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

val lombokVersion = "1.18.48"
val mapstructVersion = "1.6.3"
val lombokMapstructBindingVersion = "0.2.0"
val postgresqlVersion = "42.7.8"
val h2Version = "2.3.232"
val logbackVersion = "9.0"
val checkstyleVersion = "13.7.0"
val chaosMonkeyVersion = "4.0.0"
val resilience4jVersion = "2.4.0"
val pactProviderVersion = "4.7.5"

group = "org.spring.divas"
version = "0.0.1-SNAPSHOT"
description = "user-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("de.codecentric:chaos-monkey-spring-boot:$chaosMonkeyVersion")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("io.github.resilience4j:resilience4j-spring-boot4:$resilience4jVersion")

    implementation("net.logstash.logback:logstash-logback-encoder:$logbackVersion")

    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("au.com.dius.pact.provider:junit5:$pactProviderVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("com.h2database:h2:$h2Version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

checkstyle {
    toolVersion = checkstyleVersion
    configFile = file("checkstyle/checks.xml")
    maxWarnings = 0
}

tasks.withType<Test> {
    useJUnitPlatform()
    val pactFolder = System.getProperty(
        "pactfolder.path",
        project.file("../common/contract/pact").absolutePath
    )
    inputs.dir(pactFolder)
    systemProperty("pactfolder.path", pactFolder)
}
