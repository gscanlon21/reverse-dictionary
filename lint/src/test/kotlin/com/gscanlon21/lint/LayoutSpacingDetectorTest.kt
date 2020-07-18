package com.gscanlon21.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Severity
import org.junit.Test


@Suppress("UnstableApiUsage")
class LayoutSpacingDetectorTest : LintDetectorTest() {
    @Test
    fun testLayoutSpacingDetector() {
        lint()
            .files(
                xml(
                    "/res/layout/test.xml",
                    """
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="0"
    android:paddingTop="121dp"
    android:paddingRight="4%"
    />
                    """.trimIndent()
                )
            ).run()
            .expectCount(2, Severity.INFORMATIONAL)
    }

    override fun getDetector() = LayoutSpacingDetector()
    override fun getIssues() = listOf(LayoutSpacingDetector.ISSUE_LAYOUT_SPACING_UNIT)
}