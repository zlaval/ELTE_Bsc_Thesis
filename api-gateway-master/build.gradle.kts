import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    id("maven-publish")
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.zlrx.thesis"
version = project.findProperty("version")!!
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

val springCloudVersion: String by project
val springContextIndexer: String by project
val springBootAdminVersion: String by project

extra["springCloudVersion"] = springCloudVersion
extra["springBootAdminVersion"] = springBootAdminVersion

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    implementation("de.codecentric:spring-boot-admin-starter-client")

    implementation("io.micrometer:micrometer-registry-statsd")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
    implementation("io.opentelemetry:opentelemetry-exporter-common")

    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.github.microutils:kotlin-logging:3.0.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework:spring-context-indexer:$springContextIndexer")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib{
    from {
        image = "amazoncorretto:17.0.6"
        auth {
            username = System.getenv("DOCKER_USER")
            password = System.getenv("DOCKER_PASSWORD")
        }
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "zalerix/thesis-api-gateway"
        tags = setOf("latest", "${project.version}")
        auth {
            username = System.getenv("DOCKER_USER")
            password = System.getenv("DOCKER_PASSWORD")
        }
    }
    container {
        mainClass = "com.zlrx.thesis.apigateway.ApiGatewayApplicationKt"
        ports = listOf("8080/tcp", "9000/tcp")
        appRoot = "/app"
        workingDirectory = "/app"
        creationTime.set("USE_CURRENT_TIMESTAMP")
        jvmFlags = listOf(
            "-XX:InitialRAMPercentage=40.0",
            "-XX:MaxRAMPercentage=75.0",
            "-noverify",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+UseShenandoahGC"
        )
        args = listOf(
            "-Duser.timezone=UTC"
        )
        environment = mapOf(
            //"JAVA_TOOL_OPTIONS" to "-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n ",
            "JAVA_OPTS" to "--spring.config.location=classpath:/"
        )
    }
}

abstract class VersionPrintTask : DefaultTask() {
    @TaskAction
    fun printTask() {
        print("${project.version}")
    }
}

tasks.register<VersionPrintTask>("printVersion")