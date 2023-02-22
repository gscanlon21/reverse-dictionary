package dev.ascallion.lint

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test


@Suppress("UnstableApiUsage")
class TestMethodNameDetectorTest {
    @Test
    fun testConstructorDetector() {
        lint().files(
            Stubs.FRAGMENT_STUB_KT,
            Stubs.CONSTRUCTOR_TEST_KT
        )
            .allowMissingSdk()
            .issues(
                ConstructorDetector.ISSUE_MISSING_PUBLIC_PARAMETERLESS_CONSTRUCTOR,
                ConstructorDetector.ISSUE_PUBLIC_CONSTRUCTOR_WITH_FACTORY_METHOD,
                ConstructorDetector.ISSUE_REDUNDANT_PARAMETERLESS_CONSTRUCTOR
            )
            .run()
            .expectErrorCount(2)
    }
}