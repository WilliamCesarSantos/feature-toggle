dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.cloud:spring-cloud-bus")

    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}