import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.0"
	// id("org.springframework.boot") version "2.3.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.jason"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}
val restAssuredVersion: String by project
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation ("com.github.kittinunf.fuel:fuel:2.3.1")
	implementation ("com.google.code.gson:gson:2.9.0")
	implementation ("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.0")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
	testImplementation("io.rest-assured", "rest-assured", restAssuredVersion)
	testImplementation("io.rest-assured", "kotlin-extensions", restAssuredVersion)
	testImplementation("io.rest-assured", "json-path", restAssuredVersion)
	testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "13"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
