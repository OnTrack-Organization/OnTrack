import org.springframework.boot.gradle.tasks.run.BootRun
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.kotlinJpa)
    alias(libs.plugins.liquibase)
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

buildscript {
    dependencies {
        classpath(libs.liquibase.core)
    }
}

dependencies {
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterValidation)
    implementation(libs.kotlinReflect)
    implementation(libs.firebase.admin)
    developmentOnly(libs.springBootDevtools)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.kotlinTestJunit5)
    testImplementation(libs.springSecurityTest)
    testRuntimeOnly(libs.junitPlatformLauncher)

    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime(libs.liquibase.hibernate)
    liquibaseRuntime(sourceSets.main.get().output)
    liquibaseRuntime(libs.springBootStarterDataJpa)
    liquibaseRuntime(libs.kotlinReflect)
}

val commonArgs = mapOf(
    "url" to "jdbc:postgresql://db:5432/ontrack",
    "username" to "default",
    "password" to "demo",
    "driver" to "org.postgresql.Driver",
)

val now = Date()
val timestamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(now)
val monthOfYear: String = SimpleDateFormat("yyyy-MM").format(now)
val changelogDir = "src/main/resources/db/changelog"

liquibase {
    activities.register("main") {
        this.arguments = commonArgs + mapOf(
            "changelogFile" to "$changelogDir/db.changelog-master.yaml",
            "count" to (System.getProperty("count") ?: "1")
        )
    }

    activities.register("diff") {
        this.arguments = commonArgs + mapOf(
            "changelogFile" to "$changelogDir/migrations/$monthOfYear/changelog-$timestamp.yaml",
            "referenceUrl" to "hibernate:spring:de.ashman.ontrack?dialect=org.hibernate.dialect.PostgreSQLDialect",
            "author" to (System.getProperty("author") ?: "generated"),
            //"hibernate.physical_naming_strategy" to "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy",
            //"hibernate.implicit_naming_strategy" to "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy",
        )
    }

    runList = System.getProperty("runList") ?: "main"
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
