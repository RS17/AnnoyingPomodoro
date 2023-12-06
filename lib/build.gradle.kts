// Do not pollute this build file with Android stuff, will cause problems.
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("multiplatform") version "1.7.22"
    `java-library`
}

version = "7.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val jvmMain by getting
    }
}
