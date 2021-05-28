plugins {
    id("maven-publish")
    idea
    jacoco
    kotlin("jvm") version "1.4.20"
    id("com.github.fhermansson.assertj-generator") version "1.1.2"
    id("org.jmailen.kotlinter") version "3.1.0"
    id("pl.allegro.tech.build.axion-release") version "1.10.2"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.8"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.13"
}


group = "se.svt.oss"
project.version = scmVersion.version

apply {
    from("initial-version.gradle")
    from("checks.gradle")
}

tasks.test {
    useJUnitPlatform {
        if (project.hasProperty("runIntegrationTest")) {
            excludeTags("integrationTest")
        }
    }
}

kotlinter {
    disabledRules = arrayOf("import-ordering")
}

assertjGenerator {
    classOrPackageNames = arrayOf("se.svt.oss.mediaanalyzer", "org.apache.commons.math3.fraction")
    entryPointPackage = "se.svt.oss.mediaanalyzer"
}

repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")
    implementation("io.github.microutils:kotlin-logging:1.7.9")
    api("org.apache.commons:commons-math3:3.6.1")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "6.8.3"
}
