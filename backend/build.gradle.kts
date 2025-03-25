import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.kotlinJpa)
}

group = "de.ashman"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterWeb)
    implementation(libs.kotlinReflect)
    implementation(libs.firebase.admin)
    developmentOnly(libs.springBootDevtools)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.kotlinTestJunit5)
    testImplementation(libs.springSecurityTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<BootRun> {
    // This will attach the debug agent only to the application process.
    jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
