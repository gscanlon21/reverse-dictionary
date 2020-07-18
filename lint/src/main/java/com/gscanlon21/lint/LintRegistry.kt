package com.gscanlon21.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage", "unused")
class LintRegistry : IssueRegistry() {
    override val api: Int = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(
            ConstructorDetector.ISSUE_MISSING_PUBLIC_PARAMETERLESS_CONSTRUCTOR,
            ConstructorDetector.ISSUE_PUBLIC_CONSTRUCTOR_WITH_FACTORY_METHOD,
            ConstructorDetector.ISSUE_REDUNDANT_PARAMETERLESS_CONSTRUCTOR,
            LayoutSpacingDetector.ISSUE_LAYOUT_SPACING_UNIT
        )
}