plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("jacoco")
    // CAMBIO 1: Versión más estable para Android
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
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
}

// CONFIGURACIÓN UNIFICADA DE TEST (ESTA ES LA QUE FUNCIONA)
tasks.withType<Test>().configureEach {
    // 1. Configuración de la extensión de JaCoCo
    extensions.configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> {
        isIncludeNoLocationClasses = false
        // Excluimos absolutamente todos los paquetes del sistema y de librerías
        excludes = listOf(
            "jdk.internal.*",
            "android.*",
            "com.android.*",
            "org.robolectric.*",
            "androidx.*",
            "com.google.*",
            "net.bytebuddy.*",
            "org.mockito.*",
            "io.mockk.*"
        )
    }

    // 2. Forzamos un entorno limpio para cada test
    forkEvery = 1
    maxParallelForks = 1 // Bajamos a 1 para asegurar estabilidad total durante la suite completa

    // 3. Argumentos de la JVM (El secreto está en el orden de los filtros)
    jvmArgs(
        "-Xmx2g",
        "-Djacoco-agent.excludes=android.*:com.android.*:org.robolectric.*:androidx.*:com.google.*:jdk.internal.*",
        "-Drobolectric.logging.enabled=true"
    )
}

// Configuración para SonarQube
// CAMBIO 2: Configuración de SonarQube optimizada
sonar {
    properties {
        property("sonar.projectKey", "kevinzamoraa_Temporis-Final_Project-DAM")
        property("sonar.organization", "kevinzamoraa")
        property("sonar.host.url", "https://sonarcloud.io")

        // 1. Forzamos el análisis solo de este módulo
        property("sonar.projectName", "Temporis-AndroidApp")

        // 2. Definimos las fuentes como colecciones (listOf) para evitar el error de Casting
        property("sonar.sources", listOf("src/main/java"))
        property("sonar.tests", listOf("src/test/java"))

        // 3. Binarios (clases compiladas)
        property("sonar.java.binaries", listOf("build/tmp/kotlin-classes/debug"))

        // 4. Ruta del reporte de JaCoCo (Relativa al módulo app)
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/testDebugUnitTestCoverageReport/testDebugUnitTestCoverageReport.xml")

        // 5. Configuraciones de estabilidad
        property("sonar.gradle.skipCompile", "true")
        property("sonar.scm.disabled", "true")
        property("sonar.android.variant", "debug")
    }
}

// Tarea para generar el reporte XML de Jacoco
// CAMBIO 3: Tarea JaCoCo sin funciones obsoletas
tasks.register<JacocoReport>("testDebugUnitTestCoverageReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"

    val buildDir = layout.buildDirectory.get().asFile

    reports {
        xml.required.set(true)
        html.required.set(true)
        // Eliminamos project.buildDir por layout.buildDirectory
        xml.outputLocation.set(file("$buildDir/reports/jacoco/testDebugUnitTestCoverageReport/testDebugUnitTestCoverageReport.xml"))
    }

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/*Binding.*", "**/BR.*"
    )

    val kotlinTree = fileTree("$buildDir/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(files("${project.projectDir}/src/main/java"))
    classDirectories.setFrom(files(kotlinTree))

    executionData.setFrom(fileTree("$buildDir") {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec", "jacoco/testDebugUnitTest.exec")
    })
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-appcheck-debug")

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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("androidx.test.ext:junit:1.1.5")
}