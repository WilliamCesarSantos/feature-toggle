
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("software.amazon.awssdk:sns:2.28.16")
    implementation("software.amazon.awssdk:sqs:2.28.16")

    implementation("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
