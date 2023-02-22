plugins {
    id("kotlin")
    id("java-library")
}

dependencies {
    compileOnly(kotlin("stdlib", Dependencies.KOTLIN_VERSION))

    compileOnly("com.android.tools.lint:lint-api:27.1.3")
    compileOnly("com.android.tools.lint:lint-checks:27.1.3")

    testImplementation("junit:junit:4.13.1")
    testImplementation("com.android.tools.lint:lint:27.1.3")
    testImplementation("com.android.tools.lint:lint-tests:27.1.3")
    testImplementation("com.android.tools:testutils:27.1.3")
}

tasks.jar {
    manifest {
        attributes(
            "Lint-Registry-v2" to "dev.ascallion.lint.LintRegistry"
        )
    }
}