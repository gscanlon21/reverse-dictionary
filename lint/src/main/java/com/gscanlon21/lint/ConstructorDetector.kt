package dev.ascallion.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass

@Suppress("UnstableApiUsage")
class ConstructorDetector : Detector(), SourceCodeScanner {
    override fun getApplicableMethodNames(): List<String>? = listOf("getInstance", "newInstance")
    override fun applicableSuperClasses(): List<String>? = listOf("androidx.fragment.app.Fragment")

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (true != declaration.constructors.singleOrNull { it.parameterList.parametersCount == 0 }?.modifierList?.hasModifierProperty("public")) {
            context.report(ISSUE_MISSING_PUBLIC_PARAMETERLESS_CONSTRUCTOR, declaration, context.getNameLocation(declaration), "Fragment must have a parameterless contructor")
        }
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        // TODO check return types and make sure they match
        val referenceClass = method.containingClass?.containingClass ?: return
        if (method.parameterList.parametersCount == 0) {
            context.report(ISSUE_REDUNDANT_PARAMETERLESS_CONSTRUCTOR, method, context.getLocation(method), "Constructor should be moved over to reference class's constructor implementation")
        } else if (method.parameterList.parametersCount > 0 && referenceClass.constructors.any { it.parameterList.parametersCount > 0 && it.modifierList.hasModifierProperty("public") }) {
            context.report(ISSUE_PUBLIC_CONSTRUCTOR_WITH_FACTORY_METHOD, method, context.getLocation(method), "A public constructor was found on a class with a ${method.name} method")
        }
    }

    companion object {
        val ISSUE_MISSING_PUBLIC_PARAMETERLESS_CONSTRUCTOR: Issue = Issue.create(
            id = "ISSUE_MISSING_PUBLIC_PARAMETERLESS_CONSTRUCTOR",
            briefDescription = "Any class that inherits from an Android Fragment must have a parameterless constructor",
            explanation = """
                Android will call the parameterless constructor internally when the fragment is instantiated
                Causing a runtime error if this constructor does not exist""",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(ConstructorDetector::class.java, Scope.JAVA_FILE_SCOPE)
        ).setAndroidSpecific(true)

        val ISSUE_REDUNDANT_PARAMETERLESS_CONSTRUCTOR: Issue = Issue.create(
            id = "ISSUE_REDUNDANT_PARAMETERLESS_CONSTRUCTOR",
            briefDescription = "This constructor is conflicting with the classes default parameterless constructor",
            explanation = """
                This constructor is redundant
                It should be replaced with a primary or secondary constructor""",
            category = Category.CORRECTNESS,
            priority = 1,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(ConstructorDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val ISSUE_PUBLIC_CONSTRUCTOR_WITH_FACTORY_METHOD: Issue = Issue.create(
            id = "ISSUE_PUBLIC_CONSTRUCTOR_WITH_FACTORY_METHOD",
            briefDescription = "A public constructor and factory method were both found on this class",
            explanation = """
                Both a public constructor and factory method may provide confusion over which one to use
                Switch to using only one""",
            category = Category.CORRECTNESS,
            priority = 3,
            severity = Severity.WARNING,
            implementation = Implementation(ConstructorDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
