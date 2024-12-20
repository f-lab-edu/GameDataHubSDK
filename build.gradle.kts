plugins {
    kotlin("jvm") version "2.0.21"
}


subprojects {
    group = "com.gamedatahub.sdk"
    version = "1.0-SNAPSHOT"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        testImplementation(kotlin("test"))
    }
    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(17)
    }

    repositories {
        mavenCentral()
    }
}

