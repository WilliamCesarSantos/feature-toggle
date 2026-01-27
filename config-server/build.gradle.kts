dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("software.amazon.awssdk:ssm:2.28.16")
    implementation("software.amazon.awssdk:sts:2.28.16")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}