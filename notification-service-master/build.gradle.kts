import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("com.google.cloud.tools.jib") version "3.3.1"
    id("maven-publish")
    id("org.jmailen.kotlinter") version "3.10.0"
    jacoco
}

group = "com.zlrx.thesis"
version = project.findProperty("version")!!
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["springBootAdminVersion"] = "3.0.0-M4"
extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("de.codecentric:spring-boot-admin-starter-client")

    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
    implementation("io.opentelemetry:opentelemetry-exporter-common")


    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:3.0.3")

    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")

    implementation("com.chrylis:base58-codec:1.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
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
    finalizedBy(tasks.jacocoTestReport)
}

kotlinter {
    ignoreFailures = true
    disabledRules = arrayOf("import-ordering", "filename")
}

tasks.compileKotlin { dependsOn(tasks.lintKotlin) }
tasks.lintKotlin { dependsOn(tasks.formatKotlin) }

jib {
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
        image = "zalerix/thesis-notification-service"
        tags = setOf("latest", "${project.version}")
        auth {
            username = System.getenv("DOCKER_USER")
            password = System.getenv("DOCKER_PASSWORD")
        }
    }
    container {
        mainClass = "com.zlrx.thesis.notificationservice.NotificationServiceApplicationKt"
        ports = listOf("8000/tcp", "9000/tcp")
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

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it).apply {
                        exclude(
                            "com/zlrx/thesis/**/config/**",
                            "com/zlrx/thesis/**/filters/**",
                            "com/zlrx/thesis/**/migration/**",
                            "org/springframework/**"
                        )
                    }
                }
            )
        )
    }
}


