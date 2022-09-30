plugins {
    id("java")
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}