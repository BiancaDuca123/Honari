import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

val ktlintVersion = "1.3.1"
val ktlintConfig = mapOf(
    "max_line_length" to "100",
    "ktlint_code_style" to "android_studio",
    "ij_kotlin_imports_layout" to "*,java.**,javax.**,kotlin.**,^",
    "ktlint_standard_function-naming" to "disabled",
    "indent_size" to "4",
    "indent_style" to "space",
    "insert_final_newline" to "true",
    "ktlint_standard_no-wildcard-imports" to "enabled",
)

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(ktlintVersion).editorConfigOverride(ktlintConfig)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**/*.kts")
        ktlint(ktlintVersion).editorConfigOverride(ktlintConfig)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
    parallel = true
    source.setFrom(
        files(
            "app/src/main/kotlin",
            "app/src/main/java",
        )
    )
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks.register("staticAnalysis") {
    group = "verification"
    description = "Runs Spotless and Detekt checks without tests"
    dependsOn("spotlessCheck", "detekt")
}
