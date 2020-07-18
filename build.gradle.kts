// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.3.72")) // Kotlin Gradle plugin
        classpath("com.google.gms:google-services:4.3.3") // Google Services Gradle plugin
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.2.0") // Crashlytics Gradle plugin
        classpath("org.jmailen.gradle:kotlinter-gradle:2.3.2") // ktlint
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}