// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.jacoco:org.jacoco.core:0.8.12")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "kevinzamoraa_Temporis-Final_Project-DAM")
        property("sonar.projectName", "Temporis-Final_Project-DAM")
        property("sonar.organization", "kevinzamoraa")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
    }
}

// Inyección con rutas absolutas basadas en la ubicación del subproyecto
subprojects {
    if (name == "app") {
        extensions.configure<org.sonarqube.gradle.SonarExtension> {
            properties {
                // Forzamos a Sonar a usar la ruta absoluta del módulo :app
                property("sonar.sources", "${project.projectDir}/src/main/java")
                property("sonar.tests", "${project.projectDir}/src/test/java")
                property("sonar.java.binaries", "${project.projectDir}/build/tmp/kotlin-classes/debug")
                property("sonar.kotlin.binaries", "${project.projectDir}/build/tmp/kotlin-classes/debug")
                property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/build/reports/jacoco/testDebugUnitTestCoverageReport/testDebugUnitTestCoverageReport.xml")
            }
        }
    }
}