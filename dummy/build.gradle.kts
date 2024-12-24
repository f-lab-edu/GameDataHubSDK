dependencies {
    val jacksonVersion: String by project

    // HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // json
    implementation("com.google.code.gson:gson:2.10.1")

    // Serialize
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

}
