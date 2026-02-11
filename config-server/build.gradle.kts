dependencies {
    // Config Server
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-config-monitor")

    // Spring Cloud Stream com Kafka Binder
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")

    // Spring Cloud Bus com Kafka
    implementation("org.springframework.cloud:spring-cloud-bus")

    // AWS Spring Cloud (para Parameter Store)
    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}