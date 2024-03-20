import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// shadow jar documentation: https://anyroad.dev/posts/2022/gradle-plugin-shadow-jar/

plugins {
	id("com.github.johnrengelman.shadow") version "7.1.0"
	id("org.springframework.boot") version "2.7.0"
	// id("org.springframework.boot") version "2.3.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	id("org.flywaydb.flyway") version "9.3.0"
	application
}

group = "com.jason"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}
val restAssuredVersion: String by project

// shadow jar
val shadowImplementation by configurations.creating
configurations["compileOnly"].extendsFrom(shadowImplementation)
configurations["testImplementation"].extendsFrom(shadowImplementation)

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation ("com.github.kittinunf.fuel:fuel:2.3.1")
//	implementation ("com.google.code.gson:gson:2.9.0")
	implementation ("com.google.code.gson:gson:2.8.5")
	implementation ("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.0")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
	testImplementation("io.rest-assured", "rest-assured", restAssuredVersion)
	testImplementation("io.rest-assured", "kotlin-extensions", restAssuredVersion)
	testImplementation("io.rest-assured", "json-path", restAssuredVersion)
	testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	testImplementation("io.zonky.test:embedded-database-spring-test:2.1.2")
	compileOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.flywaydb:flyway-core") //run migrations on startup
	implementation("javax.persistence:javax.persistence-api")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(kotlin("script-runtime"))
	shadow("com.plaid:plaid-java:8.2.0")
	implementation("com.plaid:plaid-java:20.1.0")
	implementation(files("$buildDir/libs/plaid-shadow-0.0.1-SNAPSHOT-all.jar"))
}
tasks.shadowJar{
	relocate("com.plaid.client", "com.shadowed.plaid.client")
	from(project.configurations.shadow.map { project.files(it) })
	// Set the archive name to match the expected in the `run` task configuration
	archiveBaseName.set("plaid-shadow")
	archiveClassifier.set("all") // Optional, for distinguishing the shadowed JAR
}

application {
	mainClass.set("com.jason.kotlinplayground.KotlinPlaygroundApplication")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
		jvmTarget = "13"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.build {
	dependsOn(tasks.shadowJar)
}

//db migration
//flyway {
//	url = ""
//	user = ""
//	password = ""
//	schemas = arrayOf()
//	placeholders = mapOf()
//}
