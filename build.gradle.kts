plugins {
    kotlin("multiplatform") version "1.5.10"
}

group = "com.drjcoding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
