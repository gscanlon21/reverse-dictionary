package dev.ascallion.lint

import com.android.SdkConstants
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LayoutDetector
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr

@Suppress("UnstableApiUsage")
class LayoutSpacingDetector : LayoutDetector() {
    override fun getApplicableAttributes(): Collection<String>? {
        return listOf(
            SdkConstants.ATTR_PADDING,
            SdkConstants.ATTR_PADDING_BOTTOM,
            SdkConstants.ATTR_PADDING_TOP,
            SdkConstants.ATTR_PADDING_START,
            SdkConstants.ATTR_PADDING_END,
            SdkConstants.ATTR_PADDING_LEFT,
            SdkConstants.ATTR_PADDING_RIGHT,
            SdkConstants.ATTR_PADDING_HORIZONTAL,
            SdkConstants.ATTR_PADDING_VERTICAL,
            SdkConstants.ATTR_LAYOUT_MARGIN,
            SdkConstants.ATTR_LAYOUT_MARGIN_BOTTOM,
            SdkConstants.ATTR_LAYOUT_MARGIN_TOP,
            SdkConstants.ATTR_LAYOUT_MARGIN_START,
            SdkConstants.ATTR_LAYOUT_MARGIN_END,
            SdkConstants.ATTR_LAYOUT_MARGIN_LEFT,
            SdkConstants.ATTR_LAYOUT_MARGIN_RIGHT,
            SdkConstants.ATTR_LAYOUT_MARGIN_HORIZONTAL,
            SdkConstants.ATTR_LAYOUT_MARGIN_VERTICAL,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_BOTTOM,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_TOP,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_START,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_END,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_LEFT,
            SdkConstants.ATTR_LAYOUT_GONE_MARGIN_RIGHT
        )
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.specified) {
            if (attribute.value.isBlank()) { return }
            if (!attribute.value.startsWith(SdkConstants.DIMEN_PREFIX) && !attribute.value.startsWith("0")) {
                context.report(
                    ISSUE_LAYOUT_SPACING_UNIT,
                    context.getLocation(attribute),
                    ISSUE_LAYOUT_SPACING_UNIT.getExplanation(TextFormat.TEXT)
                )
            }
        }
    }

    companion object {
        val ISSUE_LAYOUT_SPACING_UNIT: Issue = Issue.create(
            id = "ISSUE_LAYOUT_SPACING_UNIT",
            briefDescription = "Layout sizes should use @dimen resources",
            explanation = "Layout sizes should use @dimen resources",
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(LayoutSpacingDetector::class.java, Scope.ALL_RESOURCES_SCOPE)
        )
    }
}