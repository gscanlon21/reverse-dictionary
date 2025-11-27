plugins {
    id("kotlin")
    id("java-library")
}

dependencies {
    compileOnly(kotlin("stdlib", Dependencies.KOTLIN_VERSION))

    compileOnly("com.android.tools.lint:lint-api:31.13.1")
    compileOnly("com.android.tools.lint:lint-checks:31.13.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.android.tools.lint:lint:31.13.1")
    testImplementation("com.android.tools.lint:lint-tests:31.13.1")
    testImplementation("com.android.tools:testutils:30.4.1")
}

tasks.jar {
    manifest {
        attributes(
            "Lint-Registry-v2" to "dev.ascallion.lint.LintRegistry"
        )
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}