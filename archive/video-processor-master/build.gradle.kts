import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.0"
	id("io.spring.dependency-management") version "1.1.0"
	//id("org.graalvm.buildtools.native") version "0.9.16"
	id("com.google.cloud.tools.jib") version "3.3.1"
	id("maven-publish")
	id("org.jmailen.kotlinter") version "3.10.0"
	kotlin("jvm") version "1.7.20"
	kotlin("plugin.spring") version "1.7.20"
}

group = "com.zlrx.thesis"
version = project.findProperty("version")!!
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springBootAdminVersion"] = "3.0.0-M4"
extra["springCloudVersion"] = "2022.0.0-RC2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	implementation("org.springframework.cloud:spring-cloud-stream")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")

	implementation("de.codecentric:spring-boot-admin-starter-client")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("io.projectreactor.addons:reactor-extra")
	implementation("io.projectreactor.netty:reactor-netty:1.0.24")

	implementation("io.micrometer:micrometer-registry-statsd")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
	implementation("io.opentelemetry:opentelemetry-exporter-common")

	implementation("io.github.microutils:kotlin-logging:3.0.3")

	implementation("com.github.zakgof:velvet-video:0.5.2")
	implementation("com.github.zakgof:velvet-video-natives:0.2.8.full")

	implementation("com.chrylis:base58-codec:1.2.0")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")

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

jib {
	from {
		image = "amazoncorretto:17.0.5-alpine3.16"
		auth {
			username = System.getenv("DOCKER_USER")
			password = System.getenv("DOCKER_PASSWORD")
		}
	}
	to {
		image = "zalerix/thesis-video-processor"
		tags = setOf("latest", "${project.version}")
		auth {
			username = System.getenv("DOCKER_USER")
			password = System.getenv("DOCKER_PASSWORD")
		}
	}
	container {
		mainClass = "com.zlrx.thesis.videoprocessor.VideoProcessorApplicationKt"
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
		environment = mapOf(
			//"JAVA_TOOL_OPTIONS" to "-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n ",
			"JAVA_OPTS" to "--spring.config.location=classpath:/"
		)
	}
}

kotlinter {
	ignoreFailures = true
	disabledRules = arrayOf("import-ordering", "filename")
}

tasks.compileKotlin { dependsOn(tasks.lintKotlin) }
tasks.lintKotlin { dependsOn(tasks.formatKotlin) }
