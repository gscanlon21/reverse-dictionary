import com.android.build.gradle.internal.cxx.json.readJsonFile
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt") // Room db annotation processing
    id("com.google.gms.google-services") // Google Services Gradle plugin
    id("com.google.firebase.crashlytics") // Crashlytics Gradle plugin
    id("org.jmailen.kotlinter") //ktlint
    id("com.github.triplet.play") version "2.8.0" // Release Management
}

data class ManifestFile (val version: String)
android {
    testOptions.unitTests.isIncludeAndroidResources = true
    sourceSets {
        named("test") {
            assets.srcDir(File("src/main/assets"))
        }
    }
    compileSdkVersion(30)
    buildToolsVersion("30.0.0")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
    kotlinter {
        disabledRules = arrayOf("filename") // Disables _class_ should be declared in a file named _class.kt_
    }
    defaultConfig {
        applicationId = "com.gscanlon21.reversedictionary"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
        versionName = readJsonFile(file("manifest.json"), ManifestFile::class.java).version
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("int", "MIN_SDK_VERSION", "${minSdkVersion!!.apiLevel}")
        buildConfigField("int", "TARGET_SDK_TEST_VERSION", "28") // Robolectric doesn't support v29 w/ JAVA_1_8
    }
    signingConfigs {
        register("release") {
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            storeFile = file("../sub/release/reverse-dictionary.jks")
        }
    }
    buildTypes {
        named("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        named("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    tasks.withType(JavaCompile::class) {
        dependsOn(tasks.withType(FormatTask::class))
    }

    applicationVariants.all {
        val lintTask = tasks["lint${name.capitalize()}"]
        assembleProvider?.get()?.dependsOn?.add(lintTask)
    }
}

play {
    resolutionStrategy = "auto"
    defaultToAppBundles = true
    if (System.getenv("ANDROID_PUBLISHER_CREDENTIALS") == null) {
        isEnabled = false
    }
}

dependencies {
    lintChecks(project(":lint"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Material Styles
    implementation("com.google.android.material:material:1.3.0-alpha02")

    // Kotlin Extensions
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")

    // User Preference
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference-ktx:1.1.1")

    // Actively developed version of ViewPager
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Network requests
    implementation("com.android.volley:volley:1.1.1")

    // Room database
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5") // Coroutines support
    implementation("androidx.room:room-ktx:2.2.5") // Kotlin Extensions

    // Crash Reporting -- Firebase SDK for Crashlytics
    implementation("com.google.firebase:firebase-crashlytics:17.2.1")

    debugImplementation("androidx.fragment:fragment-testing:1.2.5")

    // Instrumented Tests
    androidTestImplementation("androidx.test:core:1.2.0") // Core library
    androidTestImplementation("androidx.test:core-ktx:1.2.0") // Core kotlin extensions
    androidTestImplementation("androidx.test:runner:1.2.0") // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:rules:1.2.0") // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.ext:junit:1.1.1") // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.1") // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.2.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.4") // Coroutines test support
    androidTestImplementation("io.mockk:mockk-android:1.10.0") // Mocking


    // Local Unit Tests
    testImplementation("androidx.test:core:1.2.0") // Core library
    testImplementation("androidx.test:core-ktx:1.2.0") // Core kotlin extensions
    testImplementation("androidx.test:runner:1.2.0") // AndroidJUnitRunner and JUnit Rules
    testImplementation("androidx.test:rules:1.2.0") // AndroidJUnitRunner and JUnit Rules
    testImplementation("androidx.test.ext:junit:1.1.1") // AndroidJUnitRunner and JUnit Rules
    testImplementation("androidx.test.ext:junit-ktx:1.1.1") // AndroidJUnitRunner and JUnit Rules
    testImplementation("androidx.test.espresso:espresso-core:3.2.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.robolectric:robolectric:4.3.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.4") // Coroutines test support
    testImplementation("com.jraska.livedata:testing-ktx:1.1.2") // LiveData testing
    testImplementation("io.mockk:mockk:1.10.0") // Mocking

    implementation("androidx.test.espresso:espresso-idling-resource:3.2.0")
}
