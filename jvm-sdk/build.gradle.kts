group = "com.gamedatahub.sdk.jvm"
version = "1.0-SNAPSHOT"


dependencies {
    val jacksonVersion: String by project

    implementation(project(":shared-sdk"))

    // http client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Serialize
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}