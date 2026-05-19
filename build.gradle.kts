plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.diffplug.spotless")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(rootProject.files(".config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        parallel = true
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt", "**/presentation/theme/*.kt")
            ktlint("1.2.1")
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_filename" to "disabled",
                        "ktlint_standard_function-naming" to "disabled",
                    ),
                )
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            ktlint("1.2.1")
        }
    }
}
