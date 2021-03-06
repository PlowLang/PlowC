plugins {
    kotlin("multiplatform") version "1.6.20"
}

group = "com.drjcoding.plowc"
version = "0.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

val mainClassName = "MainKt"

kotlin {

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        val jvmJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
            doFirst {
                manifest {
                    attributes["Main-Class"] = mainClassName
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.drjcoding.plow:Plow:0.0.0")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("com.squareup.okio:okio:3.1.0")
                implementation ("com.github.ajalt.clikt:clikt:3.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.drjcoding.plow:Plow:0.0.0")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
            }
        }
    }
}

task("runOnJVM", JavaExec::class) {
    group = "run"
    mainClass.set(mainClassName)
    classpath = configurations["jvmRuntimeClasspath"] + kotlin.targets["jvm"].compilations["main"].output.allOutputs
}

kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    binaries.all {
        freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
    }
}
