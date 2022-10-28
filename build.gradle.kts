plugins {
    id("java")
    id("org.gradle.test-retry").version("1.4.1")
}

group = "name.sargon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    implementation(platform("org.junit:junit-bom:5.9.0"))
    implementation("org.junit.platform:junit-platform-engine")
    implementation("net.objecthunter:exp4j:0.4.8")

    testImplementation("org.junit.platform:junit-platform-testkit")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform {
        includeEngines("expression-test-engine", "junit-jupiter")
    }

    retry {
        maxRetries.set(1)
    }
}
