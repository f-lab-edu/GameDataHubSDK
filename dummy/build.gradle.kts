dependencies {
    val jacksonVersion: String by project

    implementation(project(":jvm-sdk"))
    implementation(project(":shared-sdk"))

    // HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Serialize
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Testing
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.11")
}
