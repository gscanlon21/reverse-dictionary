package com.gscanlon21.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest

@Suppress("UnstableApiUsage")
object Stubs {
    val CONSTRUCTOR_TEST_KT = LintDetectorTest.kotlin(
        "com/gscanlon21/lint/Test.kt",
        """
package com.gscanlon21.lint

class PublicConstructorWithInstanceWithParams {
    init {
        getInstance(1)
    }
    companion object {
        fun getInstance(one: Int) = PublicConstructorWithInstanceWithParams()
    }
}


class PublicConstructorWithInstanceNoParams {
    init {
        newInstance()
    }
    companion object {
        fun newInstance() = PublicConstructorWithInstanceNoParams()
    }
}

class PrivateConstructorWithInstanceNoParams private constructor() {
    init {
        newInstance(1)
    }
    companion object {
        fun newInstance(one: Int) = PublicConstructorWithInstanceNoParams()
    }
}

class MyFrag private constructor() : androidx.fragment.app.Fragment {
    init {
        MyFrag()
    }
}
"""
    ).indented().within("src")!!


    val FRAGMENT_STUB_KT = LintDetectorTest.kotlin(
        "androidx/fragment/app/Fragment.kt",
        """
package androidx.fragment.app
interface Fragment
"""
    ).indented().within("src")!!
}