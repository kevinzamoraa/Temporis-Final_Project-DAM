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

        // Mapeos y exclusiones globales unificados
        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java")
        property("sonar.java.binaries", "app/build/tmp/kotlin-classes/debug")
        property("sonar.kotlin.binaries", "app/build/tmp/kotlin-classes/debug")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/testDebugUnitTestCoverageReport/testDebugUnitTestCoverageReport.xml")
    }
}