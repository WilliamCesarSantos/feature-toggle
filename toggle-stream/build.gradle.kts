
dependencies {
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("software.amazon.awssdk:sns:2.28.16")
    implementation("software.amazon.awssdk:sqs:2.28.16")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
