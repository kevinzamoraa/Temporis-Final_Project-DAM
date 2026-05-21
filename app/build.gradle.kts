plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("jacoco")
    // Usamos la versión oficial estable indexada globalmente
    id("org.sonarqube") version "4.4.1.3373"
}

android {
    namespace = "com.kevinzamora.temporis_androidapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kevinzamora.temporis_androidapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            output.outputFileName = "Temporis_v${versionName}.apk"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperty("robolectric.offline", "false")
                it.systemProperty("robolectric.dependency.repo.url", "https://repo1.maven.org/maven2/")
                it.maxHeapSize = "1024m"
            }
        }
    }
}

// =============================================================================
// CONFIGURACIÓN UNIFICADA DE JACOCO Y TESTING (OPTIMIZADA)
// =============================================================================

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test>().configureEach {
    extensions.configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> {
        isIncludeNoLocationClasses = false
        excludes = listOf(
            "jdk.internal.*", "android.*", "com.android.*",
            "org.robolectric.*", "androidx.*", "com.google.*",
            "net.bytebuddy.*", "org.mockito.*", "io.mockk.*"
        )
    }

    forkEvery = 0
    maxParallelForks = 1

    jvmArgs(
        "-Xmx2g",
        "-Djacoco-agent.excludes=android.*:com.android.*:org.robolectric.*:androidx.*:com.google.*:jdk.internal.*",
        "-Drobolectric.logging.enabled=true",
        "-Drobolectric.scandir=none"
    )
}

tasks.register<JacocoReport>("testDebugUnitTestCoverageReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Genera el reporte de cobertura de JaCoCo para la variante Debug."

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/testDebugUnitTestCoverageReport/testDebugUnitTestCoverageReport.xml"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*\$Lambda\$*.*",
        "**/*Companion*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*Hilt*.*",
        "**/*_Factory*.*",
        "**/*_MembersInjector*.*"
    )

    val debugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

dependencies {
    // Firebase - Entorno de la Aplicación (Forzamos versiones fijas estables con Kotlin 1.9)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics") {
        exclude(group = "com.google.android.gms", module = "play-services-measurement-api")
    }
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Firebase - Entorno de Tests Unitarios
    testImplementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    testImplementation("com.google.firebase:firebase-auth:23.0.0")
    testImplementation("com.google.firebase:firebase-firestore:25.0.0")

    // UI & Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation & Lifecycle
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")

    // Biometrics & Permissions
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("com.karumi:dexter:6.2.3")

    // Images & Charts
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("io.coil-kt:coil:2.6.0")

    // Google Sign-In & Ads
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")

    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}